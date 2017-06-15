package com.edgar.util.vertx.deployment;

import io.vertx.core.AbstractVerticle;

import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2017/6/14.
 *
 * @author Edgar  Date 2017/6/14
 */
public class TestVerticle3 extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    System.out.println("TestVerticle3:" + vertx.getOrCreateContext().isWorkerContext());
    TimeUnit.SECONDS.sleep(1);
    vertx.eventBus().send("deployment.test", config().getInteger("port", 3));
  }
}
