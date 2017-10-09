package com.github.edgar615.util.vertx.task;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

/**
 * Created by Edgar on 2016/7/28.
 *
 * @author Edgar  Date 2016/7/28
 */
public class TaskFunction implements Function<Integer, Task<Integer>> {

  private final Vertx vertx;

  public TaskFunction(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public Task<Integer> apply(Integer length) {
    Future<Integer> future = Future.future();
    Task<Integer> task = Task.create(future);
    vertx.createHttpClient().get(9000, "localhost", "/?length=" + length, response -> {
      response.bodyHandler(body -> {
        JsonObject jsonObject = body.toJsonObject();
        task.complete(Integer.parseInt(jsonObject.getString("length")) * 2);
      });
    }).end();

    return task;
  }
}
