package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Consumer3;
import com.edgar.util.vertx.function.Function3;
import com.edgar.util.vertx.function.Tuple2;
import io.vertx.core.Future;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Created by Edgar on 2016/7/26.
 *
 * @author Edgar  Date 2016/7/26
 */
public interface Tuple2Task<T1, T2> extends Task<Tuple2<T1, T2>> {

  /**
   * 将Task<Tuple2<T1, T2>> 转换为Tuple2Task<T1, T2>
   *
   * @param task
   * @param <T1>
   * @param <T2>
   * @return Tuple2Task<T1, T2>
   */
  default <T1, T2> Tuple2Task<T1, T2> cast(final Task<Tuple2<T1, T2>> task) {
    return new Tuple2TaskDelegate<>(task);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param function function类
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> map(BiFunction<T1, T2, R> function) {
    return mapWithFallback(function, null);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param function function类
   * @param <R>      转换后的类型
   * @param fallback 如果function出现异常，回退的操作
   * @return task
   */
  default <R> Task<R> mapWithFallback(BiFunction<T1, T2, R> function,
                                      Function3<Throwable, T1, T2, R> fallback) {
    return mapWithFallback("map: " + function.getClass().getName(), function, fallback);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param desc     任务描述
   * @param function function类
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> map(String desc, BiFunction<T1, T2, R> function) {
    return mapWithFallback(desc, function, null);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param desc     任务描述
   * @param function function类
   * @param fallback 如果function出现异常，回退的操作
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> mapWithFallback(String desc, BiFunction<T1, T2, R> function,
                                      Function3<Throwable, T1, T2, R> fallback) {
    if (fallback == null) {
      return map(desc, t -> function.apply(t.getT1(), t.getT2()));
    }
    return mapWithFallback(desc, t -> function.apply(t.getT1(), t.getT2()),
                           (throwable, t) -> fallback.apply(throwable, t.getT1(), t.getT2()));
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param consumer consumer类
   * @return task
   */
  default Tuple2Task<T1, T2> andThen(BiConsumer<T1, T2> consumer) {
    return andThenWithFallback(consumer, null);
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param consumer consumer类
   * @param fallback 如果consumer出现异常，回退的操作
   * @return task
   */
  default Tuple2Task<T1, T2> andThenWithFallback(BiConsumer<T1, T2> consumer,
                                                 Consumer3<Throwable, T1, T2> fallback) {
    return andThenWithFallback("andThen: " + consumer.getClass().getName(), consumer, fallback);
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param desc     任务描述
   * @param consumer consumer类
   * @return task
   */
  default Tuple2Task<T1, T2> andThen(String desc, BiConsumer<T1, T2> consumer) {
    return andThenWithFallback(desc, consumer, null);
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param desc     任务描述
   * @param consumer consumer类
   * @param fallback 如果consumer出现异常，回退的操作
   * @return task
   */
  default Tuple2Task<T1, T2> andThenWithFallback(String desc, BiConsumer<T1, T2> consumer,
                                                 Consumer3<Throwable, T1, T2> fallback) {
    if (fallback == null) {
      return cast(andThen(desc, t -> consumer.accept(t.getT1(), t.getT2())));
    }
    return cast(andThenWithFallback(desc, t -> consumer.accept(t.getT1(), t.getT2()),
                                    (throwalbe, t) -> fallback
                                            .accept(throwalbe, t.getT1(), t.getT2())));
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMap(BiFunction<T1, T2, Future<R>> function) {
    return flatMapWithFallback(function, null);
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMapWithFallback(BiFunction<T1, T2, Future<R>> function,
                                          Function3<Throwable, T1, T2, R> fallback) {
    return flatMapWithFallback("flatMap: " + function.getClass().getName(),
                               function, fallback);
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param desc     任务描述
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMap(String desc, BiFunction<T1, T2, Future<R>> function) {
    return flatMapWithFallback(desc, function, null);
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param desc     任务描述
   * @param function function类，将结果转换为一个新的future
   * @param fallback 如果function出现异常，回退的操作. 这个回退操作，不再返回一个Future,而是直接返回一个特定的值
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMapWithFallback(String desc, BiFunction<T1, T2, Future<R>> function,
                                          Function3<Throwable, T1, T2, R> fallback) {
    if (fallback == null) {
      return flatMap(desc, t -> function.apply(t.getT1(), t.getT2()));
    } else {
      return flatMapWithFallback(desc, t -> function.apply(t.getT1(), t.getT2()),
                                 ((throwable, t) -> fallback
                                         .apply(throwable, t.getT1(), t.getT2())));
    }
  }
}
