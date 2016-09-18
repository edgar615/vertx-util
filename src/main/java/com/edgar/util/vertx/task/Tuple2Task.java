package com.edgar.util.vertx.task;

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
    default <R> Task<R> map(String desc, BiFunction<T1, T2, R> function) {
        return map(desc, t -> function.apply(t.getT1(), t.getT2()));
    }

    /**
     * 任务完成后，根据结果做一些额外操作.
     *
     * @param consumer consumer类
     * @return task
     */
    default Tuple2Task<T1, T2> andThen(BiConsumer<T1, T2> consumer) {
        return andThen("andThen: " + consumer.getClass().getName(), consumer);
    }

    /**
     * 任务完成后，根据结果做一些额外操作.
     *
     * @param desc     任务描述
     * @param consumer consumer类
     * @return task
     */
    default Tuple2Task<T1, T2> andThen(String desc, BiConsumer<T1, T2> consumer) {
        return cast(andThen(desc, t -> consumer.accept(t.getT1(), t.getT2())));
    }

    /**
     * 任务完成之后，让结果传递给另外一个任务执行，futureFunction用来使用结果创建一个新的任务.
     *
     * @param function function类，将结果转换为一个新的future
     * @param <R>
     * @return
     */
    default <R> Task<R> flatMap(BiFunction<T1, T2, Future<R>> function) {
        return flatMap("map: " + function.getClass().getName(), t -> function.apply(t.getT1(), t.getT2()));
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
        return flatMap(desc, t -> function.apply(t.getT1(), t.getT2()));
    }
}
