package com.github.edgar615.util.vertx.cache;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Cache<K, V> {

  /**
   * cache的名称
   *
   * @return
   */
  String name();

  /**
   * 从缓存中取出一个值
   *
   * @param key           缓存键
   * @param resultHandler 缓存的结果处理，不存在的数据返回null, 需要注意对null的处理.
   */
  void get(K key, Handler<AsyncResult<V>> resultHandler);

  /**
   * 从缓存中取出一个值，如果缓存中不存在这个值，则通过cacheLoader从后端查询这个值。<b>Read Through模式</b>
   * <p>
   * 这个方法存在缓存穿透的问题：用户查询的数据在数据库一定没有，自然在缓存中也不会有。这样就导致用户查询的时候，每次都要去数据库中查询。在流量大时，可能数据库就挂掉了。
   * 为了避免缓存穿透，需要cacheLoader将不存在的值预设为一个特定的值，由调用方判断.
   * <p>
   * 因为Vert.x线程的特殊性，所以这个方法并不考虑缓存并发的问题，但是如果cacheLoader是自己实现的多线程调用，那么需要在cacheLoader内部考虑使用锁避免缓存并发.
   * 缓存并发是指一个缓存如果失效，可能出现多个进程同时查询DB，同时设置缓存的情况，如果并发确实很大，这也可能造成DB压力过大，还有缓存频繁更新的问题。
   *
   * @param key           缓存键
   * @param cacheLoader   从后端加载不存在值的方法，如果出现异常，返回给缓存的结果为null
   * @param resultHandler 缓存的结果处理，不存在的数据返回null, 需要注意对null的处理.
   */
  void get(K key, CacheLoader<K, V> cacheLoader, Handler<AsyncResult<V>> resultHandler);

  /**
   * 向缓存中放入一个值
   *
   * @param key           缓存键
   * @param value         缓存值
   * @param resultHandler 回调函数
   */
  void put(K key, V value, Handler<AsyncResult<Void>> resultHandler);

  /**
   * 删除缓存中的某个值
   *
   * @param key           缓存键
   * @param resultHandler 回调函数
   */
  void evict(K key, Handler<AsyncResult<V>> resultHandler);

  /**
   * 向缓存中放入一个值，并通过cacheWriter写入后端 <b>Write Through模式</b>
   * <p>
   * 先更新后端，在更新缓存。
   * 如果将CacheWriter改为使用Eventbus发送消息，则实现了Write Behind Caching Pattern模式
   * <p>
   * 假设先写数据库，再更新缓存：第一步写DB操作成功，第二步更新缓存失败，则会出现DB中是新数据，Cache中是旧数据，数据不一致。
   * <p>
   * 假设先更新缓存，再写数据库：第一步更新缓存成功，第二步写更新DB失败，则会出现DB中是旧数据，Cache中是新数据，数据不一致。
   * <p>
   * 考虑到写DB失败的几率比写缓存失败的几率要高的多，所以这个方法采用先写DB，再写缓存的顺序进行。
   *
   * @param key           缓存键
   * @param value         缓存值
   * @param cacheWriter   向后端写入数据的方法
   * @param resultHandler 回调函数
   */
  void put(K key, V value, CacheWriter<K, V> cacheWriter, Handler<AsyncResult<Void>> resultHandler);

  /**
   * 删除缓存中的某个值，并删除后端数据 <b>Write Through模式</b>
   * <p>
   * 先删除后端，在删除缓存
   *
   * @param key           缓存键
   * @param cacheEvictor  向后端删除数据的方法
   * @param resultHandler 回调函数
   */
  void evict(K key, CacheEvictor<K> cacheEvictor, Handler<AsyncResult<V>> resultHandler);

}