package com.github.edgar615.util.vertx.wheel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by edgar on 17-3-19.
 */
public class ScheduledTask {

  /**
   * 任务ID
   */
  private final int id;

  /**
   * 时间轮每走过一圈，cycelNum减1
   */
  private final AtomicInteger cycleNum;

  /**
   * 周期
   */
  private final long period;

  /**
   * 周期性任务标识
   */
  private boolean periodTask;

  public ScheduledTask(int id, int cycleNum) {
    this.id = id;
    this.cycleNum = new AtomicInteger(cycleNum);
    this.period = 0;
    this.periodTask = false;
  }

  public ScheduledTask(int id, int cycleNum, long period) {
    this.id = id;
    this.cycleNum = new AtomicInteger(cycleNum);
    this.period = period;
    this.periodTask = true;
  }

  public boolean periodTask() {
    return periodTask;
  }

  public long period() {
    return period;
  }

  public int id() {
    return id;
  }

  public int cycleNum() {
    return cycleNum.get();
  }

  public int dec() {
    return this.cycleNum.getAndDecrement();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ScheduledTask task = (ScheduledTask) o;

    if (id != task.id) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
