package it.unicam.cs.bdslab.triplematcher.models.filters;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * this class is a filter for the matches, who implements the test methos of the BiPredicate interface.
 *
 */
@FunctionalInterface
public interface MatchFilter extends BiPredicate<RNASecondaryStructure, Pair<Match<WeakBond>, Match<Character>>> {
    default MatchFilter and(MatchFilter otherFilter) {
        Objects.requireNonNull(otherFilter);
        return (structure, matchMatchPair) -> test(structure, matchMatchPair) && otherFilter.test(structure, matchMatchPair);
    }

    default MatchFilter or(MatchFilter otherFilter) {
        Objects.requireNonNull(otherFilter);
        return (structure, matchMatchPair) -> test(structure, matchMatchPair) || otherFilter.test(structure, matchMatchPair);
    }

    default MatchFilter negate() {
        return (structure, matchMatchPair) -> !test(structure, matchMatchPair);
    }

}
