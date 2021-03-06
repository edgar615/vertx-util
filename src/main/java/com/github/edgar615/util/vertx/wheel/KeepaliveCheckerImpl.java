package com.github.edgar615.util.vertx.wheel;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
  private final Map<String, Integer> location = new ConcurrentHashMap<>();

  /**
   * 环形队列
   */
  private final Map<Integer, Set<String>> bucket = new ConcurrentHashMap<>();

  /**
   * 设备掉线的事件地址
   */
  private final String disConnAddress;

  /**
   * 设备第一次上线对事件地址
   */
  private final String firstConnAddress;

  private final long timer;

  KeepaliveCheckerImpl(Vertx vertx, KeepaliveOptions options) {
    this.vertx = vertx;
    this.interval = options.getInterval();
    this.disConnAddress = options.getDisConnAddress();
    this.firstConnAddress = options.getFirstConnAddress();
    this.period = options.getStep();

    //初始化时间轮
    for (int i = 0; i <= interval; i++) {
      bucket.put(i, new CopyOnWriteArraySet<>());
    }
    //周期性移动指针
    timer = vertx.setPeriodic(period, l -> {
      List<String> oldList = new ArrayList<String>(forward());
      if (oldList.size() > 0) {
        int bucket = oldList.size() / 10 + 1;
        for (int i = 0; i < bucket; i++) {
          //减少一次消息中发送的数量
          int start = i * 10;
          int end = Math.min(oldList.size(), i * 10 + 10);
          vertx.eventBus().publish(disConnAddress,
                                   new JsonObject()
                                           .put("ids", new JsonArray(oldList.subList(start, end)))
                                           .put("time", Instant.now().getEpochSecond()));
        }
      }
    });
  }

  /**
   * 游标向前移动一格
   *
   * @return
   */
  private synchronized Set<String> forward() {
    int expiredSolt = cursor
            .getAndAccumulate(1, (left, right) -> left + right > interval ? 0 : left + right);
    Set<String> disConnectedIds = bucket.put(expiredSolt, new CopyOnWriteArraySet<>());
    disConnectedIds.forEach(i -> location.remove(i));
    return disConnectedIds;
  }

  /**
   * 向桶中增加一个心跳．
   *
   * @param id
   * @return 如果是第一次添加（或者掉线后再次添加）返回true
   */
  private synchronized boolean addToBucket(String id) {
    int solt = cursor.get() - 1;
    if (solt < 0) {
      solt = interval;
    }
    Integer oldLocation = location.put(id, solt);
    if (oldLocation != null) {
      bucket.get(oldLocation).remove(id);
    }
    bucket.get(solt).add(id);

    return oldLocation == null;
  }

  @Override
  public void heartbeat(String id) {
    boolean firstCheck = addToBucket(id);
    if (firstCheck) {
      vertx.eventBus().publish(firstConnAddress,
                               new JsonObject().put("id", id));
    }
  }

  @Override
  public void remove(String id) {
    Integer solt = location.remove(id);
    if (solt == null) {
      return;
    }
    bucket.get(solt).remove(id);
  }

  @Override
  public int size() {
    return location.size();
  }

  @Override
  public boolean close() {
    return vertx.cancelTimer(timer);
  }
}
