package com.edgar.util.vertx.deployment;

import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Edgar on 2017/6/12.
 *
 * @author Edgar  Date 2017/6/12
 */
public class MainVerticleOptions {
  private final List<HierarchicalDeploymentOptions> optionsList = new ArrayList<>();

  private final JsonObject config = new JsonObject();

  public MainVerticleOptions(JsonObject json) {
    JsonObject verticlesConfig = json.getJsonObject("verticles", new JsonObject());
    json.remove("verticles");
    this.config.mergeIn(json);
    List<HierarchicalDeploymentOptions> optionsList = new ArrayList<>();
    for (String verticleName : verticlesConfig.fieldNames()) {
      HierarchicalDeploymentOptions options =
              new HierarchicalDeploymentOptions(verticleName,
                                                verticlesConfig.getJsonObject(verticleName,
                                                                              new JsonObject()));
      optionsList.add(options);
    }
    //迭代HierarchicalDeploymentOptions，填充Children
    addChild(optionsList);

    //检测依赖有没有形成闭环（会导致死循环）
    for (HierarchicalDeploymentOptions options : optionsList) {
      if (checkCycle(options)) {
        throw new IllegalArgumentException("cycle dependency:" + options.getVerticleName());
      }

    }
  }

  public HierarchicalDeploymentOptions get(String verticle) {
    Optional<HierarchicalDeploymentOptions> optional
            = optionsList.stream()
            .filter(o -> o.getVerticleName().equals(verticle))
            .findFirst();
    if (optional.isPresent()) {
      return optional.get();
    }
    return null;
  }

  private List<HierarchicalDeploymentOptions> descendant(HierarchicalDeploymentOptions options) {
    List<HierarchicalDeploymentOptions> children = new ArrayList<>();
    children.addAll(options.getChildren());
    for (HierarchicalDeploymentOptions child : options.getChildren()) {
      children.addAll(descendant(child));
    }
    return children;
  }

  private boolean checkCycle(HierarchicalDeploymentOptions options) {
    List<HierarchicalDeploymentOptions> descendant = descendant(options);
    if (options.getChildren().isEmpty()) {
      return false;
    }

    for (HierarchicalDeploymentOptions child : descendant) {
      if (child == options) {
        return true;
      }
    }
    return false;
  }

  private void addChild(List<HierarchicalDeploymentOptions> optionsList) {
    for (HierarchicalDeploymentOptions options : optionsList) {
      List<String> dependencyVerticles = options.getDependencyVerticles();
      for (String verticle : dependencyVerticles) {
        HierarchicalDeploymentOptions dependency = get(verticle);
        if (dependency == null) {
          throw new IllegalArgumentException("undefined dependencyVerticles:" + verticle);
        }
        dependency.addChild(options);
      }
    }
  }

}
