package com.github.edgar615.util.vertx.cache;

/**
 * Created by Edgar on 2017/10/27.
 *
 * @author Edgar  Date 2017/10/27
 */
public class GuavaCacheOptions {

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

  public Long getExpireAfterWrite() {
    return expireAfterWrite;
  }

  public GuavaCacheOptions setExpireAfterWrite(Long expireAfterWrite) {
    this.expireAfterWrite = expireAfterWrite;
    return this;
  }

  public Long getExpireAfterAccess() {
    return expireAfterAccess;
  }

  public GuavaCacheOptions setExpireAfterAccess(Long expireAfterAccess) {
    this.expireAfterAccess = expireAfterAccess;
    return this;
  }

  public Long getMaximumSize() {
    return maximumSize;
  }

  public GuavaCacheOptions setMaximumSize(Long maximumSize) {
    this.maximumSize = maximumSize;
    return this;
  }
}
