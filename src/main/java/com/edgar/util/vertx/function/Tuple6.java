package com.edgar.util.vertx.function;

/**
 * 六元组.
 */
public class Tuple6<T1, T2, T3, T4, T5, T6> {

  private final T1 t1;

  private final T2 t2;

  private final T3 t3;

  private final T4 t4;

  private final T5 t5;

  private final T6 t6;

  private Tuple6(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
    this.t1 = t1;
    this.t2 = t2;
    this.t3 = t3;
    this.t4 = t4;
    this.t5 = t5;
    this.t6 = t6;
  }

  public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> create(T1 t1, T2 t2, T3 t3,
                                                                               T4 t4, T5 t5,
                                                                               T6 t6) {
    return new Tuple6<>(t1, t2, t3, t4, t5, t6);
  }

  public T1 getT1() {
    return t1;
  }

  public T2 getT2() {
    return t2;
  }

  public T3 getT3() {
    return t3;
  }

  public T4 getT4() {
    return t4;
  }

  public T5 getT5() {
    return t5;
  }

  public T6 getT6() {
    return t6;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Tuple6 tuple2 = (Tuple6) o;

    if (t1 != null ? !t1.equals(tuple2.t1) : tuple2.t1 != null) {
      return false;
    }

    if (t2 != null ? !t2.equals(tuple2.t2) : tuple2.t2 != null) {
      return false;
    }

    if (t3 != null ? !t3.equals(tuple2.t3) : tuple2.t3 != null) {
      return false;
    }

    if (t4 != null ? !t4.equals(tuple2.t4) : tuple2.t4 != null) {
      return false;
    }

    if (t5 != null ? !t5.equals(tuple2.t5) : tuple2.t5 != null) {
      return false;
    }

    if (t6 != null ? !t6.equals(tuple2.t6) : tuple2.t6 != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = t1 != null ? t1.hashCode() : 0;

    result = 31 * result + (t2 != null ? t2.hashCode() : 0);

    result = 31 * result + (t3 != null ? t3.hashCode() : 0);

    result = 31 * result + (t4 != null ? t4.hashCode() : 0);

    result = 31 * result + (t5 != null ? t5.hashCode() : 0);

    result = 31 * result + (t6 != null ? t6.hashCode() : 0);
    return result;
  }


  @Override
  public String toString() {
    return String.format("(%s, %s, %s, %s, %s, %s)", getT1(), getT2(), getT3(), getT4(), getT5(),
                         getT6());
  }

}