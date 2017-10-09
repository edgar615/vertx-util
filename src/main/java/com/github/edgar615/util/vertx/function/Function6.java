package com.github.edgar615.util.vertx.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by Edgar on 2016/8/1.
 *
 * @author Edgar  Date 2016/8/1
 */
@FunctionalInterface
public interface Function6<T1, T2, T3, T4, T5, T6, R> {

  R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

  default <V> Function6<T1, T2, T3, T4, T5, T6, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return (T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) -> after.apply(apply(t1, t2, t3, t4, t5, t6));
  }
}
