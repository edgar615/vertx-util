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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Edgar on 2016/5/6.
 *
 * @author Edgar  Date 2016/5/6
 */
@RunWith(VertxUnitRunner.class)
public class ParTaskTest {

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
            .listen(8888);
  }

  @After
  public void shutdown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testMap(TestContext context) {
    Async async = context.async();
    Future<Integer> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    Task.par(Arrays.asList(future1, future2, future3))
            .map(result -> {
              int sum = 0;
              for (int i : result) {
                sum += i;
              }
              return sum;
            })
            .map(length -> {
              context.assertEquals(6, length);
              async.complete();
              return length;
            });
    future1.complete(1);
    future2.complete(2);
    future3.complete(3);
  }

  @Test
  public void testAndThen(TestContext context) {
    AtomicInteger seq = new AtomicInteger();
    Async async = context.async();
    Future<Integer> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    Task.par(Arrays.asList(future1, future2, future3))
            .andThen(result -> seq.incrementAndGet())
            .map(result -> {
              int sum = 0;
              for (int i : result) {
                sum += i;
              }
              return sum;
            })
            .andThen(length -> {
              context.assertEquals(6, length);
              context.assertEquals(seq.get(), 1);
              async.complete();
            });
    future1.complete(1);
    future2.complete(2);
    future3.complete(3);

  }

  @Test
  public void testFlatMap(TestContext context) {
    Async async = context.async();
    Future<Integer> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();
    Task.par(Arrays.asList(future1, future2, future3))
            .map(result -> {
              int sum = 0;
              for (int i : result) {
                sum += i;
              }
              return sum;
            })
            .flatMap(new FutureFunction(vertx, 8888))
            .andThen(length -> {
              System.out.println(length);
              context.assertEquals(12, length);
              async.complete();
            }).onFailure(throwable -> throwable.printStackTrace());
    future1.complete(1);
    future2.complete(2);
    future3.complete(3);
  }

  @Test
  public void testFailure(TestContext context) {
    Async async = context.async();
    Future<Integer> future1 = Future.future();
    Future<Integer> future2 = Future.future();
    Future<Integer> future3 = Future.future();

    Task.par(future1, future2, future3)
            .andThen(length -> {
              context.fail();
              async.isFailed();
            })
            .onFailure(throwable -> {
              context.assertNotNull(throwable);
              async.complete();
            });

    future1.complete(1);
    future2.complete(2);
    future3.fail(new RuntimeException());
  }

}
