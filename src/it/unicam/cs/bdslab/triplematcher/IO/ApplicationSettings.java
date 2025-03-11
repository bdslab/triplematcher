package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

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
        return Utils.replicate(bondPattern, maxPatternLength);
    }

    public List<Character> getSeqPatternList() {
        return Utils.replicate(seqPattern, maxPatternLength);
    }

}
