package com.github.edgar615.util.vertx.cache;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2017/10/27.
 *
 * @author Edgar  Date 2017/10/27
 */
public class GuavaCache<K, V> implements Cache<K, V> {

  private final Vertx vertx;

  private final com.google.common.cache.Cache<K, V> cache;

  public GuavaCache(Vertx vertx, GuavaCacheOptions options) {
    this.vertx = vertx;
    CacheBuilder builder = CacheBuilder.newBuilder()
            .ticker(Ticker.systemTicker());
    if (options.getExpireAfterAccess() != null) {
      builder.expireAfterAccess(options.getExpireAfterAccess(), TimeUnit.SECONDS);
    }
    if (options.getExpireAfterWrite() != null) {
      builder.expireAfterWrite(options.getExpireAfterWrite(), TimeUnit.SECONDS);
    }
    if (options.getMaximumSize() != null) {
      builder.maximumSize(options.getMaximumSize());
    }
    this.cache = builder.build();
  }

  @Override
  public void get(K key, Handler<AsyncResult<V>> resultHandler) {
    V value;
    try {
      value = cache.getIfPresent(key);
      resultHandler.handle(Future.succeededFuture(value));
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public void get(K key, CacheLoader<K, V> cacheLoader,
                  Handler<AsyncResult<V>> resultHandler) {
    V value;
    try {
      value = cache.getIfPresent(key);
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
      return;
    }
    if (value == null) {
      cacheLoader.load(key, ar -> {
        if (ar.succeeded() && ar.result() != null) {
          cache.put(key, ar.result());
          resultHandler.handle(Future.succeededFuture(ar.result()));
        } else {
          resultHandler.handle(Future.succeededFuture(null));
        }
      });
    } else {
      resultHandler.handle(Future.succeededFuture(value));
    }
  }

  @Override
  public void put(K key, V value, Handler<AsyncResult<Void>> resultHandler) {
    try {
      cache.put(key, value);
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }

  }

  @Override
  public void evict(K key, Handler<AsyncResult<V>> resultHandler) {
    try {
      cache.invalidate(key);
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public void put(K key, V value, CacheWriter<K, V> cacheWriter,
                  Handler<AsyncResult<Void>> resultHandler) {
    cacheWriter.write(key, value, ar -> {
      if (ar.succeeded()) {
        //如果缓存更新失败，会有数据不一致问题
        put(key, value, resultHandler);
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void evict(K key, CacheEvictor<K> cacheEvictor,
                    Handler<AsyncResult<V>> resultHandler) {
    cacheEvictor.delete(key, ar -> {
      if (ar.succeeded()) {
        //如果缓存删除失败，会有数据不一致问题
        evict(key, resultHandler);
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

}
