package it.unicam.cs.bdslab.triplematcher.models.filters;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

/**
 * This class implements a filter that checks if the bonds in a match are not consecutive.
 */
public class FilterNotConsecutiveBond implements MatchFilter {
    private final int tolerance;

    /**
     * Constructor for the FilterNotConsecutiveBond class.
     * @param tolerance the tolerance for the filter
     */
    public FilterNotConsecutiveBond(int tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public boolean test(RNASecondaryStructure structure, Pair<Match<CompleteWeakBond>, Match<Character>> matchMatchPair) {
        Match<CompleteWeakBond> bond = matchMatchPair.getFirst();
        int notConsecutive = 0;
        boolean consecutive = true;
        for (int i = 1; i < bond.getEditOperations().size(); i++) {
            WeakBond previous = bond.getEditOperations().get(i - 1).getSecond();
            WeakBond current = bond.getEditOperations().get(i).getSecond();
            if (previous == null || current == null || previous.getLeft() != current.getLeft() - 1) {
                notConsecutive += current.getLeft() - previous.getLeft() - 1;
                if (notConsecutive > this.tolerance) {
                    consecutive = false;
                    break;
                }
            }
        }
        bond.setFilterTolerance(this.getClass().getName(), notConsecutive);
        return consecutive;
    }
}
