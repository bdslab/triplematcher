package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Combines two lists of matches into a list of pairs.
 * @param <T> the type of the first match
 * @param <K> the type of the second match
 */
public class MatchCombiner<T, K> {

    /**
     * Default constructor.
     */
    public MatchCombiner() {

    }

    /**
     * Combines two lists of matches into a list of pairs.
     * @param matches1 the first list of matches
     * @param matches2 the second list of matches
     * @return a list of pairs of matches, where the first element of the pair is a match from the first list and the second element is a match from the second list
     */
    public List<Pair<Match<T>, Match<K>>> combine(List<Match<T>> matches1, List<Match<K>> matches2) {
        List<Pair<Match<T>, Match<K>>> result = new ArrayList<>();
        for (Match<T> match1 : matches1) {
            for (Match<K> match2 : matches2) {
                    result.add(new Pair<>(match1, match2));
            }
        }
        return result;
    }
}
