package com.github.edgar615.util.vertx.base;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.function.Consumer;

/**
 * 每次对AsyncResult写 if (ar.succeeded()){} else{}，我看着代码都很别扭，所以我通过这个类将if/else的逻辑拆分.
 *
 * @param <T>
 */
public interface AsyncResultHandler<T> extends Handler<AsyncResult<T>> {

  /**
   * 将handler注册到future上.
   *
   * @param future
   */
  void bind(Future<T> future);

  /**
   * 异常的处理类.
   *
   * @param failedHandler
   * @return
   */
  AsyncResultHandler<T> otherwise(Consumer<Throwable> failedHandler);

  static <T> AsyncResultHandler<T> create(Consumer<T> succeededHandler) {
    return new AsyncResultHandlerImpl<>(succeededHandler);
  }
}
