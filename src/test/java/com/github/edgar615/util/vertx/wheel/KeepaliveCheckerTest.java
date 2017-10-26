package com.github.edgar615.util.vertx.wheel;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    Set<Integer> first = new HashSet<>();
    Set<Integer> dis = new HashSet<>();

    vertx.eventBus().<JsonObject>consumer(options.getDisConnAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      dis.addAll(removed.body().getJsonArray("ids").getList());
    });
    vertx.eventBus().<JsonObject>consumer(options.getFirstConnAddress(), added -> {
      System.out.println(System.currentTimeMillis() + ",added:" + added.body());
      first.add(added.body().getInteger("id"));
    });
    KeepaliveChecker checker = new KeepaliveCheckerImpl(vertx, options);

    checker.add(1);
    checker.add(2);

    testContext.assertEquals(2, checker.size());

    TimeUnit.SECONDS.sleep(1);

    //１秒后仍然有２个在线
    testContext.assertEquals(2, checker.size());

    TimeUnit.SECONDS.sleep(3);
    checker.add(1);
    testContext.assertEquals(2, checker.size());

    TimeUnit.SECONDS.sleep(2);
    testContext.assertEquals(1, checker.size());

    testContext.assertEquals(2, first.size());
    testContext.assertEquals(1, dis.size());
  }


//  @Test
//  public void testKeepalive2(TestContext testContext) throws InterruptedException {
//    KeepaliveOptions options = new KeepaliveOptions()
//            .setInterval(5);
//
//    Set<Integer> first = new HashSet<>();
//    Set<Integer> dis = new HashSet<>();
//
//    vertx.eventBus().<JsonObject>consumer(options.getDisConnAddress(), removed -> {
//      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
//      dis.addAll(removed.body().getJsonArray("ids").getList());
//    });
//    vertx.eventBus().<JsonObject>consumer(options.getFirstConnAddress(), added -> {
//      System.out.println(System.currentTimeMillis() + ",added:" + added.body());
//      first.add(added.body().getInteger("id"));
//    });
//    KeepaliveCheckerImpl checker = new KeepaliveCheckerImpl(vertx, options);
//
//    for (int i = 0; i < 10; i++) {
//      checker.add(i);
//    }
//
//    testContext.assertEquals(10, checker.size());
//
//    TimeUnit.SECONDS.sleep(3);
//
//    testContext.assertEquals(10, first.size());
//    testContext.assertEquals(0, dis.size());
//
//    testContext.assertEquals(10, checker.size());
//    for (int i = 4; i < 8; i++) {
//      checker.add(i);
//    }
//
//    TimeUnit.SECONDS.sleep(3);
//    testContext.assertEquals(6, dis.size());
//    testContext.assertEquals(4, checker.size());
//
//    TimeUnit.SECONDS.sleep(3);
//
//    testContext.assertEquals(0, checker.size());
//    testContext.assertEquals(10, dis.size());
//
//  }

}
