package it.unicam.cs.bdslab.triplematcher.models.algorithms;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.BasicFilter;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.RNAApproximatePatternMatcher;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;
import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RNAApproximateMatcher extends RNABaseTripleMatcher {
    private final int maxPatternLength;

    public RNAApproximateMatcher(int tolerance, int minPatternLength, int maxPatternLength) {
        super(tolerance, minPatternLength);
        this.maxPatternLength = maxPatternLength;
    }


    @Override
    public List<Pair<Match<WeakBond>, Match<Character>>> match(RNASecondaryStructure structure, WeakBond bondPattern, Character seqPattern) {
        return super.combiner.combine(
                getMatches(super.getBondText(structure), Utils.replicate(bondPattern, this.maxPatternLength)),
                getMatches(super.getSeqText(structure), Utils.replicate(seqPattern, this.maxPatternLength))
        ).stream()
                .filter(pair -> super.getBaseFilter().test(structure, pair))
                .collect(Collectors.toList());
    }

    private<T> List<Match<T>> getMatches(List<T> text, List<T> pattern) {
        BasicFilter<T> filter = new BasicFilter<>(text, pattern);
        RNAApproximatePatternMatcher<T> matcher = new RNAApproximatePatternMatcher<>(text);
        return filter.filter(matcher, super.tolerance, super.minPatternLength);
    }
}
