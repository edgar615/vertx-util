package com.github.edgar615.util.vertx.eventbus;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2018/4/26.
 *
 * @author Edgar  Date 2018/4/26
 */
public class JsonObjectMessageConsumer implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  private final JsonObject config;

  public JsonObjectMessageConsumer(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
  }

  @Override
  public void handle(Message<JsonObject> msg) {
    System.out.println(msg.body());
    JsonObject jsonObject = new JsonObject()
            .put("config", config)
            .put("data", msg.body());
    msg.reply(jsonObject);
  }

}
