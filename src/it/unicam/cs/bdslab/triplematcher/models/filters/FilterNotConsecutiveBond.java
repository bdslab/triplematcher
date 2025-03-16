package it.unicam.cs.bdslab.triplematcher.models.filters;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

public class FilterNotConsecutiveBond implements MatchFilter {
    private final int tolerance;

    public FilterNotConsecutiveBond(int tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public boolean test(RNASecondaryStructure structure, Pair<Match<WeakBond>, Match<Character>> matchMatchPair) {
        Match<WeakBond> bond = matchMatchPair.getFirst();
        int notConsecutive = 0;
        boolean consecutive = true;
        for (int i = 1; i < bond.getEditOperations().size(); i++) {
            WeakBond previous = bond.getEditOperations().get(i - 1).getSecond();
            WeakBond current = bond.getEditOperations().get(i).getSecond();
            if (previous.getLeft() != current.getLeft() - 1) {
                notConsecutive++;
                if (notConsecutive > this.tolerance) {
                    consecutive = false;
                    break;
                }
            }
        }
        bond.setFilterTollerance(notConsecutive);
        return consecutive;
    }
}
