package com.edgar.util.vertx.deployment;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于启动Vert.x应用的组件，通过配置文件指定需要启动哪些Verticle，避免硬编码.
 * <p>
 * <pre>
 *     "verticles": {
 * "MetricsVerticle": {
 * "class": "com.groupon.vertx.utils.MetricsVerticle",
 * "instances": 1,
 * "worker": true,
 * "config": { }
 * },
 * "ExampleVerticle": {
 * "class": "com.groupon.example.verticle.ExampleVerticle",
 * "instances": 1,
 * "worker": true,
 * "config": { },
 * "dependencies": [ "MetricsVerticle" ]
 * }
 * }
 * </pre>
 *
 * @author Edgar  Date 2017/6/12
 */
public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  private final AtomicInteger verticleCount = new AtomicInteger(0);
  @Override
  public void start(Future<Void> startFuture) throws Exception {
    MainVerticleDeployment deployment = new MainVerticleDeployment(config().copy());

    deployment.getDeployments().stream()
            .filter(o -> o.getDependencyVerticles().isEmpty())
            .forEach(d -> d.deploy(vertx, createHandler(deployment, d.getVerticleName(), startFuture)));
  }

  private void checkAndDeploy(MainVerticleDeployment deployment, String verticle,Future<Void> startFuture) {
    deployment.getDeployments().forEach(d -> {
      boolean checkResult = d.checkPrecondition(verticle);
      if (checkResult) {
        d.deploy(vertx, createHandler(deployment, d.getVerticleName(), startFuture));
      }
    });
    if (verticleCount.get() == deployment.getDeployments().size()) {
      if (!startFuture.isComplete()) {
        startFuture.complete();
      }
    }
  }

  private Handler<AsyncResult<String>> createHandler(MainVerticleDeployment deployment,
                                                     String verticle, Future<Void> startFuture) {
    return ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        startFuture.fail(ar.cause());
        return;
      }
      verticleCount.incrementAndGet();
      checkAndDeploy(deployment, verticle, startFuture);
    };
  }

}
