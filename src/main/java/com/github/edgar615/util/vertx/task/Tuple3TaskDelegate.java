package com.github.edgar615.util.vertx.task;

import com.github.edgar615.util.vertx.function.Tuple3;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by Edgar on 2016/7/29.
 *
 * @author Edgar  Date 2016/7/29
 */
class Tuple3TaskDelegate<T1, T2, T3> implements Tuple3Task<T1, T2, T3> {
  private final Task<Tuple3<T1, T2, T3>> task;

  Tuple3TaskDelegate(Task<Tuple3<T1, T2, T3>> task) {
    this.task = task;
  }

  @Override
  public Tuple3<T1, T2, T3> result() {
    return task.result();
  }

  @Override
  public String name() {
    return task.name();
  }

  @Override
  public void complete(Tuple3<T1, T2, T3> result) {
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
  public void setHandler(Handler<AsyncResult<Tuple3<T1, T2, T3>>> handler) {
    task.setHandler(handler);
  }

  @Override
  public Handler<AsyncResult<Tuple3<T1, T2, T3>>> completer() {
    return task.completer();
  }

}
