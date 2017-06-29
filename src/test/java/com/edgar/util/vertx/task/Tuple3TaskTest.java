package com.edgar.util.vertx.task;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Created by Edgar on 2016/5/6.
 *
 * @author Edgar  Date 2016/5/6
 */
@RunWith(VertxUnitRunner.class)
public class Tuple3TaskTest {

  Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route(HttpMethod.GET, "/")
            .handler(rc -> {
              vertx.setTimer(2000, l -> rc.response().putHeader("content-type", "application/json")
                      .setStatusCode(200)
                      .end(new JsonObject().put("length", rc.request().getParam("length"))
                                   .encode()));
            });
    vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(9002);
  }

  @After
  public void shutdown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testMap(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    future1.complete("Hello World");
    Task.par(future1, future2, future3)
            .map((s, i1, i2) -> s.length() + i1 + i2)
            .map(length -> {
              context.assertEquals("Hello World".length() + 20, length);
              async.complete();
              return length;
            });
    future2.complete(10);
    future3.complete(10);
  }

  @Test
  public void testAndThen(TestContext context) {
    AtomicInteger seq = new AtomicInteger();
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    future1.complete("Hello World");
    Task.par(future1, future2, future3)
            .andThen((s, i1, i2) -> seq.incrementAndGet())
            .map((s, i1, i2) -> s.length() + i1 + i2)
            .andThen(length -> {
              context.assertEquals("Hello World".length() + 20, length);
              context.assertEquals(seq.get(), 1);
              async.complete();
            });
    future2.complete(10);
    future3.complete(10);
  }

  @Test
  public void testFlatMap(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    future1.complete("Hello World");

    Task.par(future1, future2, future3)
            .map((s, i1, i2) -> s.length() + i1 + i2)
            .flatMap(new FutureFunction(vertx, 9002))
            .andThen(length -> {
              System.out.println(length);
              context.assertEquals("Hello World".length() * 6, length);
              async.complete();
            }).onFailure(throwable -> throwable.printStackTrace());
    future2.complete("Hello World".length());
    future3.complete("Hello World".length());
  }

  @Test
  public void testFlat2(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    future1.complete("Hello World");

    Task.par(future1, future2, future3)
            .map((s, i1, i2) -> Integer.parseInt(s) + i1 + i2)
            .flatMap(new Function<Integer, Future<Integer>>() {
              @Override
              public Future<Integer> apply(Integer integer) {
                Future<Integer> rFuture = Future.future();
                rFuture.complete(integer / 0);
                return rFuture;
              }
            })
            .andThen(length -> {
              context.fail();
            }).onFailure(throwable -> {
      context.assertNotNull(throwable);
      async.complete();
    });
    future2.complete(10);
    future3.complete(10);
  }

  @Test
  public void testFailture(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    future1.complete("Hello World");

    Task.par(future1, future2, future3)
            .map((s, i1, i2) -> Integer.parseInt(s) + i1 + i2)
            .andThen(length -> {
              async.isFailed();
            })
            .onFailure(throwable -> {
              context.assertNotNull(throwable);
              async.complete();
            });
    future2.complete(10);
    future3.complete(10);
  }

  @Test
  public void testMapFallback(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    future1.complete("Hello World");

    Task.par(future1, future2, future3)
            .mapWithFallback((s, i1, i2) -> Integer.parseInt(s) + i1 + i2,
                             (t, s, i1, i2) -> s.length() + i1 + i2)
            .onFailure(throwable -> {
              context.fail();
            })
            .andThen(i -> {
              context.assertEquals(i, 31);
              async.complete();
            });
    future2.complete(10);
    future3.complete(10);
  }

}
