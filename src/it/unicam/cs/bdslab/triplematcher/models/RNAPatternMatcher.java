package it.unicam.cs.bdslab.triplematcher.models;
import it.unicam.cs.bdslab.triplematcher.WeakBond;

import java.util.Comparator;
import java.util.List;

public interface RNAPatternMatcher<T extends Comparable<T>> {
    /**
     * find the pattern in the secondary structure
     * @param pattern the pattern to match
     * @return return true if the algorithm succeed
     * @throws NullPointerException if the pattern in null
     *
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
}
