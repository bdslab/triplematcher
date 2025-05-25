package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

import java.util.List;

public class ApplicationSettings {
    private final CompleteWeakBond bondPattern;
    private final Character seqPattern;
    private final int sequenceTolerance;
    private final int minPatternLength;
    private final int maxPatternLength = 11;
    private final int bondTolerance;
    private final int notPairedTolerance;
    private final int notConsecutiveTolerance;
    private final int pseudoknotsTolerance;
    public ApplicationSettings(CompleteWeakBond bondPattern,
                               Character seqPattern,
                               int sequenceTolerance,
                               int minPatternLength,
                               int bondTolerance,
                               int notPairedTolerance,
                               int notConsecutiveTolerance,
                               int pseudoknotsTolerance) {
        this.bondPattern = bondPattern;
        this.seqPattern = seqPattern;
        this.sequenceTolerance = sequenceTolerance;
        this.minPatternLength = minPatternLength;
        this.bondTolerance = bondTolerance;
        this.notPairedTolerance = notPairedTolerance;
        this.notConsecutiveTolerance = notConsecutiveTolerance;
        this.pseudoknotsTolerance = pseudoknotsTolerance;
    }

    public CompleteWeakBond getBondPattern() {
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

    public int getPseudoknotsTolerance() {
        return pseudoknotsTolerance;
    }
    public List<WeakBond> getBondPatternList() {
        return Utils.replicate(bondPattern, maxPatternLength);
    }

    public List<Character> getSeqPatternList() {
        return Utils.replicate(seqPattern, maxPatternLength);
    }

    public String toString() {
        return "ApplicationSettings{" + "\n" +
                "\tbondPattern=" + bondPattern + "\n" +
                "\t, seqPattern=" + seqPattern + "\n" +
                "\t, minPatternLength=" + minPatternLength + "\n" +
                "\t, maxPatternLength=" + maxPatternLength + "\n" +
                "\t, seqTolerance=" + sequenceTolerance + "\n" +
                "\t, bondTolerance=" + bondTolerance + "\n" +
                "\t, notPairedTolerance=" + notPairedTolerance + "\n" +
                "\t, notConsecutiveTolerance=" + notConsecutiveTolerance + "\n" +
                "\t, pseudoknotsTolerance=" + (pseudoknotsTolerance == -1 ? "not used" : pseudoknotsTolerance) + "\n" +
                '}' + "\n";
    }
}
