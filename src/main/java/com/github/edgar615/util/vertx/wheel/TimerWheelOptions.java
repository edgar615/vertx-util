package com.github.edgar615.util.vertx.wheel;

/**
 * Created by edgar on 17-3-19.
 */
public class TimerWheelOptions {

  public static final String DEFAULT_TIMER_ADDED_ADDRESS = "com.edgar.timerwheel.added";

  public static final String DEFAULT_TIMER_DELETED_ADDRESS = "com.edgar.timerwheel.deleted";

  public static final String DEFAULT_TIMER_TRIGGERED_ADDRESS = "com.edgar.timerwheel.triggered";

  /**
   * 新增事件
   */
  private String addedAddress = DEFAULT_TIMER_ADDED_ADDRESS;

  /**
   * 删除事件
   */
  private String deletedAddress = DEFAULT_TIMER_DELETED_ADDRESS;

  /**
   * 触发事件
   */
  private String triggeredAddress = DEFAULT_TIMER_TRIGGERED_ADDRESS;

  public String getAddedAddress() {
    return addedAddress;
  }

  public TimerWheelOptions setAddedAddress(String addedAddress) {
    this.addedAddress = addedAddress;
    return this;
  }

  public String getDeletedAddress() {
    return deletedAddress;
  }

  public TimerWheelOptions setDeletedAddress(String deletedAddress) {
    this.deletedAddress = deletedAddress;
    return this;
  }

  public String getTriggeredAddress() {
    return triggeredAddress;
  }

  public TimerWheelOptions setTriggeredAddress(String triggeredAddress) {
    this.triggeredAddress = triggeredAddress;
    return this;
  }

}
