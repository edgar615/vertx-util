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
public class Tuple4TaskTest {

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
    Future<Integer> future3 = Future.future();
    Future<Integer> future4 = Future.future();
    future1.complete("Hello World");
    Task.par(future1, future2, future3, future4)
            .map((s, i1, i2, i3) -> s.length() + i1 + i2 + i3)
            .map(length -> {
              context.assertEquals("Hello World".length() + 30, length);
              async.complete();
              return length;
            });
    future2.complete(10);
    future3.complete(10);
    future4.complete(10);
  }

  @Test
  public void testAndThen(TestContext context) {
    AtomicInteger seq = new AtomicInteger();
    Async async = context.async();
    Future<String> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    Future<Integer> future4 = Future.future();
    future1.complete("Hello World");
    Task.par(future1, future2, future3, future4)
            .andThen((s, i1, i2, i3) -> seq.incrementAndGet())
            .map((s, i1, i2, i3) -> s.length() + i1 + i2 + i3)
            .andThen(length -> {
              context.assertEquals("Hello World".length() + 30, length);
              context.assertEquals(seq.get(), 1);
              async.complete();
            });
    future2.complete(10);
    future3.complete(10);
    future4.complete(10);
  }

}
