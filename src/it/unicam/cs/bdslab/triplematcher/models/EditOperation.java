package it.unicam.cs.bdslab.triplematcher.models;

import java.util.Objects;

public class EditOperation<T> {
    private final T first;
    private final T second;
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

    public String getStringForExport() {
        return Objects.equals(first, second) ? "M" : "-";
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
}
