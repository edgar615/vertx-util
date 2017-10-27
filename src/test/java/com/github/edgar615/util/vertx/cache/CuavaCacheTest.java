package com.github.edgar615.util.vertx.cache;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Edgar on 2017/10/27.
 *
 * @author Edgar  Date 2017/10/27
 */
@RunWith(VertxUnitRunner.class)
public class CuavaCacheTest {

  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @Test
  public void testGet(TestContext testContext) {
    Cache<String, String> cache = new GuavaCache<>(vertx, new GuavaCacheOptions());
    AtomicBoolean check = new AtomicBoolean();
    cache.get("foo", ar -> {
      if (ar.succeeded()) {
        testContext.assertNull(ar.result());
        check.set(true);
      } else {
        testContext.fail();
      }
    });
    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testLoadIfNull(TestContext testContext) {
    Cache<String, String> cache = new GuavaCache<>(vertx, new GuavaCacheOptions());
    AtomicInteger check = new AtomicInteger();
    AtomicInteger loadCount = new AtomicInteger();
    CacheLoader<String, String> loader = (key, handler) -> {
      loadCount.incrementAndGet();
      handler.handle(Future.succeededFuture(key.toUpperCase()));
    };
    for (int i = 0; i < 100; i++) {
      cache.get("foo", loader, ar -> {
        if (ar.succeeded()) {
          testContext.assertEquals("FOO", ar.result());
          check.incrementAndGet();
        } else {
          testContext.fail();
        }
      });
    }
    Awaitility.await().until(() -> check.get() == 100);
    testContext.assertEquals(1, loadCount.get());
  }

  @Test
  public void testLoadThough(TestContext testContext) {
    Cache<String, String> cache = new GuavaCache<>(vertx, new GuavaCacheOptions());
    AtomicInteger check = new AtomicInteger();
    AtomicInteger loadCount = new AtomicInteger();
    CacheLoader<String, String> loader = (key, handler) -> {
      loadCount.incrementAndGet();
      handler.handle(Future.succeededFuture(null));
    };
    for (int i = 0; i < 100; i++) {
      cache.get("foo", loader, ar -> {
        if (ar.succeeded()) {
          testContext.assertNull(ar.result());
          check.incrementAndGet();
        } else {
          testContext.fail();
        }
      });
    }
    Awaitility.await().until(() -> check.get() == 100);
    testContext.assertEquals(100, loadCount.get());
  }

  @Test
  public void testPut(TestContext testContext) {
    Cache<String, String> cache = new GuavaCache<>(vertx, new GuavaCacheOptions());
    AtomicBoolean check = new AtomicBoolean();
    cache.get("foo", ar -> {
      if (ar.succeeded()) {
        testContext.assertNull(ar.result());
        check.set(true);
      } else {
        testContext.fail();
      }
    });
    Awaitility.await().until(() -> check.get());
    AtomicBoolean check2 = new AtomicBoolean();
    cache.put("foo","bar", ar -> {
      if (ar.succeeded()) {
        testContext.assertNull(ar.result());
        check2.set(true);
      } else {
        testContext.fail();
      }
    });
    Awaitility.await().until(() -> check2.get());

    AtomicBoolean check3 = new AtomicBoolean();
    cache.get("foo", ar -> {
      if (ar.succeeded()) {
        testContext.assertEquals("bar", ar.result());
        check3.set(true);
      } else {
        testContext.fail();
      }
    });
    Awaitility.await().until(() -> check3.get());
  }

  @Test
  public void testEvict(TestContext testContext) {
    Cache<String, String> cache = new GuavaCache<>(vertx, new GuavaCacheOptions());
    AtomicBoolean check1 = new AtomicBoolean();
    cache.put("foo","bar", ar -> {
      if (ar.succeeded()) {
        testContext.assertNull(ar.result());
        check1.set(true);
      } else {
        testContext.fail();
      }
    });
    Awaitility.await().until(() -> check1.get());

    AtomicBoolean check2 = new AtomicBoolean();
    cache.get("foo", ar -> {
      if (ar.succeeded()) {
        testContext.assertEquals("bar", ar.result());
        check2.set(true);
      } else {
        testContext.fail();
      }
    });
    Awaitility.await().until(() -> check2.get());

    AtomicBoolean check3 = new AtomicBoolean();
    cache.evict("foo", ar -> {
      if (ar.succeeded()) {
        testContext.assertNull(ar.result());
        check3.set(true);
      } else {
        testContext.fail();
      }
    });
    Awaitility.await().until(() -> check3.get());

    AtomicBoolean check4 = new AtomicBoolean();
    cache.get("foo", ar -> {
      if (ar.succeeded()) {
        testContext.assertNull(ar.result());
        check4.set(true);
      } else {
        testContext.fail();
      }
    });
    Awaitility.await().until(() -> check4.get());
  }

}
