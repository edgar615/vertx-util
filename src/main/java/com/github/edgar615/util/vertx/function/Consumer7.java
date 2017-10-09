package com.github.edgar615.util.vertx.function;

import java.util.Objects;

/**
 * Created by Edgar on 2016/8/1.
 *
 * @author Edgar  Date 2016/8/1
 */
@FunctionalInterface
public interface Consumer7<T1, T2, T3, T4, T5, T6, T7> {
  void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

  default Consumer7<T1, T2, T3, T4, T5, T6, T7> andThen(
          Consumer7<T1, T2, T3, T4, T5, T6, T7> after) {
    Objects.requireNonNull(after);

    return (t1, t2, t3, t4, t5, t6, t7) -> {
      accept(t1, t2, t3, t4, t5, t6, t7);
      after.accept(t1, t2, t3, t4, t5, t6, t7);
    };
  }
}
