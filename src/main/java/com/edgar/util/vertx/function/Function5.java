package com.edgar.util.vertx.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by Edgar on 2016/8/1.
 *
 * @author Edgar  Date 2016/8/1
 */
@FunctionalInterface
public interface Function5<T1, T2, T3, T4, T5, R> {

    R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    default <V> Function5<T1, T2, T3, T4, T5, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) -> after.apply(apply(t1, t2, t3, t4, t5));
    }
}
