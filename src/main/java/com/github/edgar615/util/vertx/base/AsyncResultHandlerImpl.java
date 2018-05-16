package com.github.edgar615.util.vertx.base;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.Objects;
import java.util.function.Consumer;

class AsyncResultHandlerImpl<T> implements AsyncResultHandler<T> {

  private final Consumer<T> succeededHandler;

  private Consumer<Throwable> failedHandler = t -> {
  };

  AsyncResultHandlerImpl(Consumer<T> succeededHandler) {
    Objects.requireNonNull(succeededHandler);
    this.succeededHandler = succeededHandler;
  }

  @Override
  public void bind(Future<T> future) {
    future.setHandler(this);
  }

  @Override
  public AsyncResultHandler<T> otherwise(Consumer<Throwable> failedHandler) {
    Objects.requireNonNull(failedHandler);
    this.failedHandler = failedHandler;
    return this;
  }

  @Override
  public void handle(AsyncResult<T> asyncResult) {
    if (asyncResult.succeeded()) {
      succeededHandler.accept(asyncResult.result());
    } else {
      failedHandler.accept(asyncResult.cause());
    }
  }
}
