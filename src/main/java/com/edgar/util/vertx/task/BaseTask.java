package com.edgar.util.vertx.task;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Created by Edgar on 2016/5/9.
 *
 * @author Edgar  Date 2016/5/9
 */
class BaseTask<T> implements Task<T> {

  /**
   * future
   */
  private final Future<T> future;

  private String name;

  BaseTask(String name, Future<T> future) {
    this.name = name;
    this.future = future;
  }

  BaseTask(Future<T> future) {
    this("createTask:", future);
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public void complete(T result) {
    future.complete(result);
  }

  @Override
  public void fail(Throwable throwable) {
    future.fail(throwable);
  }

  @Override
  public T result() {
    return future.result();
  }

  public Throwable cause() {
    return future.cause();
  }

  public boolean succeeded() {
    return future.succeeded();
  }

  public boolean failed() {
    return future.failed();
  }

  @Override
  public boolean isComplete() {
    return future.isComplete();
  }

  @Override
  public void setHandler(Handler<AsyncResult<T>> handler) {
    future.setHandler(handler);
  }

  @Override
  public Handler<AsyncResult<T>> completer() {
    return future.completer();
  }

}
