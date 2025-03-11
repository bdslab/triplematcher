package it.unicam.cs.bdslab.triplematcher.models.algorithms;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.BasicFilter;
import it.unicam.cs.bdslab.triplematcher.models.LinearMatcher;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.RNAApproximatePatternMatcher;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RNALinearTripleMatcher extends RNABaseTripleMatcher {
    private final LinearMatcher<WeakBond> bondMatcher;
    private final LinearMatcher<Character> seqMatcher;
    public RNALinearTripleMatcher(int tolerance, int minPatternLength) {
        super(tolerance, minPatternLength);
        seqMatcher = new LinearMatcher<>(tolerance, minPatternLength);
        bondMatcher = new LinearMatcher<>(tolerance, minPatternLength);
    }

    @Override
    public List<Pair<Match<WeakBond>, Match<Character>>> match(RNASecondaryStructure structure, WeakBond bondPattern, Character seqPattern) {
        return super.combiner.combine(
            this.bondMatcher.solve(structure, super.getBondText(structure), bondPattern),
            this.seqMatcher.solve(structure, super.getSeqText(structure), seqPattern)
        ).stream()
                .filter(pair -> super.getBaseFilter().test(structure, pair))
                .collect(Collectors.toList());
    }
}
