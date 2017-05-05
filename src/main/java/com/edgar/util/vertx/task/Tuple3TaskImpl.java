package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Tuple3;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * Created by Edgar on 2016/7/21.
 *
 * @author Edgar  Date 2016/7/21
 */
class Tuple3TaskImpl<T1, T2, T3> extends BaseTask<Tuple3<T1, T2, T3>> implements Tuple3Task<T1,
        T2, T3> {

  Tuple3TaskImpl(String name, Future<T1> futureT1, Future<T2> futureT2, Future<T3> futureT3) {
    super(name, Future.<Tuple3<T1, T2, T3>>future());
    CompositeFuture compositeFuture = CompositeFuture.all(futureT1, futureT2, futureT3);
    compositeFuture.setHandler(ar -> {
      if (ar.succeeded()) {
        T1 t1 = ar.result().resultAt(0);
        T2 t2 = ar.result().resultAt(1);
        T3 t3 = ar.result().resultAt(2);
        complete(Tuple3.create(t1, t2, t3));
      } else {
        fail(ar.cause());
      }
    });
  }

  Tuple3TaskImpl(Future<T1> futureT1, Future<T2> futureT2, Future<T3> futureT3) {
    this("Tuple3Task:", futureT1, futureT2, futureT3);
  }

}
