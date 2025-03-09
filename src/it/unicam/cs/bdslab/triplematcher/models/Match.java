package it.unicam.cs.bdslab.triplematcher.models;

import java.util.List;

/**
 * this class represent a match
 * @param <T>
 */
public class Match<T> {
    private final int row;
    private final int col;
    private final int distance;
    private final List<T> sequence;
    private final List<T> pattern;
    private final List<EditOperation<T>> editOperations;

    public Match(int row, int col, int distance, List<T> sequence, List<T> pattern, List<EditOperation<T>> editOperations) {
        this.row = row;
        this.col = col;
        this.distance = distance;
        this.sequence = sequence;
        this.pattern = pattern;
        this.editOperations = editOperations;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public int getDistance() {
        return distance;
    }
    public List<T> getSequence() {
        return sequence;
    }
    public List<T> getPattern() {
        return pattern;
    }
    public List<EditOperation<T>> getEditOperations() {
        return editOperations;
    }
    public int getLength() {
        return pattern.size();
    }

    public String getAlignmentString() {
        StringBuilder alignmentString = new StringBuilder();
        for (EditOperation<T> operation : editOperations) {
            alignmentString.append(operation.getStringForExport());
            alignmentString.append(";");
        }
        return alignmentString.toString();
    }
    public int getAlignmentLength() {
        return editOperations.size();
    }
}
