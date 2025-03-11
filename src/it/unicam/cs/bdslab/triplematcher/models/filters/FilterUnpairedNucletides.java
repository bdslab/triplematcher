package it.unicam.cs.bdslab.triplematcher.models.filters;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

public class FilterUnpairedNucletides implements MatchFilter {
    private final int tolerance;
    public FilterUnpairedNucletides(int tolerance) {
        this.tolerance = tolerance;
    }


    @Override
    public boolean test(RNASecondaryStructure structure, Pair<Match<WeakBond>, Match<Character>> matchMatchPair) {
        int numberOfBonds = 0;
        for (int i = matchMatchPair.getSecond().getCol() - 1; i >= matchMatchPair.getSecond().getCol() - matchMatchPair.getSecond().getLength(); i--) {
            if (!structure.isNotWeakBond(i)) {
                numberOfBonds++;
                if (numberOfBonds > this.tolerance) {
                    return false;
                }
            }
        }
        return true;
    }
}
