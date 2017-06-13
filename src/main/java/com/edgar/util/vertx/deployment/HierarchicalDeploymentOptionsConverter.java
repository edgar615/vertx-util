package com.edgar.util.vertx.deployment;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2017/6/13.
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
 * @author Edgar  Date 2017/6/13
 */
class HierarchicalDeploymentOptionsConverter {
  static void fromJson(JsonObject json, HierarchicalDeploymentOptions options) {
    if (json.getValue("class") instanceof String) {
      options.setVerticleClass(json.getString("class"));
    } else {
      throw new RuntimeException("miss verticle class");
    }
    if (json.getValue("instances") instanceof Integer) {
      options.setInstance(json.getInteger("instances"));
    }
    if (json.getValue("worker") instanceof Boolean) {
      options.setWorker(json.getBoolean("worker"));
    }
    if (json.getValue("config") instanceof JsonObject) {
      options.setConfig(json.getJsonObject("config"));
    }
    if (json.getValue("dependencies") instanceof JsonArray) {
      JsonArray dependencies = json.getJsonArray("dependencies");
      for (int i = 0; i < dependencies.size(); i++) {
        if (dependencies.getValue(i) instanceof String) {
          options.addDependencyVerticle(dependencies.getString(i));
        }
      }
    }
  }
}
