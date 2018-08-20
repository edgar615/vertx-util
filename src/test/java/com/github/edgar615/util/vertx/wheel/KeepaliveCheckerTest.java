package com.github.edgar615.util.vertx.wheel;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
  public void test() {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < 143; i ++) {
      list.add(i);
    }
    System.out.println(list);
    int len = 0;
    int bucket = list.size() / 10 + 1;
    for (int i = 0; i < bucket; i ++) {
      //减少一次消息中发送的数量
      int start = i * 10;
      int end = Math.min(list.size(), i * 10 + 10);
      List<Integer> newList = list.subList(start, end);
      len += newList.size();
      System.out.println(newList);
    }
    System.out.println(len);
  }

  @Test
  public void testKeepalive(TestContext testContext) throws InterruptedException {
    KeepaliveOptions options = new KeepaliveOptions()
            .setInterval(5);
    Set<String> first = new HashSet<>();
    Set<String> dis = new HashSet<>();

    vertx.eventBus().<JsonObject>consumer(options.getDisConnAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      dis.addAll(removed.body().getJsonArray("ids").getList());
    });
    vertx.eventBus().<JsonObject>consumer(options.getFirstConnAddress(), added -> {
      System.out.println(System.currentTimeMillis() + ",added:" + added.body());
      first.add(added.body().getString("id"));
    });
    KeepaliveChecker checker = new KeepaliveCheckerImpl(vertx, options);

    checker.heartbeat("1");
    checker.heartbeat("2");
//    vertx.eventBus().send(options.getHeartbeatAddress(), new JsonObject().put("id", "2"));

    TimeUnit.SECONDS.sleep(1);

    testContext.assertEquals(2, checker.size());

    TimeUnit.SECONDS.sleep(1);

    //１秒后仍然有２个在线
    testContext.assertEquals(2, checker.size());

    TimeUnit.SECONDS.sleep(3);
    checker.heartbeat("1");
    testContext.assertEquals(2, checker.size());

    TimeUnit.SECONDS.sleep(2);
    testContext.assertEquals(1, checker.size());

    testContext.assertEquals(2, first.size());
    testContext.assertEquals(1, dis.size());
  }


  @Test
  public void testKeepalive2(TestContext testContext) throws InterruptedException {
    KeepaliveOptions options = new KeepaliveOptions()
            .setInterval(5);

    Set<String> first = new HashSet<>();
    Set<String> dis = new HashSet<>();

    vertx.eventBus().<JsonObject>consumer(options.getDisConnAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      dis.addAll(removed.body().getJsonArray("ids").getList());
    });
    vertx.eventBus().<JsonObject>consumer(options.getFirstConnAddress(), added -> {
      System.out.println(System.currentTimeMillis() + ",added:" + added.body());
      first.add(added.body().getString("id"));
    });
    KeepaliveCheckerImpl checker = new KeepaliveCheckerImpl(vertx, options);

    for (int i = 0; i < 10; i++) {
      checker.heartbeat(i + "");
    }

    testContext.assertEquals(10, checker.size());

    TimeUnit.SECONDS.sleep(3);

    testContext.assertEquals(10, first.size());
    testContext.assertEquals(0, dis.size());

    testContext.assertEquals(10, checker.size());
    for (int i = 4; i < 8; i++) {
      checker.heartbeat(i + "");
    }

    TimeUnit.SECONDS.sleep(3);
    testContext.assertEquals(6, dis.size());
    testContext.assertEquals(4, checker.size());

    TimeUnit.SECONDS.sleep(3);

    testContext.assertEquals(0, checker.size());
    testContext.assertEquals(10, dis.size());

  }

  @Test
  public void testRemove(TestContext testContext) throws InterruptedException {
    KeepaliveOptions options = new KeepaliveOptions()
            .setInterval(5);
    List<String> first = new ArrayList<>();
    List<String> dis = new ArrayList<>();

    vertx.eventBus().<JsonObject>consumer(options.getDisConnAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      dis.addAll(removed.body().getJsonArray("ids").getList());
    });
    vertx.eventBus().<JsonObject>consumer(options.getFirstConnAddress(), added -> {
      System.out.println(System.currentTimeMillis() + ",added:" + added.body());
      first.add(added.body().getString("id"));
    });
    KeepaliveChecker checker = new KeepaliveCheckerImpl(vertx, options);

    checker.heartbeat("1");
    checker.heartbeat("2");
//    vertx.eventBus().send(options.getHeartbeatAddress(), new JsonObject().put("id", "2"));

    TimeUnit.SECONDS.sleep(1);

    testContext.assertEquals(2, checker.size());

    checker.remove("1");

    testContext.assertEquals(1, checker.size());

    TimeUnit.SECONDS.sleep(5);
    testContext.assertEquals(0, checker.size());

    checker.heartbeat("1");
    TimeUnit.SECONDS.sleep(1);
    testContext.assertEquals(1, checker.size());

    testContext.assertEquals(3, first.size());
    testContext.assertEquals(1, dis.size());
  }
}
