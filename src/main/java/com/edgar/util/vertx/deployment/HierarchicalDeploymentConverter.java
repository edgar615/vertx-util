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
class HierarchicalDeploymentConverter {
  static void fromJson(JsonObject json, HierarchicalDeployment hd) {
    if (json.getValue("class") instanceof String) {
      hd.setVerticleClass(json.getString("class"));
    } else {
      throw new IllegalArgumentException(
              String.format("Field `class` not specified for for verticle %s", hd.getVerticleName()));
    }
    if (json.getValue("instances") instanceof Integer) {
      int instances = json.getInteger("instances");
      if (instances < 1) {
        throw new IllegalArgumentException(
                String.format("Field `instances` not specified or less than 1 for verticle %s",
                              hd.getVerticleName()));
      }
      hd.setInstances(instances);
    }
    if (json.getValue("instances") instanceof String) {
      String instancesStr = json.getString("instances");
      if (instancesStr.length() < 2) {
        throw new IllegalArgumentException(
                String.format("Field `instances` not specified or less than 1 for verticle %s",
                              hd.getVerticleName()));
      }
      if (instancesStr.endsWith("C")) {
        instancesStr = instancesStr.substring(0, instancesStr.indexOf("C"));
        int instances = Integer.parseInt(instancesStr);
        hd.setInstances(instances * Runtime.getRuntime().availableProcessors());
      }
    }
    if (json.getValue("worker") instanceof Boolean) {
      hd.setWorker(json.getBoolean("worker"));
    }
    if (json.getValue("config") instanceof JsonObject) {
      hd.setConfig(json.getJsonObject("config"));
    }
    if (json.getValue("dependencies") instanceof JsonArray) {
      JsonArray dependencies = json.getJsonArray("dependencies");
      for (int i = 0; i < dependencies.size(); i++) {
        if (dependencies.getValue(i) instanceof String) {
          hd.addDependencyVerticle(dependencies.getString(i));
        }
      }
    }
  }

//  protected JsonObject getConfiguration(String configFile) {
//    JsonObject conf;
//    if (configFile != null) {
//      try (Scanner scanner = new Scanner(new File(configFile)).useDelimiter("\\A")) {
//        String sconf = scanner.next();
//        try {
//          conf = new JsonObject(sconf);
//        } catch (DecodeException e) {
////          log.error("Configuration file " + sconf + " does not contain a valid JSON object");
//          return null;
//        }
//      } catch (FileNotFoundException e) {
//        try {
//          conf = new JsonObject(configFile);
//        } catch (DecodeException e2) {
//          // The configuration is not printed for security purpose, it can contain sensitive data.
////          log.error("The -conf option does not point to an existing file or is not a valid JSON object");
//          e2.printStackTrace();
//          return null;
//        }
//      }
//    } else {
//      conf = null;
//    }
//    return conf;
//  }
}
