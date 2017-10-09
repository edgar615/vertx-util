package com.github.edgar615.util.vertx.base;

import com.github.edgar615.util.vertx.task.Task;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Edgar on 2017/9/12.
 *
 * @author Edgar  Date 2017/9/12
 */
@RunWith(VertxUnitRunner.class)
public class FuturesTest {

  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @Test
  public void testCompletableFuture(TestContext testContext) {
    Future<String> future = Future.future();
    CompletableFuture<String> completableFuture = Futures.toCompletableFuture(future);
    AtomicBoolean check1 = new AtomicBoolean();
    completableFuture.thenApply(s -> {
      Integer i = Integer.parseInt(s);
      check1.set(true);
      return i;
    })
            .exceptionally(r -> {
              testContext.fail();
              return null;
            });
    future.complete("1");
    Awaitility.await().until(() -> check1.get());
  }

  @Test
  public void testCompletableFutureFailed(TestContext testContext) {
    Future<String> future = Future.future();
    CompletableFuture<String> completableFuture = Futures.toCompletableFuture(future);
    AtomicBoolean check1 = new AtomicBoolean();
    completableFuture.thenApply(s -> {
      Integer i = Integer.parseInt(s);
      testContext.fail();
      return i;
    })
            .exceptionally(r -> {
              r.printStackTrace();
              check1.set(true);
              return null;
            });
    future.complete("a");
    Awaitility.await().until(() -> check1.get());
  }

  @Test
  public void testFuture(TestContext testContext) {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();
    Future<String> future = Futures.toFuture(completableFuture);
    Task<String> task = Task.create(future);

    AtomicBoolean check1 = new AtomicBoolean();
    task.map(s -> {
      Integer i = Integer.parseInt(s);
      check1.set(true);
      return i;
    })
            .onFailure(r -> {
              testContext.fail();
            });
    completableFuture.complete("1");
    Awaitility.await().until(() -> check1.get());
  }

  @Test
  public void testFutureFailure(TestContext testContext) {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();
    Future<String> future = Futures.toFuture(completableFuture);
    Task<String> task = Task.create(future);

    AtomicBoolean check1 = new AtomicBoolean();
    task.map(s -> {
      Integer i = Integer.parseInt(s);
      testContext.fail();
      return i;
    })
            .onFailure(r -> {
              check1.set(true);
            });
    completableFuture.complete("a");
    Awaitility.await().until(() -> check1.get());
  }

}
