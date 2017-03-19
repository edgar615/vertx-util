package com.edgar.util.vertx.wheel;

import io.vertx.core.Vertx;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by edgar on 17-3-19.
 */
public class DelayChecker {

  private final AtomicInteger cursor = new AtomicInteger(0);

  /**
   * 环形队列
   */
  private final Map<Integer, Set<Task>> wheelQueue = new ConcurrentHashMap<>();

  private AtomicLong currentTime = new AtomicLong();

  private final Vertx vertx;

  public DelayChecker(Vertx vertx) {
    this.vertx = vertx;
    vertx.setPeriodic(1000,l -> {
      currentTime.set(Instant.now().getEpochSecond());

    });
  }
}
