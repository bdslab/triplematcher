package it.unicam.cs.bdslab.triplematcher.filter.distance.utils;

public class Triple<T> {
    private final T first;
    private final T second;
    private final T third;

    public Triple(T first, T second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
