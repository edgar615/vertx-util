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
 * Created by edgar on 17-3-19.
 */
class KeepaliveCheckerImpl implements KeepaliveChecker {

  private final Vertx vertx;

  /**
   * 多少次检测之后的心跳认为掉线
   */
  private final int interval;

  /**
   * 每次检测的间隔时间
   */
  private final int period;

  /**
   * 游标
   */
  private final AtomicInteger cursor = new AtomicInteger(0);

  /**
   * 记录心跳的在环形队列中的位置，每次收到心跳都需要更新心跳在环形队列中的位置，通过location可以很快定位到心跳在环形队列中的位置
   */
  private final Map<Integer, Integer> location = new ConcurrentHashMap<>();

  /**
   * 环形队列
   */
  private final Map<Integer, Set<Integer>> queue = new ConcurrentHashMap<>();

  /**
   * 序列号
   */
  private final AtomicInteger seq = new AtomicInteger(0);

  /**
   * 设备掉线的事件地址
   */
  private final String disConnAddress;

  /**
   * 设备第一次上线对事件地址
   */
  private final String firstConnAddress;

  KeepaliveCheckerImpl(Vertx vertx, KeepaliveOptions options) {
    this.vertx = vertx;
    this.interval = options.getInterval();
    this.disConnAddress = options.getDisConnAddress();
    this.firstConnAddress = options.getFirstConnAddress();
    this.period = options.getCheckPeriod();

    for (int i = 0; i <= interval; i++) {
      queue.put(i, new CopyOnWriteArraySet<>());
    }
    vertx.setPeriodic(period, l -> {
      forward();
      Set<Integer> oldList = disconn();
      if (oldList.size() > 0) {
        vertx.eventBus().publish(disConnAddress,
                                 new JsonObject().put("ids", new JsonArray(new ArrayList(oldList)))
                                         .put("seq", seq.get()));
      }
    });
  }

  private synchronized Set<Integer> disconn() {
    int deadSolt = deadSolt();
//      int removedSolt = cursor.incrementAndGet() % interval - 1;
    checkCycle();
    Set<Integer> oldList = queue.put(deadSolt, new CopyOnWriteArraySet<>());
    oldList.forEach(i -> location.remove(i));
    if (!oldList.isEmpty()) {
      seq.incrementAndGet();
    }
    return oldList;
  }

  private synchronized boolean registerHeartbeat(Integer id) {
    boolean firstCheck = true;
    if (location.containsKey(id)) {
      int currentSolt = location.get(id);
      Set<Integer> list = queue.get(currentSolt);
      list.remove(id);
      firstCheck = false;
    }
    int solt = cursor.get() - 1;
    if (solt < 0) {
      solt = interval;
    }
    Set<Integer> list = queue.get(solt);
    list.add(id);
    location.put(id, solt);
    if (firstCheck) {
      seq.incrementAndGet();
    }
    return firstCheck;
  }

  @Override
  public void heartbeat(Integer id) {
    boolean firstCheck = registerHeartbeat(id);
    if (firstCheck) {
      vertx.eventBus().publish(firstConnAddress,
                               new JsonObject().put("id", id)
                                       .put("seq", seq.get()));
    }
    ;
  }

  @Override
  public int counter() {
    return location.size();
  }

  private int deadSolt() {
    if (cursor.get() == 0) {
      return interval;
    }
    return cursor.get() - 1;
  }

  private void forward() {
    cursor.incrementAndGet();
  }

  private void checkCycle() {
    if (cursor.get() == interval + 1) {
      cursor.set(0);
    }
  }
}
