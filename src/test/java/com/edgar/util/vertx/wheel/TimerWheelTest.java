package com.edgar.util.vertx.wheel;

import io.vertx.core.Vertx;

import java.util.Random;

/**
 * Created by edgar on 17-3-17.
 */
public class TimerWheelTest {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.eventBus().consumer("add", added -> {
      System.out.println(added.body());
    });
    vertx.eventBus().consumer("remove", removed -> {
      System.out.println(removed.body());
    });
    TimerWheel wheel = new TimerWheel(vertx, 10);
    for (int i = 1; i < 100; i ++) {
      vertx.setTimer(new Random().nextInt(10) * 1000l, l -> {
        int r = new Random().nextInt(100);
        wheel.registerEvent(r);
      });

    }

  }
}
