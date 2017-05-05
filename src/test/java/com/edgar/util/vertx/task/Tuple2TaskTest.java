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

/**
 * Created by Edgar on 2016/5/6.
 *
 * @author Edgar  Date 2016/5/6
 */
@RunWith(VertxUnitRunner.class)
public class Tuple2TaskTest {

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
            .listen(9000);
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
    future1.complete("Hello World");
    Task.par(future1, future2)
            .map((s, i) -> s.length() + i)
            .map(length -> {
              context.assertEquals("Hello World".length() + 10, length);
              async.complete();
              return length;
            });
    future2.complete(10);
  }

  @Test
  public void testAndThen(TestContext context) {
    AtomicInteger seq = new AtomicInteger();
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    future1.complete("Hello World");
    Task.par(future1, future2)
            .andThen((s, i) -> seq.incrementAndGet())
            .map((s, i) -> s.length() + i)
            .andThen(length -> {
              context.assertEquals("Hello World".length() + 10, length);
              context.assertEquals(seq.get(), 1);
              async.complete();
            });
    future2.complete(10);
  }

  @Test
  public void testFlatMap(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    future1.complete("Hello World");

    Task.par(future1, future2)
            .map((s, i) -> s.length() + i)
            .flatMap(new FutureFunction(vertx))
            .andThen(length -> {
              System.out.println(length);
              context.assertEquals("Hello World".length() * 4, length);
              async.complete();
            }).onFailure(throwable -> throwable.printStackTrace());
    future2.complete("Hello World".length());
  }

  @Test
  public void testFlat2(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    future1.complete("Hello World");
    Task.par(future1, future2)
            .map((s, i) -> Integer.parseInt(s) + i)
            .flatMap(integer -> {
              Future<Integer> rFuture = Future.future();
              rFuture.complete(integer / 0);
              return rFuture;
            })
            .andThen(length -> {
              context.fail();
            }).onFailure(throwable -> {
      context.assertNotNull(throwable);
      async.complete();
    });
    future2.complete(10);
  }

  @Test
  public void testFailure(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    future1.complete("Hello World");
    Task.par(future1, future2)
            .map((s, i) -> Integer.parseInt(s) + i)
            .andThen(length -> {
              async.isFailed();
            })
            .onFailure(throwable -> {
              context.assertNotNull(throwable);
              async.complete();
            });
    future2.complete(10);
  }

  @Test
  public void testMapFallback(TestContext context) {
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    future1.complete("Hello World");
    Task.par(future1, future2)
            .mapWithFallback((s, i) -> Integer.parseInt(s) + i, (t, s, i) -> s.length() + i)
            .andThen(length -> {
              context.assertEquals(length, 21);
            })
            .onFailure(throwable -> {
              context.assertNotNull(throwable);
//                    async.complete();
              context.fail();
            })
            .andThen(i -> {
              context.assertEquals(i, 21);
              async.complete();
            });
    future2.complete(10);
  }

}
