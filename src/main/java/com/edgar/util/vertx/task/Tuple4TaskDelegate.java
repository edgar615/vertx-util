package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Tuple4;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by Edgar on 2016/7/29.
 *
 * @author Edgar  Date 2016/7/29
 */
class Tuple4TaskDelegate<T1, T2, T3, T4> implements Tuple4Task<T1, T2, T3, T4> {
  private final Task<Tuple4<T1, T2, T3, T4>> task;

  Tuple4TaskDelegate(Task<Tuple4<T1, T2, T3, T4>> task) {
    this.task = task;
  }

  @Override
  public Tuple4<T1, T2, T3, T4> result() {
    return task.result();
  }

  @Override
  public String name() {
    return task.name();
  }

  @Override
  public void complete(Tuple4<T1, T2, T3, T4> result) {
    task.complete(result);
  }

  @Override
  public void fail(Throwable throwable) {
    task.fail(throwable);
  }

  @Override
  public boolean isComplete() {
    return task.isComplete();
  }

  @Override
  public void setHandler(Handler<AsyncResult<Tuple4<T1, T2, T3, T4>>> handler) {
    task.setHandler(handler);
  }

  @Override
  public Handler<AsyncResult<Tuple4<T1, T2, T3, T4>>> completer() {
    return task.completer();
  }

}
