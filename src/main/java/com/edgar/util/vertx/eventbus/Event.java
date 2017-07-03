package com.edgar.util.vertx.eventbus;

import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2017/7/3.
 *
 * @author Edgar  Date 2017/7/3
 */
public class Event {

  private final JsonObject header;

  private final JsonObject body;

  Event(JsonObject header, JsonObject body) {
    this.header = header;
    this.body = body;
  }

  public static EventBuilder builder() {
    return new EventBuilder();
  }

  public JsonObject header() {
    return header;
  }

  public JsonObject body() {
    return body;
  }

  /**
   * @return 消息ID
   */
  public String id() {
    return header.getString("id");
  }

  /**
   * @return 消息版本
   */
  public int version() {
    return header.getInteger("version", 1);
  }

  /**
   * @return 消息活动
   */
  public String action() {
    return header.getString("action");
  }

  /**
   * @return 消息类型
   */
  public String type() {
    return header.getString("type", "message");
  }

  /**
   * @return 消息地址
   */
  public String address() {
    return header.getString("type", "address");
  }

  /**
   * @return 消息产生时间戳
   */
  public Long createdOn() {
    return header.getLong("created_on");
  }

  public boolean isReply() {
    return header.containsKey("reply_to");
  }

  public String replyTo() {
    return header.getString("reply_to");
  }
}
