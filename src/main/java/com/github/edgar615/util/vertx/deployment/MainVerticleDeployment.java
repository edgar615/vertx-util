package com.github.edgar615.util.vertx.deployment;

import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Edgar on 2017/6/12.
 *
 * @author Edgar  Date 2017/6/12
 */
public class MainVerticleDeployment {
  private final List<HierarchicalDeployment> deployments = new ArrayList<>();

  private final JsonObject config = new JsonObject();

  public MainVerticleDeployment(JsonObject json) {
    JsonObject verticlesConfig = json.getJsonObject("verticles", new JsonObject());
    json.remove("verticles");
    this.config.mergeIn(json);
    for (String verticleName : verticlesConfig.fieldNames()) {
      HierarchicalDeployment options =
              new HierarchicalDeployment(verticleName,
                                         verticlesConfig.getJsonObject(verticleName,
                                                                       new JsonObject()));
      deployments.add(options);
    }


    //检测依赖有没有形成闭环（会导致死循环），有没有未定义的verticle
    for (HierarchicalDeployment deployment : deployments) {
      if (checkUndefined(deployment)) {
        throw new IllegalArgumentException("undefined dependency for:"
                                           + deployment.getVerticleName());
      }
      if (checkCycle(deployment)) {
        throw new IllegalArgumentException("cycle dependency:" + deployment.getVerticleName());
      }
    }
  }

  public HierarchicalDeployment get(String verticle) {
    Optional<HierarchicalDeployment> optional
            = deployments.stream()
            .filter(o -> o.getVerticleName().equals(verticle))
            .findFirst();
    if (optional.isPresent()) {
      return optional.get();
    }
    return null;
  }

  public List<HierarchicalDeployment> getDeployments() {
    return deployments;
  }

  public JsonObject getConfig() {
    return config;
  }

  private boolean checkUndefined(HierarchicalDeployment deployment) {
    return deployment.getDependencyVerticles().stream()
                   .map(this::get)
                   .filter(d -> d == null)
                   .count() > 0;
  }

  private void ancestors(HierarchicalDeployment deployment, List<String> ancestors) {
//    ancestors.addAll(deployment.getDependencyVerticles());
    for (String parent : deployment.getDependencyVerticles()) {
      if (ancestors.contains(parent)) {
        throw new IllegalArgumentException("cycle dependency:" + deployment.getVerticleName());
      }
      ancestors.add(parent);
      ancestors(get(parent), ancestors);
    }
  }

  private boolean checkCycle(HierarchicalDeployment deployment) {
    List<String> ancestors = new ArrayList<>();
    ancestors(deployment, ancestors);
    if (ancestors.isEmpty()) {
      return false;
    }

    for (String ancestor : ancestors) {
      if (ancestor.equals(deployment.getVerticleName())) {
        return true;
      }
    }
    return false;
  }
}
