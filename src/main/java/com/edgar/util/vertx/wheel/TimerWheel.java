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
public class TimerWheel {

  private final Vertx vertx;

  /**
   * 环形队列
   */
//  private final List<Integer> wheelQueue = new ArrayList<>();

  private final int interval;

  private final AtomicInteger cursor = new AtomicInteger(0);

  private final Map<Integer, Integer> location = new ConcurrentHashMap<>();

  /**
   * 环形队列
   */
  private final Map<Integer, Set<Integer>> wheelQueue = new ConcurrentHashMap<>();

  public TimerWheel(Vertx vertx, int interval) {
    this.vertx = vertx;
    this.interval = interval;
    for (int i = 0; i <= interval; i++) {
      wheelQueue.put(i, new CopyOnWriteArraySet<>());
    }
    vertx.setPeriodic(1000, l -> {
      int removedSolt = cursor.incrementAndGet() - 1;
//      int removedSolt = cursor.incrementAndGet() % interval - 1;
      if (removedSolt == interval) {
        cursor.set(0);
      }
      if (removedSolt < 0) {
        removedSolt = interval;
      }
      Set<Integer> oldList = wheelQueue.put(removedSolt, new CopyOnWriteArraySet<>());
      System.out.println(removedSolt + " : " + wheelQueue);
      oldList.forEach(i -> location.remove(i));
      if (oldList.size() > 0) {
        vertx.eventBus().publish("remove", new JsonObject().put("ids", new JsonArray(new ArrayList(oldList)))
            .put("solt", removedSolt));
      }
    });
  }

  public void registerEvent(Integer eventId) {
    if (location.containsKey(eventId)) {
      int currentSolt = location.get(eventId);
      Set<Integer> list = wheelQueue.get(currentSolt);
      list.remove(eventId);
    }
    int solt = cursor.get() - 1;
    if (solt < 0) {
      solt = interval;
    }
    Set<Integer> list = wheelQueue.get(solt);
    list.add(eventId);
    location.put(eventId, solt);
    vertx.eventBus().publish("add", new JsonObject().put("id", eventId).put("solt", solt));
    ;
  }
}
