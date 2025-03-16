package it.unicam.cs.bdslab.triplematcher.IO.models;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.EditOperation;
import it.unicam.cs.bdslab.triplematcher.models.Match;

import java.util.List;
import java.util.stream.Collectors;

public class CSVRow {
    public static final String[] HEADERSARRAY = {
            "FileName",
            "length",
            "solution_length",
            "start_window_seq",
            "stop_window_seq",
            "start_window_seq",
            "stop_window_bond",
            "isApprox_seq",
            "str_match_seq",
            "isApprox_bond",
            "str_match_bond",
            "real_num_seq",
            "real_num_bond",
            "match_str_seq",
            "match_str_bond",
            "tolerance_seq",
            "tolerance_bond",
            "tolerance_not_paired",
            "tolerance_not_consecutive"
    };
    public static final String HEADERS = String.join(",", HEADERSARRAY) + "\n";
    private final String RNAKey;
    private final int sequenceLength;
    private final int solutionLength;
    private final int seqWindowStart;
    private final int seqWindowEnd;
    private final WeakBond bondWindowStart;
    private final WeakBond bondWindowEnd;
    private final boolean isApproximateSeq;
    private final String strMatchSeq;
    private final boolean isApproximateBond;
    private final String strMatchBond;
    private final int scoreSeq;
    private final int scoreBond;
    private final String seqCustomMatchString;
    private final String bondCustomMatchString;
    private final int seqTolerance;
    private final int bondTolerance;
    private final int notPairedTolerance;
    private final int notConsecutiveTolerance;

    public CSVRow(RNASecondaryStructure rnaSecondaryStructure, Match<WeakBond> bondMatch, Match<Character> seqMatch) {
        this.RNAKey = rnaSecondaryStructure.getDescription();
        this.sequenceLength = rnaSecondaryStructure.getSequence().length();
        this.solutionLength = seqMatch.getLength();
        this.seqWindowStart = seqMatch.getCol() - seqMatch.getLength();
        this.seqWindowEnd = seqMatch.getCol();
        this.bondWindowStart = rnaSecondaryStructure.getBonds().get(bondMatch.getCol() - bondMatch.getLength());
        this.bondWindowEnd = rnaSecondaryStructure.getBonds().get(bondMatch.getCol() - 1);
        this.isApproximateSeq = seqMatch.getDistance() > 0;
        this.strMatchSeq = seqMatch.getAlignmentString();
        this.isApproximateBond = bondMatch.getDistance() > 0;
        this.strMatchBond = bondMatch.getAlignmentString();
        this.scoreSeq = seqMatch.getDistance();
        this.scoreBond = bondMatch.getDistance();
        this.seqCustomMatchString = getCustomMatchString(seqMatch.getEditOperations());
        this.bondCustomMatchString = getCustomMatchString(bondMatch.getEditOperations());
        this.seqTolerance = seqMatch.getDistance();
        this.bondTolerance = bondMatch.getDistance();
        this.notPairedTolerance = seqMatch.getFilterTollerance();
        this.notConsecutiveTolerance = bondMatch.getFilterTollerance();
    }

    public String getRow() {
        return RNAKey + "," + sequenceLength + "," + solutionLength + "," + seqWindowStart + "," + seqWindowEnd + "," + getBondString(bondWindowStart) + ","
                + getBondString(bondWindowEnd) + "," + isApproximateSeq + "," + strMatchSeq + "," + isApproximateBond + "," + strMatchBond
                + "," + scoreSeq + "," + scoreBond
                + "," + seqCustomMatchString + "," + bondCustomMatchString
                + "," + seqTolerance + "," + bondTolerance + "," + notPairedTolerance + "," + notConsecutiveTolerance
                + "\n";
    }

    private String getBondString(WeakBond bond) {
        return "(" + bond.getLeft() + ";" + bond.getRight() + ")";
    }

    private String getCustomMatchString(List<? extends EditOperation<?>> editOperations) {
        StringBuilder sb = new StringBuilder();
        for (EditOperation<?> editOperation : editOperations) {
            if (editOperation.isMatch())
                sb.append('M');
            else
                sb.append('-');
        }
        return sb.toString();
    }
}
