package com.edgar.util.vertx.task;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar on 2016/7/21.
 *
 * @author Edgar  Date 2016/7/21
 */
class ParTaskImpl<T> extends BaseTask<List<T>> {

    ParTaskImpl(List<Future<T>> futures) {
        super(null, Future.<List<T>>future());
        final int size = futures.size();
        List<Future> copyFuture = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            copyFuture.add(futures.get(i));
        }
        CompositeFuture compositeFuture = CompositeFuture.all(copyFuture);
        compositeFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                List<T> results = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    results.add((T) ar.result().result(i));
                }
                complete(results);
            } else {
                fail(ar.cause());
            }
        });
    }

}
