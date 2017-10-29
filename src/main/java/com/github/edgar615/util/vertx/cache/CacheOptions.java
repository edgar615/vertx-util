package com.github.edgar615.util.vertx.cache;

import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2017/10/27.
 *
 * @author Edgar  Date 2017/10/27
 */
public class CacheOptions {

  /**
   * 单位秒
   */
  private Long expireAfterWrite;

  /**
   * 单位秒
   */
  private Long expireAfterAccess;

  /**
   * 最大数量
   */
  private Long maximumSize;

  public CacheOptions() {
  }

  public CacheOptions(JsonObject json) {
    this();
    CacheOptionsConverter.fromJson(json, this);
  }


  public Long getExpireAfterWrite() {
    return expireAfterWrite;
  }

  public CacheOptions setExpireAfterWrite(Long expireAfterWrite) {
    this.expireAfterWrite = expireAfterWrite;
    return this;
  }

  public Long getExpireAfterAccess() {
    return expireAfterAccess;
  }

  public CacheOptions setExpireAfterAccess(Long expireAfterAccess) {
    this.expireAfterAccess = expireAfterAccess;
    return this;
  }

  public Long getMaximumSize() {
    return maximumSize;
  }

  public CacheOptions setMaximumSize(Long maximumSize) {
    this.maximumSize = maximumSize;
    return this;
  }
}
