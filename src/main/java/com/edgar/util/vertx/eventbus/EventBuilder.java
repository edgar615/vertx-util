package com.edgar.util.vertx.eventbus;

import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Edgar on 2017/7/3.
 *
 * @author Edgar  Date 2017/7/3
 */
public class EventBuilder {

  /**
   * 扩展头，用于增加额外的消息头
   */
  private final JsonObject ext = new JsonObject();

  /**
   * 消息ID
   */
  private String id = UUID.randomUUID().toString();

  /**
   * 消息版本
   */
  private int version = 1;

  /**
   * 消息地址
   */
  private String address;

  /**
   * 消息的活动，用于对一个消息做进一步细分
   */
  private String action;

  /**
   * 消息活动，用于区分不同的消息类型
   */
  private String type = "message";

  private String replyTo;

  private long createdOn = Instant.now().getEpochSecond();

  private JsonObject body;

  public Event build() {
    Objects.requireNonNull(body, "body cannot be null");
    JsonObject header = new JsonObject()
            .mergeIn(ext)
            .put("id", id)
            .put("address", address)
            .put("version", version)
            .put("type", type)
            .put("created_on", createdOn);
    if (action != null) {
      header.put("action", action);
    }
    if (replyTo != null) {
      header.put("reply_to", replyTo);
    }
    return new Event(header, body);
  }

  public EventBuilder setType(String type) {
    Objects.requireNonNull("type", "type cannot be null");
    this.type = type;
    return this;
  }

  public EventBuilder setAddress(String address) {
    Objects.requireNonNull("address", "address cannot be null");
    this.address = address;
    return this;
  }

  public EventBuilder setReplyTo(String replyTo) {
    Objects.requireNonNull("replyTo", "replyTo cannot be null");
    this.replyTo = replyTo;
    return this;
  }

  public EventBuilder setBody(JsonObject body) {
    Objects.requireNonNull("body", "body cannot be null");
    this.body = body;
    return this;
  }

  public EventBuilder setId(String id) {
    Objects.requireNonNull("id", "id cannot be null");
    this.id = id;
    return this;
  }

  public EventBuilder setVersion(int version) {
    this.version = version;
    return this;
  }

  public EventBuilder setAction(String action) {
    Objects.requireNonNull("action", "action cannot be null");
    this.action = action;
    return this;
  }

  public EventBuilder setCreatedOn(long createdOn) {
    this.createdOn = createdOn;
    return this;
  }

  public EventBuilder addExt(String name, Object value) {
    Objects.requireNonNull("name", "name cannot be null");
    Objects.requireNonNull("value", "value cannot be null");
    this.ext.put(name, value);
    return this;
  }
}
