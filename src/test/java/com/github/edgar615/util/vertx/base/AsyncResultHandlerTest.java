package com.github.edgar615.util.vertx.base;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(VertxUnitRunner.class)
public class AsyncResultHandlerTest {
  private Vertx vertx;

  @Before
  public  void setUp() {
    vertx = Vertx.vertx();
  }

  @Test
  public void testSucceed(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    Future<String> future = Future.future();
    future.complete("hello");
    AsyncResultHandler.<String>create(r -> check.set(true))
            .otherwise(e -> testContext.fail()).bind(future);
    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testFailed(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    Future<String> future = Future.future();
    future.fail(new RuntimeException());
    AsyncResultHandler.<String>create(r -> testContext.fail())
            .otherwise(e -> check.set(true)).bind(future);
    Awaitility.await().until(() -> check.get());
  }
}
