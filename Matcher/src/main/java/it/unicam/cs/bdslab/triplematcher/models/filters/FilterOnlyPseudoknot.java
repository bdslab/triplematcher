package it.unicam.cs.bdslab.triplematcher.models.filters;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.models.*;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

/**
 * Filter that only allows matches that are pseudoknots.
 */
public class FilterOnlyPseudoknot implements MatchFilter {
    private final int tolerance;

    public FilterOnlyPseudoknot(int tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public boolean test(RNASecondaryStructure structure, Pair<Match<CompleteWeakBond>, Match<Character>> matchMatchPair) {
        Match<CompleteWeakBond> completeWeakBondMatch = matchMatchPair.getFirst();
        boolean isValid = true;
        int tolerance = 0;
        for (EditOperation<CompleteWeakBond> editOperation : completeWeakBondMatch.getEditOperations()) {
            CompleteWeakBond completeWeakBond = editOperation.getSecond();
            if (!completeWeakBond.isCross())
                if (++tolerance > this.tolerance) {
                    isValid = false;
                    break; // Exit the loop if the tolerance is exceeded
                }
        }
        completeWeakBondMatch.setFilterTolerance(this.getClass().getName(), tolerance);
        return isValid;
    }
}
