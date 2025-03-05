package it.unicam.cs.bdslab.triplematcher.models;
import javafx.beans.value.ObservableBooleanValue;

import java.util.List;

public interface RNAPatternMatcher<T> {
    /**
     * find the pattern in the secondary structure
     *
     * @param pattern the pattern to match
     * @return return true if the algorithm succeed
     * @throws NullPointerException if the pattern in null
     */
    boolean solve(List<T> pattern);

    /**
     * Return the possible best alignment for the pattern
     * @return the edit list of edit operations representing the alignment
     */
    List<List<EditOperation<T>>> getOptimalEditOperations();

    /**
     * Computes and returns the alignment matrix, which is a two-dimensional array
     * representing the alignment scores or relationships between elements in a
     * sequence or structure comparison.
     *
     * @return a two-dimensional integer array representing the alignment matrix
     */
    int[][] getAlignmentMatrix();

    /**
     * return the score (distance) between the pattern and the string.
     * The distance is at least 0 and at most the length of the pattern
     * @return an integer value representing the distance between the pattern and the sequence
     */
    int getScore();

    /**
     * do the traceback from a given position
     * @param i row
     * @param j column
     * @return the alignment
     */
    List<EditOperation<T>> traceback(int i, int j);
}
