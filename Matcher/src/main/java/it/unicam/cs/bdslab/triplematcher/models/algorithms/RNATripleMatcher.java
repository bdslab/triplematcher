package it.unicam.cs.bdslab.triplematcher.models.algorithms;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.util.List;

/**
 * Interface for the RNA triple matcher.
 */
public interface RNATripleMatcher {
    /**
     * Matches a given RNA secondary structure with a given bond and sequence pattern.
     * @param structure the RNA secondary structure
     * @param bondPattern the bond pattern
     * @param seqPattern the sequence pattern
     * @return a list of pairs of matches, where the first element of the pair is a match of the bond pattern and the second element is a match of the sequence pattern
     */
    List<Pair<Match<CompleteWeakBond>, Match<Character>>> match(RNASecondaryStructure structure, CompleteWeakBond bondPattern, Character seqPattern);

}
