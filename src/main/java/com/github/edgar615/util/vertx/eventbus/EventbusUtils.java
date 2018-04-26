package com.github.edgar615.util.vertx.eventbus;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Edgar on 2018/4/26.
 *
 * @author Edgar  Date 2018/4/26
 */
public class EventbusUtils {

  public static void registerConsumer(Vertx vertx, JsonArray config) {
    for (int i = 0; i < config.size(); i++) {
      if (config.getValue(i) instanceof JsonObject) {
        JsonObject conf = config.getJsonObject(i);
        String address = conf.getString("address");
        String handleClass = conf.getString("class");
        Objects.requireNonNull(address);
        Objects.requireNonNull(handleClass);
        JsonObject handleConfig = conf.getJsonObject("config", new JsonObject());
        new ConsumerBinder(vertx).setAddress(address)
                .register(handleClass, handleConfig);
      }
    }
  }
}
