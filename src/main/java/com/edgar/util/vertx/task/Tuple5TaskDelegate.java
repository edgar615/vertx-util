package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Tuple5;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Edgar on 2016/7/29.
 *
 * @author Edgar  Date 2016/7/29
 */
class Tuple5TaskDelegate<T1, T2, T3, T4, T5> implements Tuple5Task<T1, T2, T3, T4, T5> {
    private final Task<Tuple5<T1, T2, T3, T4, T5>> task;

    Tuple5TaskDelegate(Task<Tuple5<T1, T2, T3, T4, T5>> task) {
        this.task = task;
    }

    @Override
    public Tuple5<T1, T2, T3, T4, T5> result() {
        return task.result();
    }

    @Override
    public String name() {
        return task.name();
    }

    @Override
    public void complete(Tuple5<T1, T2, T3, T4, T5> result) {
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
    public Task<Tuple5<T1, T2, T3, T4, T5>> onTrace(Consumer<List<Trace>> traceHandler) {
        return null;
    }

    @Override
    public void setHandler(Handler<AsyncResult<Tuple5<T1, T2, T3, T4, T5>>> handler) {
        task.setHandler(handler);
    }

    @Override
    public Handler<AsyncResult<Tuple5<T1, T2, T3, T4, T5>>> completer() {
        return task.completer();
    }

}
