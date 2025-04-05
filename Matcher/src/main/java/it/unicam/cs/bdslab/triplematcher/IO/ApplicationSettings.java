package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

import java.util.List;

public class ApplicationSettings {
    private final WeakBond bondPattern;
    private final Character seqPattern;
    private final int tolerance;
    private final int minPatternLength;
    private final int maxPatternLength = 11;
    private final int bondTollerance;
    private final int notPairedTollerance;
    private final int notConsecutiveTollerance;
    public ApplicationSettings(WeakBond bondPattern, Character seqPattern, int tolerance, int minPatternLength, int bondTollerance, int notPairedTollerance, int notConsecutiveTollerance) {
        this.bondPattern = bondPattern;
        this.seqPattern = seqPattern;
        this.tolerance = tolerance;
        this.minPatternLength = minPatternLength;
        this.bondTollerance = bondTollerance;
        this.notPairedTollerance = notPairedTollerance;
        this.notConsecutiveTollerance = notConsecutiveTollerance;
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

    public int getBondTollerance() {
        return bondTollerance;
    }

    public int getNotPairedTollerance() {
        return notPairedTollerance;
    }

    public int getNotConsecutiveTollerance() {
        return notConsecutiveTollerance;
    }

    public List<WeakBond> getBondPatternList() {
        return Utils.replicate(bondPattern, maxPatternLength);
    }

    public List<Character> getSeqPatternList() {
        return Utils.replicate(seqPattern, maxPatternLength);
    }

    public String toString() {
        return "ApplicationSettings{" + "\n" +
                "bondPattern=" + bondPattern + "\n" +
                ", seqPattern=" + seqPattern + "\n" +
                ", tolerance=" + tolerance + "\n" +
                ", minPatternLength=" + minPatternLength + "\n" +
                ", maxPatternLength=" + maxPatternLength + "\n" +
                ", bondTollerance=" + bondTollerance + "\n" +
                ", notPairedTollerance=" + notPairedTollerance + "\n" +
                ", notConsecutiveTollerance=" + notConsecutiveTollerance + "\n" +
                '}' + "\n";
    }

}
