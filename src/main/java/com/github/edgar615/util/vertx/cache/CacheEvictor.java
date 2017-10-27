package com.github.edgar615.util.vertx.cache;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by Edgar on 2017/10/27.
 *
 * @author Edgar  Date 2017/10/27
 */
public interface CacheEvictor<K> {

  void delete(K key, Handler<AsyncResult<Void>> resultHandler);
}
