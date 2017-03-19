package com.edgar.util.vertx.wheel;

/**
 * Created by edgar on 17-3-19.
 */
public class Task {

  private int id;

  private int cycleNum;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getCycleNum() {
    return cycleNum;
  }

  public void setCycleNum(int cycleNum) {
    this.cycleNum = cycleNum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Task task = (Task) o;

    if (id != task.id) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
