package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

import java.util.*;

/**
 * The BasicFilter class is an implementation of the MatchFilter interface. It provides
 * functionality to filter matches for a given pattern in a sequence based on a specified
 * tolerance. The filter identifies matches using the alignment matrix retrieved from the
 * specified RNAPatternMatcher.
 *
 * @param <T> the type of elements in the sequence and pattern
 */
public class BasicFilter<T> implements MatchFilter<T> {
    private final List<T> text;
    private final List<T> pattern;
    public BasicFilter(List<T> text, List<T> pattern) {
        this.text = Collections.unmodifiableList(text);
        this.pattern = Collections.unmodifiableList(pattern);
    }

    @Override
    public List<Match<T>> filter(RNAPatternMatcher<T> patternMatcher, int tolerance, int minPatternLength) {
        patternMatcher.solve(pattern);
        return getMatches(patternMatcher, tolerance, minPatternLength);
    }

    private List<Match<T>> getMatches(RNAPatternMatcher<T> patternMatcher, int tolerance, int minPatternLength) {
        int[][] matrix = patternMatcher.getAlignmentMatrix();
        // linked list is better for concatenation and append
        List<Match<T>> matches = new LinkedList<>();
        // If the row has 0 we don't need to consider others
        boolean rowWith0 = false;
        for (int i = matrix.length - 1; i >= 6 && !rowWith0; i--) {
            int[] matrixRow = matrix[i];
            int minScoreValue = Utils.getMin(matrixRow);
            // if the min distance on the row is higher than the tolerance I consider the next row
            if (minScoreValue > tolerance)
                continue;
            if (minScoreValue == 0)
                rowWith0 = true;
            matches.addAll(getMatchesForARow(patternMatcher, i, minScoreValue));
        }
        return new ArrayList<>(matches);
    }

    private List<Match<T>> getMatchesForARow(RNAPatternMatcher<T> patternMatcher, int i, int minValue) {
        int[] row = patternMatcher.getAlignmentMatrix()[i];
        List<Match<T>> matches = new LinkedList<>();
        for (int j = 0; j < row.length ; j++ ) {
            if (row[j] <= minValue) {
                List<EditOperation<T>> traceback = patternMatcher.traceback(i, j);
                matches.add(new Match<>(i, j, row[j], text, pattern.subList(0, i), traceback));
            }
        }
        return matches;
    }
}
