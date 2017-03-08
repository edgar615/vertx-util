package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Tuple2;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * Created by Edgar on 2016/7/21.
 *
 * @author Edgar  Date 2016/7/21
 */
class Tuple2TaskImpl<T1, T2> extends BaseTask<Tuple2<T1, T2>> implements Tuple2Task<T1, T2> {

    Tuple2TaskImpl(String name, Future<T1> futureT1, Future<T2> futureT2) {
        super(name, Future.<Tuple2<T1, T2>>future());
        CompositeFuture compositeFuture = CompositeFuture.all(futureT1, futureT2);
        compositeFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                T1 t1 = ar.result().resultAt(0);
                T2 t2 = ar.result().resultAt(1);
                complete(Tuple2.create(t1, t2));
            } else {
                fail(ar.cause());
            }
        });
    }

    Tuple2TaskImpl(Future<T1> futureT1, Future<T2> futureT2) {
        this("Tuple2Task:", futureT1, futureT2);
    }

}
