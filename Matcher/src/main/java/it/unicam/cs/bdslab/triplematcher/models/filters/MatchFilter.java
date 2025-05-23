package it.unicam.cs.bdslab.triplematcher.models.filters;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * this class is a filter for the matches, who implements the test methos of the BiPredicate interface.
 *
 */
@FunctionalInterface
public interface MatchFilter extends BiPredicate<RNASecondaryStructure, Pair<Match<CompleteWeakBond>, Match<Character>>> {
    /**
     * this method is used to test if the match is valid or not.
     * @param structure the RNA secondary structure
     * @param matchMatchPair the pair of matches
     * @return true if the match is valid, false otherwise
     */
    @Override
    boolean test(RNASecondaryStructure structure, Pair<Match<CompleteWeakBond>, Match<Character>> matchMatchPair);

    /**
     * this method is used to combine two filters with the and operator.
     * @param otherFilter the other filter to combine with
     * @return a new filter that is the combination of the two filters
     */
    default MatchFilter and(MatchFilter otherFilter) {
        Objects.requireNonNull(otherFilter);
        return (structure, matchMatchPair) -> test(structure, matchMatchPair) && otherFilter.test(structure, matchMatchPair);
    }

    /**
     * this method is used to combine two filters with the or operator.
     * @param otherFilter the other filter to combine with
     * @return a new filter that is the combination of the two filters
     */
    default MatchFilter or(MatchFilter otherFilter) {
        Objects.requireNonNull(otherFilter);
        return (structure, matchMatchPair) -> test(structure, matchMatchPair) || otherFilter.test(structure, matchMatchPair);
    }

    /**
     * this method is used to negate the filter.
     * @return a new filter that is the negation of the original filter
     */
    default MatchFilter negate() {
        return (structure, matchMatchPair) -> !test(structure, matchMatchPair);
    }

}
