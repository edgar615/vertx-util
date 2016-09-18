package com.edgar.util.vertx.function;

/**
 * 三元组.
 */
public class Tuple3<T1, T2, T3> {

    private final T1 t1;

    private final T2 t2;

    private final T3 t3;

    private Tuple3(T1 t1, T2 t2, T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> create(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1, t2, t3);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Tuple3 tuple2 = (Tuple3) o;

        if (t1 != null ? !t1.equals(tuple2.t1) : tuple2.t1 != null) {
            return false;
        }

        if (t2 != null ? !t2.equals(tuple2.t2) : tuple2.t2 != null) {
            return false;
        }

        if (t3 != null ? !t3.equals(tuple2.t3) : tuple2.t3 != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = t1 != null ? t1.hashCode() : 0;

        result = 31 * result + (t2 != null ? t2.hashCode() : 0);

        result = 31 * result + (t3 != null ? t3.hashCode() : 0);

        return result;
    }


    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", getT1(), getT2(), getT3());
    }

}