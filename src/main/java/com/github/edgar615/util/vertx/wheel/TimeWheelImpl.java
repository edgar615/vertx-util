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
import java.util.stream.Collectors;

/**
 * Created by edgar on 17-3-19.
 */
class TimeWheelImpl implements TimeWheel {

  /**
   * 事件轮的周期，一小时
   */
  private final int interval;

  /**
   * 每次检测的间隔时间，一秒
   */
  private final int period = 1000;

  //游标
  private final AtomicInteger cursor = new AtomicInteger(0);

  /**
   * 环形队列
   */
  private final Map<Integer, Set<ScheduledTask>> bucket = new ConcurrentHashMap<>();

  /**
   * 记录任务在桶中的位置
   */
  private final Map<Integer, Integer> location = new ConcurrentHashMap<>();

  private final long timer;

  private final Vertx vertx;

  /**
   * 设备掉线的事件地址
   */
  private final String announceAddress;

  TimeWheelImpl(Vertx vertx, TimerWheelOptions options) {
    this.vertx = vertx;
    this.interval = options.getInterval();

    //初始化时间轮
    for (int i = 0; i < interval; i++) {
      bucket.put(i, new CopyOnWriteArraySet<>());
    }
    this.announceAddress = options.getAnnounceAddress();
    timer = vertx.setPeriodic(period, l -> {
      List<ScheduledTask> tasks = forward();
      List<Integer> taskIds = tasks.stream()
              .map(t -> t.id())
              .collect(Collectors.toList());
      if (taskIds.size() > 0) {
        int bucket = taskIds.size() / 10 + 1;
        for (int i = 0; i < bucket; i ++) {
          //减少一次消息中发送的数量
          int start = i * 10;
          int end = Math.min(taskIds.size(), i * 10 + 10);
          vertx.eventBus().publish(announceAddress,
                  new JsonObject()
                          .put("tasks", new JsonArray(taskIds.subList(start, end)))
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
  private synchronized List<ScheduledTask> forward() {
    int expiredSolt = cursor
            .getAndAccumulate(1, (left, right) -> left + right >= interval ? 0 : left + right);
    Set<ScheduledTask> tasks = bucket.get(expiredSolt);
    tasks.forEach(t -> t.dec());
    List<ScheduledTask> expiredTasks = tasks.stream()
            .filter(t -> t.cycleNum() < 0)
            .collect(Collectors.toList());
    tasks.removeIf(t -> t.cycleNum() < 0);
    expiredTasks.forEach(t -> location.remove(t.id()));
    //周期性任务重新入队
    expiredTasks.stream().filter(t -> t.periodTask()).forEach(t -> {
      addTask(t.id(), t.period(), t.period());
    });
    return expiredTasks;
  }

  @Override
  public synchronized void addTaskAt(int taskId, long unixTimestamp) {
    addTask(taskId, unixTimestamp - Instant.now().getEpochSecond());
  }

  @Override
  public synchronized void addTaskAt(int taskId, long unixTimestamp, long period) {
    addTask(taskId, unixTimestamp - Instant.now().getEpochSecond(), period);
  }

  @Override
  public synchronized void addTask(int taskId, long delay) {
    if (delay <= 0) {
      throw new IllegalArgumentException(delay + " < 0");
    } else {
      int cycle = calCycle(delay);
      int solt = calSolt(delay);
      reLocate(taskId, solt);
      ScheduledTask task = new ScheduledTask(taskId, cycle);
      Set<ScheduledTask> tasks = bucket.get(solt);
      tasks.add(task);
    }
  }

  @Override
  public synchronized void addTask(int taskId, long delay, long period) {
    if (delay <= 0) {
      throw new IllegalArgumentException(delay + " < 0");
    } else {
      int cycle = calCycle(delay);
      int solt = calSolt(delay);
      reLocate(taskId, solt);
      ScheduledTask task = new ScheduledTask(taskId, cycle, period);
      Set<ScheduledTask> tasks = bucket.get(solt);
      tasks.add(task);
    }
  }

  @Override
  public int size() {
    int size = 0;
    for (Map.Entry<Integer, Set<ScheduledTask>> tasks : bucket.entrySet()) {
      size += tasks.getValue().size();
    }
    return size;
  }

  @Override
  public boolean close() {
    return vertx.cancelTimer(timer);
  }

  private int calSolt(long delay) {return (int) ((delay + cursor.get()) % interval);}

  private int calCycle(long delay) {return (int) (delay / interval);}

  private void reLocate(int taskId, int solt) {
    Integer oldLocaction = location.put(taskId, solt);
    if (oldLocaction != null) {
      bucket.get(oldLocaction).remove(new ScheduledTask(taskId, solt));
    }
  }

}
