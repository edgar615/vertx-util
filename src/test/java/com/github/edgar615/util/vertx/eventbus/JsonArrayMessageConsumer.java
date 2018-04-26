package com.github.edgar615.util.vertx.eventbus;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2018/4/26.
 *
 * @author Edgar  Date 2018/4/26
 */
public class JsonArrayMessageConsumer implements Handler<Message<JsonArray>> {

  private final Vertx vertx;

  public JsonArrayMessageConsumer(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonArray> msg) {
    System.out.println(msg.body());
    msg.reply(msg.body());
  }

}
