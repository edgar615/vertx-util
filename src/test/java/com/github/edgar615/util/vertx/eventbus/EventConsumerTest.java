package com.github.edgar615.util.vertx.eventbus;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Edgar on 2018/4/26.
 *
 * @author Edgar  Date 2018/4/26
 */
@RunWith(VertxUnitRunner.class)
public class EventConsumerTest {

  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    JsonObject intConfig = new JsonObject()
            .put("address", "int.consumer")
            .put("class", "com.github.edgar615.util.vertx.eventbus.IntMessageConsumer");
    JsonObject stringConfig = new JsonObject()
            .put("address", "string.consumer")
            .put("class", "com.github.edgar615.util.vertx.eventbus.StringMessageConsumer");
    JsonObject jsonConfig = new JsonObject()
            .put("address", "json.consumer")
            .put("class", "com.github.edgar615.util.vertx.eventbus.JsonObjectMessageConsumer")
            .put("config", new JsonObject().put("foo", "bar"));
    JsonObject arrayConfig = new JsonObject()
            .put("address", "array.consumer")
            .put("class", "com.github.edgar615.util.vertx.eventbus.JsonArrayMessageConsumer");
    JsonArray consumerArray = new JsonArray().add(intConfig).add(stringConfig).add
            (jsonConfig).add(arrayConfig);
    EventbusUtils.registerConsumer(vertx , consumerArray);
  }

  @Test
  public void testInt(TestContext testContext) {
    Async async = testContext.async();
    vertx.eventBus().<Integer>send("int.consumer", 1, reply -> {
      if (reply.failed()) {
        testContext.fail();
        return;
      }
      Integer result = reply.result().body();
      testContext.assertEquals(2, result);
      async.complete();
    });
  }

  @Test
  public void testString(TestContext testContext) {
    Async async = testContext.async();
    vertx.eventBus().<String>send("string.consumer", "foo", reply -> {
      if (reply.failed()) {
        testContext.fail();
        return;
      }
      String result = reply.result().body();
      testContext.assertEquals("foo", result);
      async.complete();
    });
  }

  @Test
  public void testArray(TestContext testContext) {
    Async async = testContext.async();
    vertx.eventBus().<JsonArray>send("array.consumer", new JsonArray().add(1), reply -> {
      if (reply.failed()) {
        testContext.fail();
        return;
      }
      JsonArray result = reply.result().body();
      testContext.assertEquals(1, result.getInteger(0));
      async.complete();
    });
  }

  @Test
  public void testJson(TestContext testContext) {
    Async async = testContext.async();
    vertx.eventBus().<JsonObject>send("json.consumer", new JsonObject().put("a", 1), reply -> {
      if (reply.failed()) {
        testContext.fail();
        return;
      }
      JsonObject result = reply.result().body();
      System.out.println(result);
      testContext.assertEquals(2, result.size());
      async.complete();
    });
  }
}
