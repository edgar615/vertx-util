package com.github.edgar615.util.vertx.deployment;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Edgar on 2017/6/12.
 *
 * @author Edgar  Date 2017/6/12
 */
public class HierarchicalDeployment {

  private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalDeployment.class);

  private volatile DeploymentState state = DeploymentState.WAITING;

  private final AtomicInteger precondition = new AtomicInteger();

  private final String verticleName;

  private final JsonObject config = new JsonObject();

  private final List<String> dependencyVerticles = new ArrayList<>();

  private String verticleClass;

  private int instances = 1;

  private boolean worker = false;

  public HierarchicalDeployment(String verticleName, JsonObject config) {
    if (verticleName == null) {
      throw new IllegalArgumentException("Field `name` not specified for verticle");
    }
    this.verticleName = verticleName;
    HierarchicalDeploymentConverter.fromJson(config, this);
  }

  public String getVerticleName() {
    return verticleName;
  }

  public String getVerticleClass() {
    return verticleClass;
  }

  public HierarchicalDeployment setVerticleClass(String verticleClass) {
    this.verticleClass = verticleClass;
    return this;
  }

  public int getInstances() {
    return instances;
  }

  public HierarchicalDeployment setInstances(int instances) {
    if (instances < 1) {
      throw new IllegalArgumentException(
              String.format("Field `instances` not specified or less than 1 for verticle %s",
                                verticleName));
    }
    this.instances = instances;
    return this;
  }

  public boolean isWorker() {
    return worker;
  }

  public HierarchicalDeployment setWorker(boolean worker) {
    this.worker = worker;
    return this;
  }


  public HierarchicalDeployment setConfig(JsonObject config) {
    this.config.mergeIn(config);
    return this;
  }

  public JsonObject getConfig() {
    return config;
  }

  public HierarchicalDeployment addDependencyVerticle(String verticleName) {
    dependencyVerticles.add(verticleName);
    return this;
  }

  public List<String> getDependencyVerticles() {
    return dependencyVerticles;
  }

  public Future<String> deploy(Vertx vertx, Handler<AsyncResult<String>> handler) {
    if (state != DeploymentState.WAITING) {
      handler.handle(Future.failedFuture(verticleName + " is already deploy"));
    }
    Future<String> future = Future.future();
    DeploymentOptions deploymentOptions = new DeploymentOptions()
            .setConfig(config)
            .setWorker(worker)
            .setInstances(instances);
    vertx.deployVerticle(verticleClass, deploymentOptions, ar -> {
      if (ar.succeeded()) {
        LOGGER.info("deploy {} succeeded", verticleName);
        state = DeploymentState.COMPLETED;
        handler.handle(Future.succeededFuture(verticleName));
      } else {
        LOGGER.error("deploy {} failed", verticleName, ar.cause());
        handler.handle(Future.failedFuture(ar.cause()));
      }
    });
    state = DeploymentState.STARTED;
    return future;
  }

  public boolean checkPrecondition(String completedVerticle) {
    if (state != DeploymentState.WAITING) {
      return false;
    }
    if (dependencyVerticles.contains(completedVerticle)) {
      int result = precondition.incrementAndGet();
      return  result == dependencyVerticles.size();
    }
    return false;
  }
}
