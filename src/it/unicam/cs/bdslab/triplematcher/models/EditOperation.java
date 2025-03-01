package it.unicam.cs.bdslab.triplematcher.models;

import java.util.Objects;

public class EditOperation<T> {
    private T first;
    private T second;
    public EditOperation(T first, T second) {
        if (first == null && second == null)
            throw new IllegalArgumentException("Both elements cannot be null");
        this.first = first;
        this.second = second;
    }

    public String first() {
        if (first == null) {
            return "-";
        }
        return first.toString();
    }
    public String second() {
        if (second == null) {
            return "-";
        }
        return second.toString();
    }

    @Override
    public String toString() {
        return first + "->" + second;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EditOperation<?> that = (EditOperation<?>) o;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public boolean isInsertion() {
        return first == null;
    }
    public boolean isDeletion() {
        return second == null;
    }
    public boolean isMatchOrMismatch() {
        return first != null && second != null;
    }
    public boolean isMatch() {
        return first != null && first.equals(second);
    }

}
