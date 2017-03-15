package com.edgar.util.vertx.task;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * vert.x异步任务的工具类.
 * <p>
 * taks大部分的设计都参考了linkedin的parseq实现.
 *
 * @author Edgar  Date 2016/5/10
 */
public interface Task<T> {

  Logger LOGGER = LoggerFactory.getLogger(Task.class);

  /**
   * 返回任务的执行结果
   *
   * @return 结果
   */
  T result();

  /**
   * 任务名称
   *
   * @return 名称
   */
  String name();

  /**
   * 任务成功完成.
   *
   * @param result 任务的成功返回值
   */
  void complete(T result);

  /**
   * 任务失败.
   *
   * @param throwable 任务的异常
   */
  void fail(Throwable throwable);

  /**
   * 判断任务是否完成
   *
   * @return
   */
  boolean isComplete();

  /**
   * 设置任务的回调函数
   *
   * @param handler
   */
  void setHandler(Handler<AsyncResult<T>> handler);

  /**
   * 返回任务的回调函数
   *
   * @return hander
   */
  Handler<AsyncResult<T>> completer();

  /**
   * 处理任务的日志.
   * 日志会一直在task中传播，所以通常只需要在task链的最后一个task上处理即可.
   *
   * @param traceHandler 日志的处理对象
   * @return task
   */
  Task<T> onTrace(Consumer<List<Trace>> traceHandler);

  /**
   * 创建一个异步任务.
   * 该任务需要最后调用complete方法
   *
   * @param <T>
   * @return task
   */
  public static <T> Task<T> create() {
    return new BaseTask<>(Future.<T>future());
  }

  /**
   * 创建一个异步任务
   *
   * @param name 任务名称
   * @param <T>
   * @return task
   */
  public static <T> Task<T> create(String name) {
    return new BaseTask<>(name, Future.<T>future());
  }

  /**
   * 基于Future创建一个异步任务.
   *
   * @param name   任务名称
   * @param future Future
   * @param <T>
   * @return task
   */
  public static <T> Task<T> create(String name, Future<T> future) {
    return new BaseTask<>(name, future);
  }

  /**
   * 基于Future创建一个异步任务.
   *
   * @param future Future
   * @param <T>
   * @return task
   */
  public static <T> Task<T> create(Future<T> future) {
    return new BaseTask<>(future);
  }

  /**
   * 创建两个并行的任务.
   *
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param <T1>
   * @param <T2>
   * @return task
   */
  public static <T1, T2> Tuple2Task<T1, T2> par(Future<T1> futureT1, Future<T2> futureT2) {
    return new Tuple2TaskImpl<>(futureT1, futureT2);
  }

  /**
   * 创建两个并行的任务.
   *
   * @param name     任务名
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param <T1>
   * @param <T2>
   * @return task
   */
  public static <T1, T2> Tuple2Task<T1, T2> par(String name, Future<T1> futureT1,
                                                Future<T2> futureT2) {
    return new Tuple2TaskImpl<>(name, futureT1, futureT2);
  }

  /**
   * 创建三个并行的任务.
   *
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param futureT3 任务3
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @return task
   */
  public static <T1, T2, T3> Tuple3Task<T1, T2, T3> par(Future<T1> futureT1, Future<T2> futureT2,
                                                        Future<T3> futureT3) {
    return new Tuple3TaskImpl<>(futureT1, futureT2, futureT3);
  }

  /**
   * 创建三个并行的任务.
   *
   * @param name     任务名
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param futureT3 任务3
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @return task
   */
  public static <T1, T2, T3> Tuple3Task<T1, T2, T3> par(String name, Future<T1> futureT1,
                                                        Future<T2> futureT2, Future<T3> futureT3) {
    return new Tuple3TaskImpl<>(name, futureT1, futureT2, futureT3);
  }

  /**
   * 创建四个并行的任务.
   *
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param futureT3 任务3
   * @param futureT4 任务4
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param <T4>
   * @return task
   */
  public static <T1, T2, T3, T4> Tuple4Task<T1, T2, T3, T4> par(Future<T1> futureT1, Future<T2>
          futureT2, Future<T3> futureT3, Future<T4> futureT4) {
    return new Tuple4TaskImpl<>(futureT1, futureT2, futureT3, futureT4);
  }

  /**
   * 创建四个并行的任务.
   *
   * @param name     任务名
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param futureT3 任务3
   * @param futureT4 任务4
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param <T4>
   * @return task
   */
  public static <T1, T2, T3, T4> Tuple4Task<T1, T2, T3, T4> par(String name, Future<T1>
          futureT1, Future<T2> futureT2, Future<T3> futureT3, Future<T4> futureT4) {
    return new Tuple4TaskImpl<>(name, futureT1, futureT2, futureT3, futureT4);
  }

  /**
   * 创建五个并行的任务.
   *
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param futureT3 任务3
   * @param futureT4 任务4
   * @param futureT5 任务5
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param <T4>
   * @param <T5>
   * @return task
   */
  public static <T1, T2, T3, T4, T5> Tuple5Task<T1, T2, T3, T4, T5> par(Future<T1> futureT1,
                                                                        Future<T2> futureT2,
                                                                        Future<T3> futureT3,
                                                                        Future<T4> futureT4,
                                                                        Future<T5> futureT5) {
    return new Tuple5TaskImpl<>(futureT1, futureT2, futureT3, futureT4, futureT5);
  }

  /**
   * 创建五个并行的任务.
   *
   * @param name     任务名
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param futureT3 任务3
   * @param futureT4 任务4
   * @param futureT5 任务5
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param <T4>
   * @param <T5>
   * @return task
   */
  public static <T1, T2, T3, T4, T5> Tuple5Task<T1, T2, T3, T4, T5> par(String name,
                                                                        Future<T1> futureT1,
                                                                        Future<T2> futureT2,
                                                                        Future<T3> futureT3,
                                                                        Future<T4> futureT4,
                                                                        Future<T5> futureT5) {
    return new Tuple5TaskImpl<>(name, futureT1, futureT2, futureT3, futureT4, futureT5);
  }

  /**
   * 创建六个并行的任务.
   *
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param futureT3 任务3
   * @param futureT4 任务4
   * @param futureT5 任务5
   * @param futureT6 任务6
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param <T4>
   * @param <T5>
   * @param <T6>
   * @return task
   */
  public static <T1, T2, T3, T4, T5, T6> Tuple6Task<T1, T2, T3, T4, T5, T6> par(Future<T1> futureT1,
                                                                                Future<T2> futureT2,
                                                                                Future<T3> futureT3,
                                                                                Future<T4> futureT4,
                                                                                Future<T5> futureT5,
                                                                                Future<T6>
                                                                                        futureT6) {
    return new Tuple6TaskImpl<>(futureT1, futureT2, futureT3, futureT4, futureT5, futureT6);
  }

  /**
   * 创建六个并行的任务.
   *
   * @param name     任务名
   * @param futureT1 任务1
   * @param futureT2 任务2
   * @param futureT3 任务3
   * @param futureT4 任务4
   * @param futureT5 任务5
   * @param futureT6 任务6
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param <T4>
   * @param <T5>
   * @param <T6>
   * @return task
   */
  public static <T1, T2, T3, T4, T5, T6> Tuple6Task<T1, T2, T3, T4, T5, T6> par(String name,
                                                                                Future<T1> futureT1,
                                                                                Future<T2> futureT2,
                                                                                Future<T3> futureT3,
                                                                                Future<T4> futureT4,
                                                                                Future<T5> futureT5,
                                                                                Future<T6>
                                                                                        futureT6) {
    return new Tuple6TaskImpl<>(name, futureT1, futureT2, futureT3, futureT4, futureT5,
                                futureT6);
  }

  /**
   * 创建多个并行任务.
   *
   * @param futures 任务列表
   * @param <T>
   * @return task
   */
  public static <T> Task<List<T>> par(List<Future<T>> futures) {
    return new ParTaskImpl<>(futures);
  }

  /**
   * 创建多个并行任务.
   *
   * @param name    任务名
   * @param futures 任务列表
   * @param <T>
   * @return task
   */
  public static <T> Task<List<T>> par(String name, List<Future<T>> futures) {
    return new ParTaskImpl<>(name,futures);
  }

  /**
   * 处理任务遇到的异常.
   * 异常会一直在task中传播，所以通常只需要在task链的最后一个task上捕获即可。
   *
   * @param desc     任务描述
   * @param consumer 异常处理对象
   * @return task
   */
  default Task<T> onFailure(String desc, Consumer<Throwable> consumer) {
    return new FusionTask<>(desc, this, (prev, next) -> {
      prev.setHandler(ar -> {
        Throwable throwable = ar.cause();
        if (ar.succeeded()) {
          next.complete(prev.result());
        }
        if (throwable != null) {
          try {
            consumer.accept(throwable);
            LOGGER.debug("task->{} consumer throwable succeeded", prev.name());
          } catch (Exception e) {
            throwable = e;
            LOGGER.debug("task->{} consumer throwable failed, error->{}", prev.name(), throwable.getMessage());
          }
          next.fail(throwable);
        }
      });
    });
  }

  /**
   * 处理任务遇到的异常.
   * 异常会一直在task中传播，所以通常只需要在task链的最后一个task上捕获即可。
   *
   * @param consumer 异常处理对象
   * @return task
   */
  default Task<T> onFailure(Consumer<Throwable> consumer) {
    return onFailure("onFailure: " + consumer.getClass().getName(), consumer);
  }

  /**
   * 将task从异常中恢复
   *
   * @param desc     任务描述
   * @param function 异常转换类
   * @return 新任务
   */
  // Task<T> recover(String desc, Function<Throwable, T> function);
  default Task<T> recover(String desc, Function<Throwable, T> function) {
    return new FusionTask<>(null, this, (prev, next) -> {
      prev.setHandler(ar -> {
        Throwable throwable = ar.cause();
        if (ar.succeeded()) {
          next.complete(prev.result());
        }
        if (throwable != null) {
          try {
            next.complete(function.apply(throwable));
            LOGGER.debug("task->{} recover from throwable succeeded", prev.name());
          } catch (Exception e) {
            throwable = e;
            LOGGER.debug("task->{} recover from throwable failed, error->{}", prev.name(), throwable.getMessage());
            next.fail(throwable);
          }
        }
      });
    });
  }

  /**
   * 将task从异常中恢复
   *
   * @param function 异常转换类
   * @return 新任务
   */
  default Task<T> recover(Function<Throwable, T> function) {
    return recover("recover: " + function.getClass().getName(), function);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param function function类
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> map(Function<T, R> function) {
    return map("map: " + function.getClass().getName(), function);
  }

  /**
   * 任务完成之后，将结果转换为其他对象.
   *
   * @param desc     任务描述
   * @param function function类
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> map(String desc, Function<T, R> function) {
    return new FusionTask<>(desc, this, (prev, next) -> {
      prev.setHandler(ar -> {
        Throwable throwable = ar.cause();
        if (ar.succeeded()) {
          try {
            next.complete(function.apply(ar.result()));
            LOGGER.debug("task->{} map to task->{} succeeded", prev.name(), next.name());
          } catch (Exception e) {
            throwable = e;
            LOGGER.debug("task->{} map to task->{} failed, error->{}", prev.name(), next.name(), throwable.getMessage());
          }
        }
        if (throwable != null) {
          next.fail(throwable);
        }
      });
    });
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param desc     任务描述
   * @param consumer consumer类
   * @return task
   */
  default Task<T> andThen(String desc, Consumer<T> consumer) {
    return map(desc, t -> {
      consumer.accept(t);
      return t;
    });
  }

  /**
   * 任务完成后，根据结果做一些额外操作.
   *
   * @param consumer consumer类
   * @return task
   */
  default Task<T> andThen(Consumer<T> consumer) {
    return andThen("andThen: " + consumer.getClass().getName(), consumer);
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param function function类，将结果转换为一个新的future
   * @param <R>      转换后的类型
   * @return task
   */
  default <R> Task<R> flatMap(Function<T, Future<R>> function) {
    return flatMap("flatMap: " + function.getClass().getName(), function);
  }

  /**
   * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
   *
   * @param desc     任务描述
   * @param function function类，将结果转换为一个新的future
   * @param <R>
   * @return
   */
  default <R> Task<R> flatMap(String desc, Function<T, Future<R>> function) {
    return new FusionTask<>(desc, this, (prev, next) -> {
      prev.setHandler(ar -> {
        Throwable throwable = ar.cause();
        if (ar.succeeded()) {
          try {
            Future<R> rFuture = function.apply(ar.result());
            rFuture.setHandler(next.completer());
            LOGGER.debug("task->{} flatMap to task->{} succeeded", prev.name(), next.name());
          } catch (Exception e) {
            throwable = e;
            LOGGER.debug("task->{} flatMap to task->{} failed, error->{}", prev.name(), next.name(), throwable.getMessage());
          }
        }
        if (throwable != null) {
          next.fail(throwable);
        }
      });
    });
  }

  /**
   * 将task转换为一个Future
   *
   * @param <T>
   * @return future
   */
  default <T> Future<T> toFutrue() {
    Future future = Future.future();
    andThen(t -> future.complete(t))
            .onFailure(throwable -> future.fail(throwable));
    return future;
  }


}
