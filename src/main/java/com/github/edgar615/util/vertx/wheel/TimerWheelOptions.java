package com.github.edgar615.util.vertx.wheel;

/**
 * Created by edgar on 17-3-19.
 */
public class TimerWheelOptions {

  public static final String DEFAULT_TIMER_ANNOUNCE_ADDRESS = "com.edgar.timerwheel.announce";

  public static final String DEFAULT_TIMER_CANCEL_ADDRESS = "com.edgar.timerwheel.cancel";

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
