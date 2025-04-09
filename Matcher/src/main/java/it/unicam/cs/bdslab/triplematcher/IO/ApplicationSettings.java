package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

import java.util.List;

public class ApplicationSettings {
    private final WeakBond bondPattern;
    private final Character seqPattern;
    private final int sequenceTolerance;
    private final int minPatternLength;
    private final int maxPatternLength = 11;
    private final int bondTolerance;
    private final int notPairedTolerance;
    private final int notConsecutiveTolerance;
    public ApplicationSettings(WeakBond bondPattern, Character seqPattern, int sequenceTolerance, int minPatternLength, int bondTolerance, int notPairedTolerance, int notConsecutiveTolerance) {
        this.bondPattern = bondPattern;
        this.seqPattern = seqPattern;
        this.sequenceTolerance = sequenceTolerance;
        this.minPatternLength = minPatternLength;
        this.bondTolerance = bondTolerance;
        this.notPairedTolerance = notPairedTolerance;
        this.notConsecutiveTolerance = notConsecutiveTolerance;
    }

    public WeakBond getBondPattern() {
        return bondPattern;
    }

    public Character getSeqPattern() {
        return seqPattern;
    }

    public int getSequenceTolerance() {
        return sequenceTolerance;
    }

    public int getMaxPatternLength() {
        return maxPatternLength;
    }

    public int getMinPatternLength() {
        return minPatternLength;
    }

    public int getBondTolerance() {
        return bondTolerance;
    }

    public int getNotPairedTolerance() {
        return notPairedTolerance;
    }

    public int getNotConsecutiveTolerance() {
        return notConsecutiveTolerance;
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
                ", seqTolerance=" + sequenceTolerance + "\n" +
                ", minPatternLength=" + minPatternLength + "\n" +
                ", maxPatternLength=" + maxPatternLength + "\n" +
                ", bondTolerance=" + bondTolerance + "\n" +
                ", notPairedTolerance=" + notPairedTolerance + "\n" +
                ", notConsecutiveTolerance=" + notConsecutiveTolerance + "\n" +
                '}' + "\n";
    }

}
