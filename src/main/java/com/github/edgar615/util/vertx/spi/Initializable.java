package com.github.edgar615.util.vertx.spi;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 启动接口.
 * 凡是继承该接口的类再应用启动之后都应该执行initialize方法，执行初始化工作.
 *
 * @author Edgar  Date 2017/3/30
 */
public interface Initializable {

  /**
   * 启动方法.
   *
   * @param vertx    Vertx
   * @param config   配置
   * @param complete 回调
   */
  void initialize(Vertx vertx, JsonObject config, Future<Void> complete);
}
