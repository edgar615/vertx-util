package com.github.edgar615.util.vertx.deployment;

import io.vertx.core.AbstractVerticle;

import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2017/6/14.
 *
 * @author Edgar  Date 2017/6/14
 */
public class TestVerticle2 extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    System.out.println("TestVerticle2:" + vertx.getOrCreateContext().isWorkerContext());
    TimeUnit.SECONDS.sleep(2);
    vertx.eventBus().send("deployment.test", config().getInteger("port", 2));
  }
}
