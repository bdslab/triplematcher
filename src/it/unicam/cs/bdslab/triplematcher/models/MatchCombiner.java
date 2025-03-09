package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MatchCombiner<T, K> {

    Comparator<Match<?>> order = new Comparator<Match<?>>() {
        @Override
        public int compare(Match<?> o1, Match<?> o2) {
            // order by length, then by distance so the longest matches are first with the smallest distance
            if (o1.getLength() == o2.getLength()) {
                return o1.getDistance() - o2.getDistance();
            } else {
                return o1.getLength() - o2.getLength();
            }
        }
    };
    public MatchCombiner() {

    }

    public List<Pair<Match<T>, Match<K>>> combine(List<Match<T>> matches1, List<Match<K>> matches2) {
        List<Pair<Match<T>, Match<K>>> result = new ArrayList<>();
        for (Match<T> match1 : matches1) {
            for (Match<K> match2 : matches2) {
                if (match1.getLength() == match2.getLength() && match1.getDistance() == match2.getDistance()) {
                    // if the two matches have the same length and alignment length then they are combined
                    result.add(new Pair<>(match1, match2));
                }
            }
        }
        return result;
    }
}
