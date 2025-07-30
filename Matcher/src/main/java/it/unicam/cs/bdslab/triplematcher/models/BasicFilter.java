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
public class BasicFilter<T> implements PatternMatcherFilter<T> {
    private final List<T> text;
    private final List<T> pattern;
    private final boolean findAllMatches;
    /**
     * Constructor for the BasicFilter class.
     * @param text the sequence to be matched
     * @param pattern the pattern to be matched
     * @param findAllMatches a boolean indicating whether to find all matches or not
     */
    public BasicFilter(List<T> text, List<T> pattern, boolean findAllMatches) {
        this.text = Collections.unmodifiableList(text);
        this.pattern = Collections.unmodifiableList(pattern);
        this.findAllMatches = findAllMatches;
    }
    /**
     * Constructor for the BasicFilter class.
     * @param text the sequence to be matched
     * @param pattern the pattern to be matched
     */
    public BasicFilter(List<T> text, List<T> pattern) {
        this.text = Collections.unmodifiableList(text);
        this.pattern = Collections.unmodifiableList(pattern);
        this.findAllMatches = false; // Default to not finding all matches
    }
    @Override
    public List<Match<T>> filter(RNAPatternMatcher<T> patternMatcher, int tolerance, int minPatternLength) {
        patternMatcher.solve(pattern);
        return getMatches(patternMatcher, tolerance, minPatternLength);
    }

    /**
     * This method retrieves matches from the alignment matrix of the given RNAPatternMatcher.
     * @param patternMatcher the RNAPatternMatcher to use for matching
     * @param tolerance the tolerance to use for the filter
     * @param minPatternLength the minimum length of the pattern to use for the filter
     * @return a list of matches
     */
    private List<Match<T>> getMatches(RNAPatternMatcher<T> patternMatcher, int tolerance, int minPatternLength) {
        int[][] matrix = patternMatcher.getAlignmentMatrix();
        // linked list is better for concatenation and append
        List<Match<T>> matches = new LinkedList<>();
        // If the row has 0 we don't need to consider others
        boolean rowWith0 = false;
        for (int i = matrix.length - 1; i >= minPatternLength && (!rowWith0 || findAllMatches); i--) {
            int[] matrixRow = matrix[i];
            int minScoreValue = Utils.getMin(matrixRow);
            // if the min distance on the row is higher than the tolerance I consider the next row
            if (minScoreValue > tolerance)
                continue;
            if (minScoreValue == 0)
                rowWith0 = true;
            matches.addAll(getMatchesForARow(patternMatcher, i, tolerance));
        }

        return new ArrayList<>(matches);
    }

    /**
     * This method retrieves matches for a specific row in the alignment matrix of the given RNAPatternMatcher.
     * @param patternMatcher the RNAPatternMatcher to use for matching
     * @param i the row index in the alignment matrix
     * @param tolerance the tolerance to use for the filter
     * @return a list of matches for the specified row
     */
    private List<Match<T>> getMatchesForARow(RNAPatternMatcher<T> patternMatcher, int i, int tolerance) {
        int[] row = patternMatcher.getAlignmentMatrix()[i];
        List<Match<T>> matches = new LinkedList<>();
        for (int j = 0; j < row.length ; j++ ) {
            if (row[j] <= tolerance) {
                List<EditOperation<T>> traceback = patternMatcher.traceback(i, j);
                matches.add(new Match<>(i, j, row[j], text, pattern.subList(0, i), traceback));
            }
        }
        return matches;
    }
}
