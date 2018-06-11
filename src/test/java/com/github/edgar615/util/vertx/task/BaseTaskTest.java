package com.github.edgar615.util.vertx.task;

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
public class BaseTaskTest {

    Vertx vertx;

    int port;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
        port = 9001;
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
                .listen(port);
    }

    @After
    public void shutdown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testCreate(TestContext context) {
        Async async = context.async();
        Task<String> task = Task.create();
        task.complete("Hello World");
        task
                .map(s -> s.toUpperCase())
                .andThen(s -> context.assertEquals("HELLO WORLD", s))
                .map(s -> s.length())
                .andThen(length -> {
                    context.assertEquals("Hello World".length(), length);
                    async.complete();
                });
    }

    @Test
    public void testMap(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> s.toUpperCase())
                .andThen(s -> context.assertEquals("HELLO WORLD", s))
                .map(s -> s.length())
                .andThen(length -> {
                    context.assertEquals("Hello World".length(), length);
                    async.complete();
                });
    }

    @Test
    public void testMap2(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        Task<String> task = Task.create(future);
        task.map(s -> s.toUpperCase())
                .andThen(s -> context.assertEquals("HELLO WORLD", s))
                .map(s -> s.length())
                .andThen(length -> {
                    context.assertEquals("Hello World".length(), length);
                    async.complete();
                });
        task.complete("Hello World");
    }

    @Test
    public void testFailure(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> Integer.parseInt(s))
                .andThen(i -> context.fail())
                .onFailure(throwable -> {
//                    throwable.printStackTrace();
                    context.assertNotNull(throwable);
//                    async.complete();
                }).andThen(length -> context.fail())
                .onFailure(throwable -> {
                    context.assertNotNull(throwable);
                    async.complete();
                });
    }

    @Test
    public void testFailure2(TestContext context) {
        AtomicInteger seq = new AtomicInteger();
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> Integer.parseInt(s))
                .onFailure(throwable -> {
                    seq.incrementAndGet();
                })
                .andThen(i -> context.fail())
                .onFailure(throwable -> {
                    context.assertNotNull(throwable);
                    context.assertEquals(seq.incrementAndGet(), 2);
                    async.complete();
                });
    }

    @Test
    public void testFailure3(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> "" + s.length())
                .map(s -> Integer.parseInt(s))
                .andThen(i -> System.out.println(i))
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    context.fail();
//                    async.complete();
                }).andThen(length -> {
            context.assertEquals("Hello World".length(), length);
            async.complete();
        })
                .onFailure(throwable -> {
                    context.fail();
                });
    }

    @Test
    public void testMapFallback(TestContext context) {
        AtomicInteger seq = new AtomicInteger();
        Async async = context.async();
        Future<String> future = Future.future();
//        future.complete("Hello World");
        Task.create(future)
                .mapWithFallback(s -> Integer.parseInt(s), (throwable, s) -> s.length())
                .onFailure(throwable -> seq.incrementAndGet())
                .andThen(i -> {
                    context.assertEquals("Hello World".length(), i);
                    context.assertEquals(seq.get(), 0);
                    async.complete();
                })
                .onFailure(throwable -> {
                    context.fail();
                });
        future.complete("Hello World");
    }

    @Test
    public void testMapFallback2(TestContext context) {
        AtomicInteger seq = new AtomicInteger();
        Async async = context.async();
        Future<String> future = Future.future();
//        future.complete("Hello World");
        Task.create(future)
                .mapWithFallback(s -> Integer.parseInt(s),
                        (throwable, s) -> {
                            throw new RuntimeException(throwable);
                        })
                .onFailure(throwable -> seq.incrementAndGet())
                .andThen(i -> {
                    context.fail();
                })
                .onFailure(throwable -> {
                    async.complete();
                });
        future.complete("Hello World");
    }

    @Test
    public void testAndThenFallback(TestContext context) {
        AtomicInteger seq = new AtomicInteger();
        Async async = context.async();
        Future<String> future = Future.future();
//        future.complete("Hello World");
        Task.create(future)
                .map(s -> s.substring(0, 5))
                .andThenWithFallback(s -> Integer.parseInt(s), (throwable, s) -> seq.incrementAndGet())
                .onFailure(throwable -> seq.incrementAndGet())
                .map(s -> s.length())
                .andThen(i -> {
                    context.assertEquals(5, i);
                    context.assertEquals(seq.get(), 1);
                    async.complete();
                })
                .onFailure(throwable -> {
                    context.fail();
                });
        future.complete("Hello World");
    }

    @Test
    public void testAndThenFallback2(TestContext context) {
        AtomicInteger seq = new AtomicInteger();
        Async async = context.async();
        Future<String> future = Future.future();
//        future.complete("Hello World");
        Task.create(future)
                .map(s -> s.substring(0, 5))
                .andThenWithFallback(s -> Integer.parseInt(s), (throwable, s) -> {
                    seq.incrementAndGet();
                    throw new RuntimeException(throwable);
                })
                .onFailure(throwable -> seq.incrementAndGet())
                .map(s -> s.length())
                .andThen(i -> {
                    context.fail();
                })
                .onFailure(throwable -> {
                    context.assertEquals(seq.get(), 2);
                    async.complete();
                });
        future.complete("Hello World");
    }

    @Test
    public void testFlatMap(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");

        Task.create(future)
                .map(s -> s.length())
                .flatMap(new FutureFunction(vertx, port))
                .andThen(length -> {
                    System.out.println(length);
                    context.assertEquals("Hello World".length() * 2, length);
                    async.complete();
                }).onFailure(throwable -> throwable.printStackTrace());
    }

    @Test
    public void testFlat2(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> s.length())
                .flatMap(integer -> {
                    Future<Integer> rFuture = Future.future();
                    rFuture.complete(integer * 2);
                    return rFuture;
                })
                .andThen(length -> {
                    context.assertEquals("Hello World".length() * 2, length);
                    async.complete();
                });
    }

    @Test
    public void testFlat3(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> Integer.parseInt(s))
                .flatMap(new Function<Integer, Future<Integer>>() {
                    @Override
                    public Future<Integer> apply(Integer integer) {
                        Future<Integer> rFuture = Future.future();
                        rFuture.complete(integer * 2);
                        return rFuture;
                    }
                })
                .andThen(length -> {
                    context.fail();
                }).onFailure(throwable -> {
//            throwable.printStackTrace();
            context.assertNotNull(throwable);
            async.complete();
        });
    }

    @Test
    public void testFlat4(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> s.length())
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
//            throwable.printStackTrace();
            context.assertNotNull(throwable);
            async.complete();
        });
    }

    @Test
    public void testFlatMapFallback(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> s.length())
                .flatMapWithFallback(integer -> {
                    Future<Integer> rFuture = Future.future();
                    rFuture.complete(integer / 0);
                    return rFuture;
                }, (throwable, integer) -> 0)
                .andThen(length -> {
                    context.assertEquals(0, length);
                    async.complete();
                }).onFailure(throwable -> {
//            throwable.printStackTrace();
            context.fail();

        });
    }

    @Test
    public void testToFuture(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> s.toUpperCase())
                .andThen(s -> context.assertEquals("HELLO WORLD", s))
                .map(s -> s.length())
                .andThen(length -> {
                    context.assertEquals("Hello World".length(), length);
                }).toFutrue().setHandler(ar -> {
            if (ar.succeeded()) {
                context.assertEquals("Hello World".length(), ar.result());
                async.complete();
            } else {
                context.fail();
            }
        });
    }

    @Test
    public void testToFuture2(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.complete("Hello World");
        Task.create(future)
                .map(s -> Integer.parseInt(s))
                .andThen(i -> System.out.println(i))
                .toFutrue().setHandler(ar -> {
            if (ar.succeeded()) {
                context.fail();
            } else {
                ar.cause().printStackTrace();
                async.complete();
            }
        });
    }

}
