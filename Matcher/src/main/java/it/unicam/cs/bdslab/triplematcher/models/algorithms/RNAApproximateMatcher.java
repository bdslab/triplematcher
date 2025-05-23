package it.unicam.cs.bdslab.triplematcher.models.algorithms;

import it.unicam.cs.bdslab.triplematcher.IO.ApplicationSettings;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.BasicFilter;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.RNAApproximatePatternMatcher;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;
import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class RNAApproximateMatcher extends RNABaseTripleMatcher {
    private final int maxPatternLength;

    public RNAApproximateMatcher(int tolerance, int minPatternLength, int bondTolerance, int notPairedTolerance, int notConsecutiveTolerance, int pseudoknotTolerance, int maxPatternLength) {
        super(tolerance, minPatternLength, bondTolerance, notPairedTolerance, notConsecutiveTolerance, pseudoknotTolerance);
        this.maxPatternLength = maxPatternLength;
    }

    public RNAApproximateMatcher(ApplicationSettings settings) {
        super(settings);
        this.maxPatternLength = settings.getMaxPatternLength();
    }


    @Override
    public List<Pair<Match<CompleteWeakBond>, Match<Character>>> match(RNASecondaryStructure structure, CompleteWeakBond bondPattern, Character seqPattern) {
        return super.combiner.combine(
                getMatches(super.getBondText(structure), Utils.replicate(bondPattern, this.maxPatternLength), this.bondTolerance),
                getMatches(super.getSeqText(structure), Utils.replicate(seqPattern, this.maxPatternLength), this.tolerance)
        ).stream().parallel()
                .filter(pair -> super.getBaseFilter().test(structure, pair))
                .collect(Collectors.toList());
    }

    private<T> List<Match<T>> getMatches(List<T> text, List<T> pattern, int tolerance) {
        BasicFilter<T> filter = new BasicFilter<>(text, pattern);
        RNAApproximatePatternMatcher<T> matcher = new RNAApproximatePatternMatcher<>(text);
        return filter.filter(matcher, tolerance, super.minPatternLength);
    }
}
