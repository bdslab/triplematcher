package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class MatchCombiner<T, K> {


    public MatchCombiner() {

    }

    public List<Pair<Match<T>, Match<K>>> combine(List<Match<T>> matches1, List<Match<K>> matches2) {
        List<Pair<Match<T>, Match<K>>> result = new ArrayList<>();
        for (Match<T> match1 : matches1) {
            for (Match<K> match2 : matches2) {
                    // if the two matches have the same length and alignment length then they are combined
                    result.add(new Pair<>(match1, match2));

            }
        }
        return result;
    }
}
