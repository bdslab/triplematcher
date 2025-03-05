package it.unicam.cs.bdslab.triplematcher.models;

import java.util.List;

public interface MatchFilter<T> {

    List<Match<T>> filter(RNAPatternMatcher<T> patternMatcher, int tolerance, int minPatternLength);
}
