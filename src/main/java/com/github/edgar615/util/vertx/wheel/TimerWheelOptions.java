package com.github.edgar615.util.vertx.wheel;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Created by edgar on 17-3-19.
 */
@DataObject(generateConverter = true)
public class TimerWheelOptions {

  public static final String DEFAULT_TIMER_ANNOUNCE_ADDRESS = "com.github.edgar615.timerwheel.announce";

  public static final String DEFAULT_TIMER_CANCEL_ADDRESS = "com.github.edgar615.timerwheel.cancel";

  public static final int DEFAULT_INTERVAL = 3600;

  /**
   * 执行任务事件
   */
  private String announceAddress = DEFAULT_TIMER_ANNOUNCE_ADDRESS;

  /**
   * 取消任务
   */
  private String cancelAddress = DEFAULT_TIMER_CANCEL_ADDRESS;


  /**
   * 事件轮的桶的大小
   */
  private int interval = DEFAULT_INTERVAL;

  public TimerWheelOptions() {
  }

  public TimerWheelOptions(JsonObject json) {
    this();
    TimerWheelOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    TimerWheelOptionsConverter.toJson(this, json);
    return json;
  }

  public String getAnnounceAddress() {
    return announceAddress;
  }

  public TimerWheelOptions setAnnounceAddress(String announceAddress) {
    this.announceAddress = announceAddress;
    return this;
  }

  public String getCancelAddress() {
    return cancelAddress;
  }

  public TimerWheelOptions setCancelAddress(String cancelAddress) {
    this.cancelAddress = cancelAddress;
    return this;
  }

  public int getInterval() {
    return interval;
  }

  public TimerWheelOptions setInterval(int interval) {
    this.interval = interval;
    return this;
  }
}
