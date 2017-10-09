package com.github.edgar615.util.vertx.task;

import com.github.edgar615.util.vertx.function.Tuple4;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * Created by Edgar on 2016/7/21.
 *
 * @author Edgar  Date 2016/7/21
 */
class Tuple4TaskImpl<T1, T2, T3, T4> extends BaseTask<Tuple4<T1, T2, T3, T4>> implements
        Tuple4Task<T1, T2, T3, T4> {

  Tuple4TaskImpl(String name, Future<T1> futureT1, Future<T2> futureT2, Future<T3> futureT3,
                 Future<T4> futureT4) {
    super(name, Future.<Tuple4<T1, T2, T3, T4>>future());
    CompositeFuture compositeFuture = CompositeFuture.all(futureT1, futureT2, futureT3, futureT4);
    compositeFuture.setHandler(ar -> {
      if (ar.succeeded()) {
        T1 t1 = ar.result().resultAt(0);
        T2 t2 = ar.result().resultAt(1);
        T3 t3 = ar.result().resultAt(2);
        T4 t4 = ar.result().resultAt(3);
        complete(Tuple4.create(t1, t2, t3, t4));
      } else {
        fail(ar.cause());
      }
    });
  }

  Tuple4TaskImpl(Future<T1> futureT1, Future<T2> futureT2, Future<T3> futureT3,
                 Future<T4> futureT4) {
    this("Tuple4Task", futureT1, futureT2, futureT3, futureT4);
  }

}
