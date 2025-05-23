package it.unicam.cs.bdslab.triplematcher.models.algorithms;

import it.unicam.cs.bdslab.triplematcher.IO.ApplicationSettings;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.MatchCombiner;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterBuilder;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterNotConsecutiveBond;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterUnpairedNucletides;
import it.unicam.cs.bdslab.triplematcher.models.filters.MatchFilter;

import java.util.ArrayList;
import java.util.List;

public abstract class RNABaseTripleMatcher implements RNATripleMatcher {
    protected final int tolerance;
    protected final int minPatternLength;
    protected final MatchCombiner<CompleteWeakBond, Character> combiner = new MatchCombiner<>();
    protected final int bondTolerance;
    protected final int notPairedTolerance;
    protected final int notConsecutiveTolerance;


    protected RNABaseTripleMatcher(int tolerance, int minPatternLength, int bondTolerance, int notPairedTolerance, int notConsecutiveTolerance) {
        this.tolerance = tolerance;
        this.minPatternLength = minPatternLength;
        this.bondTolerance = bondTolerance;
        this.notPairedTolerance = notPairedTolerance;
        this.notConsecutiveTolerance = notConsecutiveTolerance;
    }

    protected RNABaseTripleMatcher(ApplicationSettings settings) {
        this(settings.getSequenceTolerance(), settings.getMinPatternLength(), settings.getBondTolerance(), settings.getNotPairedTolerance(), settings.getNotConsecutiveTolerance());
    }

    /**
     * Returns the base filter for the matcher.
     * @return the base filter
     */
    protected MatchFilter getBaseFilter() {
        return new FilterBuilder()
                .addFilter(new FilterNotConsecutiveBond(this.notConsecutiveTolerance))
                .addFilter(new FilterUnpairedNucletides(this.notPairedTolerance))
                .build();
    }

    /**
     * Returns the sequence text of the given RNA secondary structure.
     * @param rna the RNA secondary structure
     * @return the sequence text of the RNA secondary structure
     */
    protected List<Character> getSeqText(RNASecondaryStructure rna) {
        List<Character> seqText = new ArrayList<>(rna.getSequence().length());
        for (char c : rna.getSequence().toCharArray()) {
            seqText.add(c);
        }
        return seqText;
    }

    /**
     * Returns the bond text of the given RNA secondary structure.
     *
     * @param rna the RNA secondary structure
     * @return the bond text of the RNA secondary structure
     */
    protected List<CompleteWeakBond> getBondText(RNASecondaryStructure rna) {
        List<CompleteWeakBond> bondText = rna.getBonds();
        bondText.sort(WeakBond::compareTo);
        return bondText;
    }
}
