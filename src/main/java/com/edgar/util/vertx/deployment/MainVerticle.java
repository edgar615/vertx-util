package com.edgar.util.vertx.deployment;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    MainVerticleOptions options = new MainVerticleOptions(config().copy());

//    vertx.deployVerticle();
  }
}
