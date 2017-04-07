package com.edgar.util.vertx.wheel;

import io.vertx.core.Vertx;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by edgar on 17-3-19.
 */
public class TimeWheel {

  private final AtomicInteger cursor = new AtomicInteger(0);

  /**
   * 环形队列
   */
  private final Map<Integer, Set<ScheduledTask>> queue = new ConcurrentHashMap<>();

//  private AtomicLong currentTime = new AtomicLong();

  private final Vertx vertx;

  public TimeWheel(Vertx vertx) {
    this.vertx = vertx;
    for (int i = 0; i <= 3600; i++) {
      queue.put(i, new CopyOnWriteArraySet<>());
    }
    vertx.setPeriodic(1000, l -> {
      int currentCursor = cursor.getAndIncrement();
      Set<ScheduledTask> tasks = queue.get(currentCursor);
      for (ScheduledTask task : tasks) {
        if (task.dec() == 0) {
          //定时时间到
          System.out.println(task);
        }
      }
    });
  }

  public void addTask(int id, long unixTimestamp) {
    long delay = unixTimestamp - Instant.now().getEpochSecond();
    if (delay < 0) {
      throw new IllegalArgumentException(unixTimestamp + " < " + id);
    } else if (delay == 0) {
      //run
    } else {
      int cycle = (int) (delay / 3600);
      int cursor = (int) (delay % 3600);
      ScheduledTask task = new ScheduledTask(id, cycle);
      Set<ScheduledTask> tasks = queue.get(cursor);
      tasks.add(task);
    }
  }

}
