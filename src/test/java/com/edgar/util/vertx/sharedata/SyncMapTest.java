package com.edgar.util.vertx.sharedata;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Edgar on 2017/6/20.
 *
 * @author Edgar  Date 2017/6/20
 */
@RunWith(VertxUnitRunner.class)
public class SyncMapTest {

  Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @Test
  public void testSame(TestContext testContext) {
    SyncMap<String, String> map1 = new SyncMap<>(vertx, "test");
    AtomicBoolean complete = new AtomicBoolean();
    Runnable runnable = () -> {
      SyncMap<String, String> map2 = new SyncMap<>(vertx, "test");
      map2.put("a", "a", ar -> {
        complete.set(true);
      });
    };

    new Thread(runnable).start();

    Awaitility.await().until(() -> complete.get());

    Async async = testContext.async();
    map1.get("a", ar2 -> {
      System.out.println(ar2.result());
      testContext.assertEquals("a", ar2.result());
      async.complete();
    });
  }
}
