package com.edgar.util.vertx.wheel;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 假设对事件做30秒的定时检测,创建一个环形队列从0到30表示30秒,每隔一秒在队列中移动一格,并使用一个指针记录移动对位置.
 * 每个队列存放一个集合,记录任务集合
 * +----+----+----+----+----+----+
 * | 0  | 1  | 2  | 3  |... | 30 |
 * +----+----+----+----+----+----+
 * ^
 * <p>
 * 如果在第一秒,指针停留在第0格,收到A和B的事件,将A和B记录在第30格中.
 * +----+----+----+----+----+----+
 * | 0  | 1  | 2  | 3  |... | 30 |
 * +----+----+----+----+----+----+
 * ^
 * --------------------------A B
 * <p>
 * 在第3秒，指针移动到第2格,如果收到A的事件,就需要将A从第30格中移动到第1格(指针的上一格)
 * +----+----+----+----+----+----+
 * | 0  | 1  | 2  | 3  |... | 30 |
 * +----+----+----+----+----+----+
 * ------------^
 * -------A-------------------B
 * <p>
 * 在30秒之后,指针会移动到第30格,如果B依然在第30格中,说明30秒之内没有收到B的事件,B已经掉线,移除A（第30格里的所有设备都需要移除）.
 * +----+----+----+----+----+----+
 * | 0  | 1  | 2  | 3  |... | 30 |
 * +----+----+----+----+----+----+
 * －－－－－－－－－－－－－－－－^
 * -------A-------------------
 * <p>
 * Created by edgar on 17-3-16.
 */
public class KeepaliveChecker {

  private final Vertx vertx;

  /**
   * 环形队列
   */
//  private final List<Integer> wheelQueue = new ArrayList<>();

  private final int interval;

  private final int period;

  private final AtomicInteger cursor = new AtomicInteger(0);

  private final Map<Integer, Integer> location = new ConcurrentHashMap<>();

  /**
   * 环形队列
   */
  private final Map<Integer, Set<Integer>> wheelQueue = new ConcurrentHashMap<>();

  private final String disConnAddress;
  private final String firstConnAddress;

  public KeepaliveChecker(Vertx vertx, KeepaliveOptions options) {
    this.vertx = vertx;
    this.interval = options.getInterval();
    this.disConnAddress = options.getDisConnAddress();
    this.firstConnAddress = options.getFirstConnAddress();
    this.period = options.getCheckPeriod();

    for (int i = 0; i <= interval; i++) {
      wheelQueue.put(i, new CopyOnWriteArraySet<>());
    }
    vertx.setPeriodic(period, l -> {
      Set<Integer> oldList = disconn();
      if (oldList.size() > 0) {
        vertx.eventBus().publish(disConnAddress, new JsonObject().put("ids", new JsonArray(new ArrayList(oldList))));
      }
    });
  }

  private synchronized Set<Integer> disconn() {
    int removedSolt = cursor.incrementAndGet() - 1;
//      int removedSolt = cursor.incrementAndGet() % interval - 1;
    if (removedSolt == interval) {
      cursor.set(0);
    }
    if (removedSolt < 0) {
      removedSolt = interval;
    }
    Set<Integer> oldList = wheelQueue.put(removedSolt, new CopyOnWriteArraySet<>());
    oldList.forEach(i -> location.remove(i));
    return oldList;
  }

  public synchronized void heartbeat(Integer id) {
    boolean firstCheck = registerHeartbeat(id);
    if (firstCheck) {
      vertx.eventBus().publish(firstConnAddress, new JsonObject().put("id", id));
    }
    ;
  }

  private synchronized boolean registerHeartbeat(Integer id) {
    boolean firstCheck = true;
    if (location.containsKey(id)) {
      int currentSolt = location.get(id);
      Set<Integer> list = wheelQueue.get(currentSolt);
      list.remove(id);
      firstCheck = false;
    }
    int solt = cursor.get() - 1;
    if (solt < 0) {
      solt = interval;
    }
    Set<Integer> list = wheelQueue.get(solt);
    list.add(id);
    location.put(id, solt);
    return firstCheck;
  }
}
