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
public class StringMessageConsumer implements Handler<Message<String>> {

  private final Vertx vertx;

  public StringMessageConsumer(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<String> msg) {
    String data = msg.body();
    System.out.println(msg.body());
    msg.reply(data);
  }

}
