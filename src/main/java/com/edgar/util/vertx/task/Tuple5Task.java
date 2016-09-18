package com.edgar.util.vertx.task;

import com.edgar.util.vertx.function.Consumer5;
import com.edgar.util.vertx.function.Function5;
import com.edgar.util.vertx.function.Tuple5;
import io.vertx.core.Future;

/**
 * Created by Edgar on 2016/7/26.
 *
 * @author Edgar  Date 2016/7/26
 */
public interface Tuple5Task<T1, T2, T3, T4, T5> extends Task<Tuple5<T1, T2, T3, T4, T5>> {

    /**
     * 将Task<Tuple5<T1, T2, T3, T4, T5>> 转换为Tuple5Task<T1, T2, T3, T4, T5>
     *
     * @param task
     * @param <T1>
     * @param <T2> * @param <T3>
     * @return Tuple5Task<T1, T2, T3, T4, T5>
     */
    default <T1, T2, T3, T4, T5> Tuple5Task<T1, T2, T3, T4, T5> cast(final Task<Tuple5<T1, T2, T3, T4, T5>> task) {
        return new Tuple5TaskDelegate<>(task);
    }

    /**
     * 任务完成之后，将结果转换为其他对象.
     *
     * @param function function类
     * @param <R>      转换后的类型
     * @return task
     */
    default <R> Task<R> map(Function5<T1, T2, T3, T4, T5, R> function) {
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
    default <R> Task<R> map(String desc, Function5<T1, T2, T3, T4, T5, R> function) {
        return map(desc, t -> function.apply(t.getT1(), t.getT2(), t.getT3(), t.getT4(), t.getT5()));
    }

    /**
     * 任务完成后，根据结果做一些额外操作.
     *
     * @param consumer consumer类
     * @return task
     */
    default Tuple5Task<T1, T2, T3, T4, T5> andThen(Consumer5<T1, T2, T3, T4, T5> consumer) {
        return andThen("andThen: " + consumer.getClass().getName(), consumer);
    }

    /**
     * 任务完成后，根据结果做一些额外操作.
     *
     * @param desc     任务描述
     * @param consumer consumer类
     * @return task
     */
    default Tuple5Task<T1, T2, T3, T4, T5> andThen(String desc, Consumer5<T1, T2, T3, T4, T5> consumer) {
        return cast(andThen(desc, t -> consumer.accept(t.getT1(), t.getT2(), t.getT3(), t.getT4(), t.getT5())));
    }

    /**
     * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
     *
     * @param function function类，将结果转换为一个新的future
     * @param <R>
     * @return
     */
    default <R> Task<R> flatMap(Function5<T1, T2, T3, T4, T5, Future<R>> function) {
        return flatMap("map: " + function.getClass().getName(), t -> function.apply(t.getT1(), t.getT2(), t.getT3(), t.getT4(), t.getT5()));
    }

    /**
     * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
     *
     * @param desc     任务描述
     * @param function function类，将结果转换为一个新的future
     * @param <R>
     * @return
     */
    default <R> Task<R> flatMap(String desc, Function5<T1, T2, T3, T4, T5, Future<R>> function) {
        return flatMap(desc, t -> function.apply(t.getT1(), t.getT2(), t.getT3(), t.getT4(), t.getT5()));
    }
}
