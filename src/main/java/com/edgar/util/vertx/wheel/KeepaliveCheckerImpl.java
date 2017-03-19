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

  private final AtomicInteger seq = new AtomicInteger(0);

  private final String disConnAddress;
  private final String firstConnAddress;

  KeepaliveCheckerImpl(Vertx vertx, KeepaliveOptions options) {
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
        vertx.eventBus().publish(disConnAddress,
            new JsonObject().put("ids", new JsonArray(new ArrayList(oldList)))
                .put("seq", seq.get()));
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
    if (!oldList.isEmpty()) {
      seq.incrementAndGet();
    }
    return oldList;
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
    if (firstCheck) {
      seq.incrementAndGet();
    }
    return firstCheck;
  }
}
