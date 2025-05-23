package it.unicam.cs.bdslab.triplematcher.IO.models;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.EditOperation;
import it.unicam.cs.bdslab.triplematcher.models.Match;

import java.util.List;

public class CSVRow {
    public static final String[] HEADERSARRAY = {
            "FileName",
            "length",
            "solution_length_seq",
            "solution_length_bond",
            "start_window_seq",
            "stop_window_seq",
            "start_window_bond",
            "stop_window_bond",
            "str_match_seq",
            "str_match_bond",
            "real_num_seq",
            "real_num_bond",
            "match_str_seq",
            "match_str_bond",
            "tolerance_seq",
            "tolerance_bond",
            "tolerance_not_paired",
            "tolerance_not_consecutive",
            "full_seq",
    };
    public static final String HEADERS = String.join(",", HEADERSARRAY) + "\n";
    private final String RNAKey;
    private final int sequenceLength;
    private final int seqSolutionLength;
    private final int bondSolutionLength;
    private final int seqWindowStart;
    private final int seqWindowEnd;
    private final WeakBond bondWindowStart;
    private final WeakBond bondWindowEnd;
    private final String strMatchSeq;
    private final String strMatchBond;
    private final int scoreSeq;
    private final int scoreBond;
    private final String seqCustomMatchString;
    private final String bondCustomMatchString;
    private final int seqTolerance;
    private final int bondTolerance;
    private final int notPairedTolerance;
    private final int notConsecutiveTolerance;
    private final String fullSeq;

    public CSVRow(RNASecondaryStructure rnaSecondaryStructure, Match<CompleteWeakBond> bondMatch, Match<Character> seqMatch) {
        this.RNAKey = rnaSecondaryStructure.getDescription();
        this.sequenceLength = rnaSecondaryStructure.getSequence().length();
        this.seqSolutionLength = seqMatch.getLength();
        this.bondSolutionLength = bondMatch.getLength();
        this.seqWindowStart = seqMatch.getCol() - seqMatch.getLength();
        this.seqWindowEnd = seqMatch.getCol();
        this.bondWindowStart = rnaSecondaryStructure.getBonds().get(bondMatch.getCol() - bondMatch.getLength());
        this.bondWindowEnd = rnaSecondaryStructure.getBonds().get(bondMatch.getCol() - 1);
        this.strMatchSeq = seqMatch.getAlignmentString();
        this.strMatchBond = bondMatch.getAlignmentString();
        this.scoreSeq = seqMatch.getDistance();
        this.scoreBond = bondMatch.getDistance();
        this.seqCustomMatchString = getCustomMatchString(seqMatch.getEditOperations());
        this.bondCustomMatchString = getCustomMatchString(bondMatch.getEditOperations());
        this.seqTolerance = seqMatch.getDistance();
        this.bondTolerance = bondMatch.getDistance();
        this.notPairedTolerance = seqMatch.getFilterTollerance();
        this.notConsecutiveTolerance = bondMatch.getFilterTollerance();
        this.fullSeq = rnaSecondaryStructure.getSequence();
    }

    public String getRow() {
        return RNAKey + "," + sequenceLength + "," + seqSolutionLength + "," + bondSolutionLength + "," + seqWindowStart + "," + seqWindowEnd + "," + getBondString(bondWindowStart) + ","
                + getBondString(bondWindowEnd) + "," + strMatchSeq + "," + strMatchBond
                + "," + scoreSeq + "," + scoreBond
                + "," + seqCustomMatchString + "," + bondCustomMatchString
                + "," + seqTolerance + "," + bondTolerance + "," + notPairedTolerance + "," + notConsecutiveTolerance
                + "," + fullSeq
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
