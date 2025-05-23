package it.unicam.cs.bdslab.triplematcher.models;

import java.util.List;

/**
 * this class filter the matches of a RNAPatternMatcher, giving a list of matches
 * @param <T>
 */
public interface PatternMatcherFilter<T> {
    /**
     * Filter the matches of a RNAPatternMatcher, giving a list of matches
     * @param patternMatcher the RNAPatternMatcher to filter
     * @param tolerance the tolerance to use for the filter
     * @param minPatternLength the minimum length of the pattern to use for the filter
     * @return a list of matches
     */
    List<Match<T>> filter(RNAPatternMatcher<T> patternMatcher, int tolerance, int minPatternLength);
}
