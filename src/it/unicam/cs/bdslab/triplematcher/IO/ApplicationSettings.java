package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.WeakBond;

import java.util.ArrayList;
import java.util.List;

public class ApplicationSettings {
    private final WeakBond bondPattern;
    private final Character seqPattern;
    private final int tolerance;
    private final int minPatternLength;
    private final int maxPatternLength = 11;
    public ApplicationSettings(WeakBond bondPattern, Character seqPattern, int tolerance, int minPatternLength) {
        this.bondPattern = bondPattern;
        this.seqPattern = seqPattern;
        this.tolerance = tolerance;
        this.minPatternLength = minPatternLength;
    }

    public WeakBond getBondPattern() {
        return bondPattern;
    }

    public Character getSeqPattern() {
        return seqPattern;
    }

    public int getTolerance() {
        return tolerance;
    }

    public int getMaxPatternLength() {
        return maxPatternLength;
    }

    public int getMinPatternLength() {
        return minPatternLength;
    }

    public List<WeakBond> getBondPatternList() {
        return replicate(bondPattern, maxPatternLength);
    }

    public List<Character> getSeqPatternList() {
        return replicate(seqPattern, maxPatternLength);
    }


    private<T> List<T> replicate(T element, int times) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            list.add(element);
        }
        return list;
    }
}
