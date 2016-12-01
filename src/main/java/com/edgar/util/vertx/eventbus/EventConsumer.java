package com.edgar.util.vertx.eventbus;

import com.edgar.util.vertx.spi.Configurable;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public interface EventConsumer extends Handler<Message<JsonObject>>, Configurable {

  /**
   * @return consumer的类型
   */
  String type();

  String address();
}