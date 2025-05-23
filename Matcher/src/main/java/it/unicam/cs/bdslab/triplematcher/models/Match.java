package it.unicam.cs.bdslab.triplematcher.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, Integer> filterToleranceMap = new HashMap<>();
    /**
     * Constructor for the Match class.
     * @param row the row index of the match in the matrix
     * @param col the column index of the match in the matrix
     * @param distance the distance of the match the value of the <code>matrix[row][col]</code>
     * @param sequence the sequence of the match
     * @param pattern the pattern of the match
     * @param editOperations the edit operations of the match
     */
    public Match(int row, int col, int distance, List<T> sequence, List<T> pattern, List<EditOperation<T>> editOperations) {
        this.row = row;
        this.col = col;
        this.distance = distance;
        this.sequence = sequence;
        this.pattern = pattern;
        this.editOperations = editOperations;
    }

    /**
     * @return the row index of the match in the matrix
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column index of the match in the matrix
     */
    public int getCol() {
        return col;
    }

     /**
      * @return the distance of the match the value of the <code>matrix[row][col]</code>
     */
    public int getDistance() {
        return distance;
    }

    /**
     * @return the sequence of the match
     */
    public List<T> getSequence() {
        return sequence;
    }

    /**
     * @return the pattern of the match
     */
    public List<T> getPattern() {
        return pattern;
    }

    /**
     * @return the edit operations of the match
     */
    public List<EditOperation<T>> getEditOperations() {
        return editOperations;
    }

    /**
     * @return the edit operations of the match
     */
    public int getLength() {
        return editOperations.size();
    }

    /**
     * @return the edit operations of the match
     */
    public String getAlignmentString() {
        StringBuilder alignmentString = new StringBuilder();
        for (EditOperation<T> operation : editOperations) {
            alignmentString.append(operation.getStringForExport());
            alignmentString.append(";");
        }
        return alignmentString.toString();
    }

    /**
     * @return the edit operations of the match
     */
    public int getAlignmentLength() {
        return editOperations.size();
    }


    /**
     * Sets the filter tolerance for a specific filter.
     */
    public void setFilterTolerance(String filterName, int filterTollerance) {
        this.filterToleranceMap.put(filterName, filterTollerance);
    }

    /**
     * Gets the filter tolerance for a specific filter.
     * @param filterName the name of the filter
     * @return the filter tolerance or -1 if not set
     */
    public int getFilterTolerance(String filterName) {
        return this.filterToleranceMap.getOrDefault(filterName, -1);
    }

}
