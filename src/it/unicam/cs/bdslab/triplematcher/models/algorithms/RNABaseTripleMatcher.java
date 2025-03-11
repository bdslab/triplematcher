package it.unicam.cs.bdslab.triplematcher.models.algorithms;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.MatchCombiner;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterBuilder;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterNotConsecutiveBond;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterUnpairedNucletides;
import it.unicam.cs.bdslab.triplematcher.models.filters.MatchFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class RNABaseTripleMatcher implements RNATripleMatcher {
    protected final int tolerance;
    protected final int minPatternLength;
    protected final MatchCombiner<WeakBond, Character> combiner = new MatchCombiner<>();
    protected RNABaseTripleMatcher(int tolerance, int minPatternLength) {
        this.tolerance = tolerance;
        this.minPatternLength = minPatternLength;
    }


    protected MatchFilter getBaseFilter() {
        return new FilterBuilder()
                .addFilter(new FilterNotConsecutiveBond(this.tolerance))
                .addFilter(new FilterUnpairedNucletides(this.tolerance))
                .build();
    }

    protected List<Character> getSeqText(RNASecondaryStructure rna) {
        List<Character> seqText = new ArrayList<>(rna.getSequence().length());
        for (char c : rna.getSequence().toCharArray()) {
            seqText.add(c);
        }
        return seqText;
    }

    protected List<WeakBond> getBondText(RNASecondaryStructure rna) {
        List<WeakBond> bondText = rna.getBonds();
        bondText.sort(WeakBond::compareTo);
        return bondText;
    }
}
