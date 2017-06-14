package com.edgar.util.vertx.deployment;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Edgar on 2017/6/14.
 *
 * @author Edgar  Date 2017/6/14
 */
@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext testContext) {
    vertx = Vertx.vertx();
  }

  @Test
  public void testSingleDeploy(TestContext testContext) {
    JsonObject config = new JsonObject()
            .put("TestVerticle", new JsonObject()
            .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
            .put("dependencies", new JsonArray().add("test")));
//    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
//            ("verticles", config));
//    Assert.assertEquals(1, deployment.getDeployments().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(), new DeploymentOptions().setConfig(new JsonObject().put
            ("verticles", config)), ar -> {
      if (ar.succeeded()) {
        async.complete();
      } else {
        ar.cause().printStackTrace();
      }

    });
  }

}
