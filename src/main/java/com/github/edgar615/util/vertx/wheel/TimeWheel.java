package com.github.edgar615.util.vertx.wheel;

import io.vertx.core.Vertx;

/**
 * Created by Edgar on 2017/10/26.
 *
 * @author Edgar  Date 2017/10/26
 */
public interface TimeWheel {
  void addTaskAt(int taskId, long unixTimestamp);

  void addTaskAt(int taskId, long unixTimestamp, long period);

  void addTask(int taskId, long delay);

  void addTask(int taskId, long delay, long period);

  void remove(int taskId);

  int size();

  boolean close();

  /**
   * 创建一个TimeWheel
   *
   * @param vertx
   * @param options
   * @return TimeWheel
   */
  static TimeWheel create(Vertx vertx, TimerWheelOptions options) {
    return new TimeWheelImpl(vertx, options);
  }
}
