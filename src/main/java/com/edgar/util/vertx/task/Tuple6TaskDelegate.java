package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Tuple6;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by Edgar on 2016/7/29.
 *
 * @author Edgar  Date 2016/7/29
 */
class Tuple6TaskDelegate<T1, T2, T3, T4, T5, T6> implements Tuple6Task<T1, T2, T3, T4, T5, T6> {
  private final Task<Tuple6<T1, T2, T3, T4, T5, T6>> task;

  Tuple6TaskDelegate(Task<Tuple6<T1, T2, T3, T4, T5, T6>> task) {
    this.task = task;
  }

  @Override
  public Tuple6<T1, T2, T3, T4, T5, T6> result() {
    return task.result();
  }

  @Override
  public String name() {
    return task.name();
  }

  @Override
  public void complete(Tuple6<T1, T2, T3, T4, T5, T6> result) {
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
  public void setHandler(Handler<AsyncResult<Tuple6<T1, T2, T3, T4, T5, T6>>> handler) {
    task.setHandler(handler);
  }

  @Override
  public Handler<AsyncResult<Tuple6<T1, T2, T3, T4, T5, T6>>> completer() {
    return task.completer();
  }

}
