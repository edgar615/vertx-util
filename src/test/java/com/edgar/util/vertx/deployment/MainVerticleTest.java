package com.edgar.util.vertx.deployment;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

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
  public void undifinedDependencyShouldThrowException(TestContext testContext) {
    JsonObject config = new JsonObject()
            .put("TestVerticle", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
                    .put("dependencies", new JsonArray().add("test")));
//    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
//            ("verticles", config));
//    Assert.assertEquals(1, deployment.getDeployments().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put
                                 ("verticles", config)), ar -> {
              if (ar.succeeded()) {
                testContext.fail();
              } else {
                ar.cause().printStackTrace();
                testContext.assertTrue(ar.cause() instanceof IllegalArgumentException);
                async.complete();
              }
            });
  }

  @Test
  public void cycleDependencyShouldThrowException(TestContext testContext) {
    JsonObject config = new JsonObject()
            .put("TestVerticle1", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
                    .put("dependencies", new JsonArray().add("TestVerticle2")))
            .put("TestVerticle2", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
                    .put("dependencies", new JsonArray().add("TestVerticle3")))
            .put("TestVerticle3", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
                    .put("dependencies", new JsonArray().add("TestVerticle1")));
//    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
//            ("verticles", config));
//    Assert.assertEquals(1, deployment.getDeployments().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put
                                 ("verticles", config)), ar -> {
              if (ar.succeeded()) {
                testContext.fail();
              } else {
                ar.cause().printStackTrace();
                testContext.assertTrue(ar.cause() instanceof IllegalArgumentException);
                async.complete();
              }
            });
  }

  @Test
  public void testSingleDeploy(TestContext testContext) {
    JsonObject config = new JsonObject()
            .put("TestVerticle", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle"));
    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
            ("verticles", config));
    Assert.assertEquals(1, deployment.getDeployments().size());
    Assert.assertEquals(0, deployment.getConfig().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put
                                 ("verticles", config)), ar -> {
              if (ar.succeeded()) {
                async.complete();
              } else {
                ar.cause().printStackTrace();
                testContext.fail();
              }
            });
  }

  @Test
  public void testTwoIndependentDeploy(TestContext testContext) {

    AtomicInteger count = new AtomicInteger();
    vertx.eventBus().consumer("deployment.test", msg -> {
      count.incrementAndGet();
    });
    JsonObject config = new JsonObject()
            .put("TestVerticle", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle"))
            .put("TestVerticle2", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle2"));
    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
            ("verticles", config));
    Assert.assertEquals(2, deployment.getDeployments().size());
    Assert.assertEquals(0, deployment.getConfig().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put
                                 ("verticles", config)), ar -> {
              if (ar.succeeded()) {
                async.complete();
              } else {
                ar.cause().printStackTrace();
                testContext.fail();
              }
            });
    Awaitility.await().until(() -> count.get() == 2);
  }

  @Test
  public void testTwoDependentDeploy(TestContext testContext) {

    List<Integer> seq = new CopyOnWriteArrayList<>();
    vertx.eventBus().<Integer>consumer("deployment.test", msg -> {
      seq.add(msg.body());
    });
    JsonObject config = new JsonObject()
            .put("TestVerticle", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
                    .put("dependencies", new JsonArray().add("TestVerticle3")))
            .put("TestVerticle3", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle3"));
    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
            ("verticles", config));
    Assert.assertEquals(2, deployment.getDeployments().size());
    Assert.assertEquals(0, deployment.getConfig().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put
                                 ("verticles", config)), ar -> {
              if (ar.succeeded()) {
                async.complete();
              } else {
                ar.cause().printStackTrace();
                testContext.fail();
              }
            });
    Awaitility.await().until(() -> seq.size() == 2);
    System.out.println(seq);
    Assert.assertEquals(3, seq.get(0), 0);
    Assert.assertEquals(1, seq.get(1), 0);
  }

  @Test
  public void testThreeDependentDeploy(TestContext testContext) {

    List<Integer> seq = new CopyOnWriteArrayList<>();
    vertx.eventBus().<Integer>consumer("deployment.test", msg -> {
      seq.add(msg.body());
    });
    JsonObject config = new JsonObject()
            .put("TestVerticle", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
                    .put("dependencies", new JsonArray().add("TestVerticle3").add("TestVerticle2")))
            .put("TestVerticle2", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle2"))
            .put("TestVerticle3", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle3"));
    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
            ("verticles", config));
    Assert.assertEquals(3, deployment.getDeployments().size());
    Assert.assertEquals(0, deployment.getConfig().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put
                                 ("verticles", config)), ar -> {
              if (ar.succeeded()) {
                async.complete();
              } else {
                ar.cause().printStackTrace();
                testContext.fail();
              }
            });
    Awaitility.await().until(() -> seq.size() == 3);
    System.out.println(seq);
    Assert.assertEquals(3, seq.get(0), 0);
    Assert.assertEquals(2, seq.get(1), 0);
    Assert.assertEquals(1, seq.get(2), 0);
  }

  @Test
  public void testThreeDependentDeployWithConfig(TestContext testContext) {

    List<Integer> seq = new CopyOnWriteArrayList<>();
    vertx.eventBus().<Integer>consumer("deployment.test", msg -> {
      seq.add(msg.body());
    });
    JsonObject config = new JsonObject()
            .put("TestVerticle", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
                    .put("dependencies", new JsonArray().add("TestVerticle3").add("TestVerticle2")))
            .put("TestVerticle2", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle2")
                    .put("config", new JsonObject().put("port", 2000)))
            .put("TestVerticle3", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle3"));
    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
            ("verticles", config));
    Assert.assertEquals(3, deployment.getDeployments().size());
    Assert.assertEquals(0, deployment.getConfig().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put
                                 ("verticles", config)), ar -> {
              if (ar.succeeded()) {
                async.complete();
              } else {
                ar.cause().printStackTrace();
                testContext.fail();
              }
            });
    Awaitility.await().until(() -> seq.size() == 3);
    System.out.println(seq);
    Assert.assertEquals(3, seq.get(0), 0);
    Assert.assertEquals(2000, seq.get(1), 0);
    Assert.assertEquals(1, seq.get(2), 0);
  }

  @Test
  public void testFourDependentDeploy(TestContext testContext) {

    List<Integer> seq = new CopyOnWriteArrayList<>();
    vertx.eventBus().<Integer>consumer("deployment.test", msg -> {
      seq.add(msg.body());
    });
    JsonObject config = new JsonObject()
            .put("TestVerticle", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle")
                    .put("dependencies", new JsonArray().add("TestVerticle3").add("TestVerticle2")))
            .put("TestVerticle2", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle2")
                    .put("worker", true)
                    .put("config", new JsonObject().put("port", 2000)))
            .put("TestVerticle3", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle3"))
            .put("TestVerticle4", new JsonObject()
                    .put("class", "com.edgar.util.vertx.deployment.TestVerticle4")
                    .put("dependencies", new JsonArray().add("TestVerticle2"))
                    .put("instances", 3));
    MainVerticleDeployment deployment = new MainVerticleDeployment(new JsonObject().put
            ("verticles", config));
    Assert.assertEquals(4, deployment.getDeployments().size());
    Assert.assertEquals(0, deployment.getConfig().size());

    Async async = testContext.async();
    vertx.deployVerticle(MainVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put
                                 ("verticles", config)), ar -> {
              if (ar.succeeded()) {
                async.complete();
              } else {
                ar.cause().printStackTrace();
                testContext.fail();
              }
            });
    Awaitility.await().until(() -> seq.size() == 6);
    System.out.println(seq);
    Assert.assertEquals(3, seq.get(0), 0);
    Assert.assertEquals(2000, seq.get(1), 0);
    Assert.assertEquals(3, seq.stream().filter(s -> s == 4).count(), 0);
  }
}
