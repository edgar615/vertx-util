package com.edgar.util.vertx.wheel;

/**
 * Created by edgar on 17-3-19.
 */
public class KeepaliveOptions {

  public static final String DEFAULT_DISCONN_ADDRESS = "com.edgar.keepalive.disconn";

  public static final String DEFAULT_FIRST_CONN_ADDRESS = "com.edgar.keepalive.firstconn";

  public static final int DEFAULT_INTERVAL = 30;

  public static final int DEFAULT_CHECK_PERIOD = 1000;


  private String disConnAddress = DEFAULT_DISCONN_ADDRESS;

  private String firstConnAddress = DEFAULT_FIRST_CONN_ADDRESS;

  private int interval = DEFAULT_INTERVAL;

  private int checkPeriod = DEFAULT_CHECK_PERIOD;

  public int getCheckPeriod() {
    return checkPeriod;
  }

  public KeepaliveOptions setCheckPeriod(int checkPeriod) {
    this.checkPeriod = checkPeriod;
    return this;
  }

  public String getDisConnAddress() {
    return disConnAddress;
  }

  public KeepaliveOptions setDisConnAddress(String disConnAddress) {
    this.disConnAddress = disConnAddress;
    return this;
  }

  public String getFirstConnAddress() {
    return firstConnAddress;
  }

  public KeepaliveOptions setFirstConnAddress(String firstConnAddress) {
    this.firstConnAddress = firstConnAddress;
    return this;
  }

  public int getInterval() {
    return interval;
  }

  public KeepaliveOptions setInterval(int interval) {
    this.interval = interval;
    return this;
  }
}
