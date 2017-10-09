package com.github.edgar615.util.vertx.deployment;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2017/6/14.
 *
 * @author Edgar  Date 2017/6/14
 */
public class TestVerticle4 extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    System.out.println("TestVerticle4:" + this.toString());
    System.out.println("TestVerticle4:" + vertx.getOrCreateContext().isWorkerContext());
    vertx.eventBus().send("deployment.test", config().getInteger("port", 4));
    startFuture.complete();
  }
}
