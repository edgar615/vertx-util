package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Consumer3;
import com.edgar.util.vertx.function.Consumer4;
import com.edgar.util.vertx.function.Function3;
import com.edgar.util.vertx.function.Function4;
import com.edgar.util.vertx.function.Tuple3;
import io.vertx.core.Future;

/**
 * Created by Edgar on 2016/7/26.
 *
 * @author Edgar  Date 2016/7/26
 */
public interface Tuple3Task<T1, T2, T3> extends Task<Tuple3<T1, T2, T3>> {

  /**
   * 将Task<Tuple3<T1, T2, T3>> 转换为Tuple3Task<T1, T2, T3>
   *
   * @param task
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @return Tuple3Task<T1, T2, T3>
   */
  default <T1, T2, T3> Tuple3Task<T1, T2, T3> cast(final Task<Tuple3<T1, T2, T3>> task) {
    return new Tuple3TaskDelegate<>(task);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param function function类
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> map(Function3<T1, T2, T3, R> function) {
    return mapWithFallback(function, null);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param function function类
   * @param fallback 如果function出现异常，回退的操作
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> mapWithFallback(Function3<T1, T2, T3, R> function,
                                      Function4<Throwable, T1, T2, T3, R> fallback) {
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
  default <R> Task<R> map(String desc, Function3<T1, T2, T3, R> function) {
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
  default <R> Task<R> mapWithFallback(String desc, Function3<T1, T2, T3, R> function,
                                      Function4<Throwable, T1, T2, T3, R> fallback) {
    if (fallback == null) {
      return map(desc, t -> function.apply(t.getT1(), t.getT2(), t.getT3()));
    }
    return mapWithFallback(desc, t -> function.apply(t.getT1(), t.getT2(), t.getT3()),
                           ((r, t) ->
                                   fallback.apply(r, t.getT1(), t.getT2(), t.getT3())));
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param consumer consumer类
   * @return task
   */
  default Tuple3Task<T1, T2, T3> andThen(Consumer3<T1, T2, T3> consumer) {
    return andThenWithFallback(consumer, null);
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param consumer consumer类
   * @param fallback 如果consumer出现异常，回退的操作
   * @return task
   */
  default Tuple3Task<T1, T2, T3> andThenWithFallback(Consumer3<T1, T2, T3> consumer,
                                                     Consumer4<Throwable, T1, T2, T3> fallback) {
    return andThenWithFallback("andThen: " + consumer.getClass().getName(), consumer, fallback);
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param desc     任务描述
   * @param consumer consumer类
   * @return task
   */
  default Tuple3Task<T1, T2, T3> andThen(String desc, Consumer3<T1, T2, T3> consumer) {
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
  default Tuple3Task<T1, T2, T3> andThenWithFallback(String desc, Consumer3<T1, T2, T3> consumer,
                                                     Consumer4<Throwable, T1, T2, T3> fallback) {
    if (fallback == null) {
      return cast(andThen(desc, t -> consumer.accept(t.getT1(), t.getT2(), t.getT3())));
    }
    return cast(andThenWithFallback(desc, t -> consumer.accept(t.getT1(), t.getT2(), t.getT3()),
                                    ((r, t) -> fallback
                                            .accept(r, t.getT1(), t.getT2(), t.getT3()))));
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMap(Function3<T1, T2, T3, Future<R>> function) {
    return flatMap("flatMap: " + function.getClass().getName(), t -> function.apply(t.getT1(), t
            .getT2(), t.getT3()));
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param function function类，将结果转换为一个新的future
   * @param fallback 如果function出现异常，回退的操作. 这个回退操作，不再返回一个Future,而是直接返回一个特定的值
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMapWithFallback(Function3<T1, T2, T3, Future<R>> function,
                                          Function4<Throwable, T1, T2, T3, R> fallback) {
    return flatMapWithFallback("flatMap: " + function.getClass().getName(), function, fallback);
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param desc     任务描述
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMap(String desc, Function3<T1, T2, T3, Future<R>> function) {
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
  default <R> Task<R> flatMapWithFallback(String desc, Function3<T1, T2, T3, Future<R>> function,
                                          Function4<Throwable, T1, T2, T3, R> fallback) {
    if (fallback == null) {
      return flatMap(desc, t -> function.apply(t.getT1(), t.getT2(), t.getT3()));
    }
    return flatMapWithFallback(desc, t -> function.apply(t.getT1(), t.getT2(), t.getT3()),
                               ((r, t) -> fallback.apply(r, t.getT1(), t.getT2(), t.getT3())));
  }
}
