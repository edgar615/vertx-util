package com.edgar.util.vertx.base;

import io.vertx.core.Future;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Edgar on 2017/9/12.
 *
 * @author Edgar  Date 2017/9/12
 */
public class Futures {

  public static <T> CompletableFuture<T> toCompletableFuture(Future<T> future) {
    CompletableFuture<T> completableFuture = new CompletableFuture<>();
    future.setHandler(ar -> {
      if (ar.succeeded()) {
        completableFuture.complete(ar.result());
      } else {
        completableFuture.completeExceptionally(ar.cause());
      }
    });
    return completableFuture;
  }

  public static <T> Future<T> toFuture(CompletableFuture<T> completableFuture) {
    Future<T> future = Future.future();
    completableFuture.thenAccept(r -> future.complete(r))
            .exceptionally(t -> {
              future.fail(t);
              return null;
            });
    return future;
  }
}
