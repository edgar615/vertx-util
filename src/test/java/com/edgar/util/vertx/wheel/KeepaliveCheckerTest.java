package com.edgar.util.vertx.wheel;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by edgar on 17-3-17.
 */
@RunWith(VertxUnitRunner.class)
public class KeepaliveCheckerTest {

  Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @Test
  public void testKeepalive(TestContext testContext) throws InterruptedException {
    KeepaliveOptions options = new KeepaliveOptions()
        .setInterval(5);
    Set<Integer> online = new HashSet<>();

    vertx.eventBus().<JsonObject>consumer(options.getDisConnAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      online.removeAll(removed.body().getJsonArray("ids").getList());
    });
    vertx.eventBus().<JsonObject>consumer(options.getFirstConnAddress(), added -> {
      System.out.println(System.currentTimeMillis() + ",added:" + added.body());
      online.add(added.body().getInteger("id"));
    });
    KeepaliveCheckerImpl checker = new KeepaliveCheckerImpl(vertx, options);

    Async async = testContext.async();


    checker.heartbeat(1);
    checker.heartbeat(2);

    vertx.setTimer(1000, l -> {
      System.out.println("after 1s:online:" + online);
      testContext.assertEquals(2, online.size());
    });

    vertx.setTimer(5100, l -> {
      System.out.println("after 5s:line:" + online);
      checker.heartbeat(1);
      System.out.println("after 5s:line:" + online);
//      testContext.assertEquals(1, online.size());
    });

    vertx.setTimer(13000, l -> {
      System.out.println("after 10s:line:" + online);
      testContext.assertEquals(0, online.size());
      async.complete();
    });
  }

}
