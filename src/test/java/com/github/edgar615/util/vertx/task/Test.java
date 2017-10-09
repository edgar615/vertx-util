package com.github.edgar615.util.vertx.task;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Edgar on 2016/5/6.
 *
 * @author Edgar  Date 2016/5/6
 */
public class Test extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(Test.class.getName());
  }

  @Override
  public void start() throws Exception {
    ConcurrentMap<Integer, Future<Integer>> map = new ConcurrentHashMap<>();
    for (int i = 0; i < 10; i++) {
      Future<Integer> future = Future.future();
      if (map.containsKey(i)) {
        //wait
        Future<Integer> prevFuture = map.get(i);
        prevFuture.setHandler(ar -> {

        });
      } else {

      }
    }
  }
}
