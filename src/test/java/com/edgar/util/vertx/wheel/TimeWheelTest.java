//package com.edgar.util.vertx.wheel;
//
//import io.vertx.core.Vertx;
//import io.vertx.ext.unit.TestContext;
//import io.vertx.ext.unit.junit.VertxUnitRunner;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.time.Instant;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by edgar on 17-3-17.
// */
//@RunWith(VertxUnitRunner.class)
//public class TimeWheelTest {
//
//  Vertx vertx;
//
//  @Before
//  public void setUp() {
//    vertx = Vertx.vertx();
//  }
//
//  @Test
//  public void testTimer(TestContext testContext) throws InterruptedException {
//    TimeWheel timeWheel = new TimeWheel(vertx);
//    timeWheel.addTask(1, Instant.now().getEpochSecond() + 5);
//    TimeUnit.SECONDS.sleep(8);
//  }
//
//
//}
