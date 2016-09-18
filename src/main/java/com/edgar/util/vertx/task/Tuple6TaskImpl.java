package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Tuple6;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * Created by Edgar on 2016/7/21.
 *
 * @author Edgar  Date 2016/7/21
 */
class Tuple6TaskImpl<T1, T2, T3, T4, T5, T6> extends BaseTask<Tuple6<T1, T2, T3, T4, T5, T6>> implements Tuple6Task<T1, T2, T3, T4, T5, T6> {

    Tuple6TaskImpl(Future<T1> futureT1, Future<T2> futureT2, Future<T3> futureT3, Future<T4> futureT4, Future<T5> futureT5, Future<T6> futureT6) {
        super(null, Future.<Tuple6<T1, T2, T3, T4, T5, T6>>future());
        CompositeFuture compositeFuture = CompositeFuture.all(futureT1, futureT2, futureT3, futureT4, futureT5, futureT6);
        compositeFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                T1 t1 = ar.result().result(0);
                T2 t2 = ar.result().result(1);
                T3 t3 = ar.result().result(2);
                T4 t4 = ar.result().result(3);
                T5 t5 = ar.result().result(4);
                T6 t6 = ar.result().result(5);
                complete(Tuple6.create(t1, t2, t3, t4, t5, t6));
            } else {
                fail(ar.cause());
            }
        });
    }

}
