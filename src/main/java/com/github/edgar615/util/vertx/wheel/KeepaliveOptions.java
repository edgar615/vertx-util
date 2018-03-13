package com.github.edgar615.util.vertx.wheel;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Created by edgar on 17-3-19.
 */
@DataObject(generateConverter = true)
public class KeepaliveOptions {

  public static final String DEFAULT_DISCONN_ADDRESS
          = "__com.github.edgar615.keepalive.disconnected";

  public static final String DEFAULT_FIRST_CONN_ADDRESS
          = "__com.github.edgar615.keepalive.firstconnected";

  public static final int DEFAULT_INTERVAL = 30;

  public static final int DEFAULT_STEP = 1000;

  /**
   * 掉线事件
   */
  private String disConnAddress = DEFAULT_DISCONN_ADDRESS;

  /**
   * 初次上线事件
   */
  private String firstConnAddress = DEFAULT_FIRST_CONN_ADDRESS;

  /**
   * 事件轮的桶的大小,interval*checkPeriod就是掉线时间
   */
  private int interval = DEFAULT_INTERVAL;

  /**
   * 时间轮每次移动的时间间隔，毫秒数
   */
  private int step = DEFAULT_STEP;

  public int getStep() {
    return step;
  }

  public KeepaliveOptions() {
  }

  public KeepaliveOptions(JsonObject json) {
    this();
    KeepaliveOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    KeepaliveOptionsConverter.toJson(this, json);
    return json;
  }


  public KeepaliveOptions setStep(int step) {
    this.step = step;
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
