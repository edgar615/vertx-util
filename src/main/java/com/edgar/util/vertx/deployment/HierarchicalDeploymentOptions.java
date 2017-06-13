package com.edgar.util.vertx.deployment;

import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar on 2017/6/12.
 *
 * @author Edgar  Date 2017/6/12
 */
public class HierarchicalDeploymentOptions {

  private final String verticleName;

  private final JsonObject config = new JsonObject();

  private final List<String> dependencyVerticles = new ArrayList<>();

  private final List<HierarchicalDeploymentOptions> children = new ArrayList<>();

  private String verticleClass;

  private int instance = 1;

  private boolean worker = false;

  public HierarchicalDeploymentOptions(String verticleName, JsonObject config) {
    this.verticleName = verticleName;
    HierarchicalDeploymentOptionsConverter.fromJson(config, this);
  }

  public String getVerticleName() {
    return verticleName;
  }

  public String getVerticleClass() {
    return verticleClass;
  }

  public HierarchicalDeploymentOptions setVerticleClass(String verticleClass) {
    this.verticleClass = verticleClass;
    return this;
  }

  public int getInstance() {
    return instance;
  }

  public HierarchicalDeploymentOptions setInstance(int instance) {
    this.instance = instance;
    return this;
  }

  public boolean isWorker() {
    return worker;
  }

  public HierarchicalDeploymentOptions setWorker(boolean worker) {
    this.worker = worker;
    return this;
  }


  public HierarchicalDeploymentOptions setConfig(JsonObject config) {
    this.config.mergeIn(config);
    return this;
  }

  public JsonObject getConfig() {
    return config;
  }

  public HierarchicalDeploymentOptions addDependencyVerticle(String verticleName) {
    dependencyVerticles.add(verticleName);
    return this;
  }

  public List<String> getDependencyVerticles() {
    return dependencyVerticles;
  }

  public HierarchicalDeploymentOptions addChild(HierarchicalDeploymentOptions options) {
    children.add(options);
    return this;
  }

  public List<HierarchicalDeploymentOptions> getChildren() {
    return children;
  }
}
