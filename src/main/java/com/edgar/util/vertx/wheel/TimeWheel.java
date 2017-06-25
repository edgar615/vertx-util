package com.edgar.util.vertx.wheel;

import io.vertx.core.Vertx;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by edgar on 17-3-19.
 */
public class TimeWheel {

  /**
   * 事件轮的周期
   */
  private final int interval = 3600;

  /**
   * 每次检测的间隔时间
   */
  private final int period = 1000;

  private final AtomicInteger cursor = new AtomicInteger(0);

  /**
   * 环形队列
   */
  private final Map<Integer, Set<ScheduledTask>> bucket = new ConcurrentHashMap<>();

  /**
   * 记录任务在桶中的位置
   */
  private final Map<Integer, Integer> location = new ConcurrentHashMap<>();

  private final Vertx vertx;

  public TimeWheel(Vertx vertx, TimerWheelOptions options) {
    this.vertx = vertx;

    for (int i = 0; i <= interval; i++) {
      bucket.put(i, new CopyOnWriteArraySet<>());
    }
    vertx.setPeriodic(period, l -> {
      List<ScheduledTask> tasks = forward();
    });
  }

  /**
   * 游标向前移动一格
   * @return
   */
  private synchronized List<ScheduledTask> forward() {
    int expiredSolt = cursor
        .getAndAccumulate(1, (left, right) -> left + right > interval ? 0 : left + right);
    Set<ScheduledTask> tasks = bucket.get(expiredSolt);
    tasks.forEach(t -> t.dec());
    List<ScheduledTask> expiredTasks = tasks.stream()
        .filter(t -> t.cycleNum() < 0)
        .collect(Collectors.toList());
    tasks.removeIf(t -> t.cycleNum() < 0);
    return expiredTasks;
  }

  public void addTask(int id, long unixTimestamp) {
    long now = Instant.now().getEpochSecond();
    long delay = unixTimestamp - now;
    if (delay <= 0) {
      throw new IllegalArgumentException(unixTimestamp + " < " + now);
    } else {
      int cycle = (int) (delay / interval);
      int cursor = (int) (delay % interval);
      ScheduledTask task = new ScheduledTask(id, cycle);
      Set<ScheduledTask> tasks = bucket.get(cursor);
      tasks.add(task);
    }
  }

  public int size() {
    int size = 0;
    for (Map.Entry<Integer, Set<ScheduledTask>> tasks : bucket.entrySet()) {
      size += tasks.getValue().size();
    }
    return size;

  }


}
