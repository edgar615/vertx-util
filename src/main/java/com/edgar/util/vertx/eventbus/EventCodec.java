package com.edgar.util.vertx.eventbus;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.impl.Arguments;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2017/7/3.
 *
 * @author Edgar  Date 2017/7/3
 */
public class EventCodec implements MessageCodec<Event, Event> {
  @Override
  public void encodeToWire(Buffer buffer, Event event) {
    new JsonObject()
            .put("header", event.header())
            .put("body", event.body())
            .writeToBuffer(buffer);
  }

  @Override
  public Event decodeFromWire(int pos, Buffer buffer) {
    System.out.println(buffer.toString());
    int length = buffer.getInt(pos);
    pos += 4;
    byte[] encoded = buffer.getBytes(pos, pos + length);
    JsonObject jsonObject = Buffer.buffer(encoded).toJsonObject();
    Arguments.require(jsonObject.containsKey("header"), "event must contains head");
    Arguments.require(jsonObject.containsKey("body"), "event must contains body");
    Arguments.require(jsonObject.getValue("header") instanceof JsonObject, "header must be JSON");
    Arguments.require(jsonObject.getValue("body") instanceof JsonObject, "body must be JSON");

    JsonObject header = jsonObject.getJsonObject("header");
    Arguments.require(jsonObject.containsKey("id"), "head must contains id");
    Arguments.require(jsonObject.containsKey("address"), "head must contains address");
    Arguments.require(jsonObject.containsKey("version"), "head must contains version");
    Arguments.require(jsonObject.containsKey("type"), "head must contains type");
    Arguments.require(jsonObject.containsKey("created_on"), "head must contains created_on");

    return new Event(header, jsonObject.getJsonObject("body"));
  }

  @Override
  public Event transform(Event event) {
    return new Event(event.header().copy(), event.body().copy());
  }

  @Override
  public String name() {
    return EventCodec.class.getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return 100;
  }
}
