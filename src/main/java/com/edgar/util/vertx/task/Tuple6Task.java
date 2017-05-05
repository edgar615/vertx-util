package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Consumer6;
import com.edgar.util.vertx.function.Consumer7;
import com.edgar.util.vertx.function.Function6;
import com.edgar.util.vertx.function.Function7;
import com.edgar.util.vertx.function.Tuple6;
import io.vertx.core.Future;

/**
 * Created by Edgar on 2016/7/26.
 *
 * @author Edgar  Date 2016/7/26
 */
public interface Tuple6Task<T1, T2, T3, T4, T5, T6> extends Task<Tuple6<T1, T2, T3, T4, T5, T6>> {

  /**
   * 将Task<Tuple6<T1, T2, T3, T4, T5, T6>> 转换为Tuple6Task<T1, T2, T3, T4, T5, T6>
   *
   * @param task
   * @param <T1>
   * @param <T2> * @param <T3>
   * @return Tuple6Task<T1, T2, T3, T4, T5, T6>
   */
  default <T1, T2, T3, T4, T5, T6> Tuple6Task<T1, T2, T3, T4, T5, T6> cast(
          final Task<Tuple6<T1, T2, T3, T4, T5, T6>> task) {
    return new Tuple6TaskDelegate<>(task);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param function function类
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> map(Function6<T1, T2, T3, T4, T5, T6, R> function) {
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
  default <R> Task<R> mapWithFallback(Function6<T1, T2, T3, T4, T5, T6, R> function,
                                      Function7<Throwable, T1, T2, T3, T4, T5, T6, R> fallback) {
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
  default <R> Task<R> map(String desc, Function6<T1, T2, T3, T4, T5, T6, R> function) {
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
  default <R> Task<R> mapWithFallback(String desc, Function6<T1, T2, T3, T4, T5, T6, R> function,
                                      Function7<Throwable, T1, T2, T3, T4, T5, T6, R> fallback) {
    if (fallback == null) {
      return map(desc, t -> function.apply(t.getT1(), t.getT2(),
                                           t.getT3(), t.getT4(), t.getT5(), t.getT6()));
    }
    return mapWithFallback(desc, t -> function.apply(t.getT1(), t.getT2(),
                                                     t.getT3(), t.getT4(), t.getT5(), t.getT6()),
                           (r, t) -> fallback.apply(r, t.getT1(), t.getT2(),
                                                    t.getT3(), t.getT4(), t.getT5(), t.getT6()));
  }


  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param consumer consumer类
   * @return task
   */
  default Tuple6Task<T1, T2, T3, T4, T5, T6> andThen(Consumer6<T1, T2, T3, T4, T5, T6> consumer) {
    return andThenWithFallback(consumer, null);
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param consumer consumer类
   * @param fallback 如果consumer出现异常，回退的操作
   * @return task
   */
  default Tuple6Task<T1, T2, T3, T4, T5, T6> andThenWithFallback(
          Consumer6<T1, T2, T3, T4, T5, T6> consumer,
          Consumer7<Throwable, T1, T2, T3, T4, T5, T6> fallback) {
    return andThenWithFallback("andThen: " + consumer.getClass().getName(), consumer, fallback);
  }


  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param desc     任务描述
   * @param consumer consumer类
   * @return task
   */
  default Tuple6Task<T1, T2, T3, T4, T5, T6> andThen(String desc,
                                                     Consumer6<T1, T2, T3, T4, T5, T6> consumer) {
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
  default Tuple6Task<T1, T2, T3, T4, T5, T6> andThenWithFallback(
          String desc,
          Consumer6<T1, T2, T3, T4, T5, T6> consumer,
          Consumer7<Throwable, T1, T2, T3, T4, T5, T6> fallback) {
    if (fallback == null) {
      return cast(andThen(desc, t -> consumer.accept(t.getT1(), t.getT2(),
                                                     t.getT3(), t.getT4(), t.getT5(), t.getT6())));
    }
    return cast(andThenWithFallback(desc, t -> consumer.accept(t.getT1(), t.getT2(),
                                                               t.getT3(), t.getT4(), t.getT5(),
                                                               t.getT6()),
                                    (r, t) -> fallback.accept(r, t.getT1(), t.getT2(),
                                                              t.getT3(), t.getT4(), t.getT5(),
                                                              t.getT6())));
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMap(Function6<T1, T2, T3, T4, T5, T6, Future<R>> function) {
    return flatMapWithFallback(function, null);
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMapWithFallback(Function6<T1, T2, T3, T4, T5, T6, Future<R>> function,
                                          Function7<Throwable, T1, T2, T3, T4, T5, T6, R>
                                                  fallback) {
    return flatMapWithFallback("flatMap: " + function.getClass().getName(),
                               function,
                               fallback);
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param desc     任务描述
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMap(String desc, Function6<T1, T2, T3, T4, T5, T6, Future<R>> function) {
    return flatMap(desc, t -> function.apply(t.getT1(), t.getT2(),
                                             t.getT3(), t.getT4(), t.getT5(), t.getT6()));
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
  default <R> Task<R> flatMapWithFallback(String desc,
                                          Function6<T1, T2, T3, T4, T5, T6, Future<R>> function,
                                          Function7<Throwable, T1, T2, T3, T4, T5, T6, R>
                                                  fallback) {
    if (fallback == null) {
      return flatMap(desc, t -> function
              .apply(t.getT1(), t.getT2(), t.getT3(), t.getT4(), t.getT5(), t.getT6()));
    }
    return flatMapWithFallback(desc,
                               t -> function.apply(t.getT1(), t.getT2(),
                                                   t.getT3(), t.getT4(), t.getT5(), t.getT6()),
                               (r, t) -> fallback.apply(r, t.getT1(), t.getT2(), t.getT3(),
                                                        t.getT4(), t.getT5(), t.getT6()));
  }
}
