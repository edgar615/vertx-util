package com.github.edgar615.util.vertx.wheel;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Assert;
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
public class TimeWheelTest {

  Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @Test
  public void testPeriod(TestContext testContext) throws InterruptedException {
    TimerWheelOptions options = new TimerWheelOptions()
            .setInterval(5);
    TimeWheelImpl timeWheel = new TimeWheelImpl(vertx, options);
    List<Integer> exeIds = new ArrayList<>();
    vertx.eventBus().<JsonObject>consumer(options.getAnnounceAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      exeIds.addAll(removed.body().getJsonArray("tasks").getList());
    });
    System.out.println(System.currentTimeMillis());
    timeWheel.addTask(1, 4);
//    timeWheel.addTask(2, 4);
//    timeWheel.addTask(3, 3);
//    timeWheel.addTask(4, 2);
//    timeWheel.addTask(4, 1);
    timeWheel.addTask(5, 1, 3);
    TimeUnit.SECONDS.sleep(11);

    testContext.assertEquals(4, exeIds.size());
    Assert.assertEquals(1, exeIds.get(1), 0);
  }

  @Test
  public void testDelay(TestContext testContext) throws InterruptedException {
    TimerWheelOptions options = new TimerWheelOptions()
            .setInterval(5);
    TimeWheelImpl timeWheel = new TimeWheelImpl(vertx, options);
    List<Integer> exeIds = new ArrayList<>();
    vertx.eventBus().<JsonObject>consumer(options.getAnnounceAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      exeIds.addAll(removed.body().getJsonArray("tasks").getList());
    });
    System.out.println(System.currentTimeMillis());
    timeWheel.addTask(1, 7);
    timeWheel.addTask(2, 4);
    timeWheel.addTask(3, 3);
    timeWheel.addTask(4, 2);
    timeWheel.addTask(4, 1);
    TimeUnit.SECONDS.sleep(11);

    testContext.assertEquals(4, exeIds.size());
    Assert.assertEquals(1, exeIds.get(3), 0);
  }

  @Test
  public void testDelayAt(TestContext testContext) throws InterruptedException {
    TimerWheelOptions options = new TimerWheelOptions()
            .setInterval(5);
    TimeWheelImpl timeWheel = new TimeWheelImpl(vertx, options);
    List<Integer> exeIds = new ArrayList<>();
    vertx.eventBus().<JsonObject>consumer(options.getAnnounceAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      exeIds.addAll(removed.body().getJsonArray("tasks").getList());
    });
    System.out.println(System.currentTimeMillis());
    timeWheel.addTaskAt(1, 5 + Instant.now().getEpochSecond());
    timeWheel.addTaskAt(2, 4 + Instant.now().getEpochSecond());
    timeWheel.addTaskAt(3, 3 + Instant.now().getEpochSecond());
    timeWheel.addTaskAt(4, 2 + Instant.now().getEpochSecond());
    timeWheel.addTaskAt(4, 1 + Instant.now().getEpochSecond());
    TimeUnit.SECONDS.sleep(11);

    testContext.assertEquals(4, exeIds.size());
    Assert.assertEquals(1, exeIds.get(3), 0);
  }

  @Test
  public void testPeriodAt(TestContext testContext) throws InterruptedException {
    TimerWheelOptions options = new TimerWheelOptions()
            .setInterval(5);
    TimeWheelImpl timeWheel = new TimeWheelImpl(vertx, options);
    List<Integer> exeIds = new ArrayList<>();
    vertx.eventBus().<JsonObject>consumer(options.getAnnounceAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      exeIds.addAll(removed.body().getJsonArray("tasks").getList());
    });
    System.out.println(System.currentTimeMillis());
    timeWheel.addTaskAt(1, 4 + Instant.now().getEpochSecond());
    timeWheel.addTaskAt(5, 1 + Instant.now().getEpochSecond(), 3);
    TimeUnit.SECONDS.sleep(11);

    testContext.assertEquals(4, exeIds.size());
    Assert.assertEquals(1, exeIds.get(1), 0);
  }

  @Test
  public void testRemove(TestContext testContext) throws InterruptedException {
    TimerWheelOptions options = new TimerWheelOptions()
            .setInterval(5);
    TimeWheelImpl timeWheel = new TimeWheelImpl(vertx, options);
    List<Integer> exeIds = new ArrayList<>();
    vertx.eventBus().<JsonObject>consumer(options.getAnnounceAddress(), removed -> {
      System.out.println(System.currentTimeMillis() + ",removed:" + removed.body());
      exeIds.addAll(removed.body().getJsonArray("tasks").getList());
    });
    System.out.println(System.currentTimeMillis());
    timeWheel.addTask(1, 7);
    timeWheel.addTask(2, 4);
    timeWheel.addTask(3, 3);
    timeWheel.addTask(4, 2);
    timeWheel.addTask(4, 1);

    TimeUnit.SECONDS.sleep(2);
    timeWheel.remove(1);
    TimeUnit.SECONDS.sleep(11);

    testContext.assertEquals(3, exeIds.size());
    Assert.assertEquals(2, exeIds.get(2), 0);
  }

}
