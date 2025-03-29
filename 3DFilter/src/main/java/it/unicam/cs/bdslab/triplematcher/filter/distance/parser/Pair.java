package it.unicam.cs.bdslab.triplematcher.filter.distance.parser;

public class Pair<T> {
    
    private final T first;
    private final T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    
    
}
