package it.unicam.cs.bdslab.triplematcher.models;

import java.util.List;

/**
 * this class filter the matches of a RNAPatternMatcher, giving a list of matches
 * @param <T>
 */
public interface MatchFilter<T> {

    List<Match<T>> filter(RNAPatternMatcher<T> patternMatcher, int tolerance, int minPatternLength);
}
