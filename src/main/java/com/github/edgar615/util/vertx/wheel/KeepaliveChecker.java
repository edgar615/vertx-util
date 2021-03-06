package com.github.edgar615.util.vertx.wheel;

import io.vertx.core.Vertx;

/**
 * 假设对事件做30秒的定时检测,创建一个环形队列从0到30表示30秒,每隔一秒在队列中移动一格,并使用一个指针记录移动对位置.
 * 每个队列存放一个集合,记录任务集合
 * +----+----+----+----+----+----+
 * | 0  | 1  | 2  | 3  |... | 30 |
 * +----+----+----+----+----+----+
 * ^
 * <p>
 * 如果在第一秒,指针停留在第0格,收到A和B的事件,将A和B记录在第30格中.
 * +----+----+----+----+----+----+
 * | 0  | 1  | 2  | 3  |... | 30 |
 * +----+----+----+----+----+----+
 * ^
 * --------------------------A B
 * <p>
 * 在第3秒，指针移动到第2格,如果收到A的事件,就需要将A从第30格中移动到第1格(指针的上一格)
 * +----+----+----+----+----+----+
 * | 0  | 1  | 2  | 3  |... | 30 |
 * +----+----+----+----+----+----+
 * ------------^
 * -------A-------------------B
 * <p>
 * 在30秒之后,指针会移动到第30格,如果B依然在第30格中,说明30秒之内没有收到B的事件,B已经掉线,移除A（第30格里的所有设备都需要移除）.
 * +----+----+----+----+----+----+
 * | 0  | 1  | 2  | 3  |... | 30 |
 * +----+----+----+----+----+----+
 * －－－－－－－－－－－－－－－－^
 * -------A-------------------
 * <p>
 * Created by edgar on 17-3-16.
 */
public interface KeepaliveChecker {

  /**
   * 记录一次心跳
   *
   * @param id 设备id
   */
  void heartbeat(String  id);

  /**
   * 删除设备
   * @param id
   */
  void remove(String id);

  /**
   * 在线的数量
   *
   * @return
   */
  int size();

  boolean close();

  /**
   * 创建一个KeepaliveChecker
   *
   * @param vertx
   * @param options
   * @return KeepaliveChecker
   */
  static KeepaliveChecker create(Vertx vertx, KeepaliveOptions options) {
    return new KeepaliveCheckerImpl(vertx, options);
  }
}
