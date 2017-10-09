package com.github.edgar615.util.vertx.deployment;

import io.vertx.core.AbstractVerticle;

/**
 * Created by Edgar on 2017/6/14.
 *
 * @author Edgar  Date 2017/6/14
 */
public class TestVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    System.out.println("TestVerticle:" + vertx.getOrCreateContext().isWorkerContext());
    vertx.eventBus().send("deployment.test", config().getInteger("port", 1));
  }
}
