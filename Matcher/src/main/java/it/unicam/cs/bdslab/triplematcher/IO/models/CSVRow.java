package it.unicam.cs.bdslab.triplematcher.IO.models;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.EditOperation;
import it.unicam.cs.bdslab.triplematcher.models.Match;

import java.util.List;
import java.util.stream.IntStream;

public class CSVRow {
    public static final String[] HEADERSARRAY = {
            "FileName",
            "length",
            "solution_length_seq",
            "solution_length_bond",
            "indices_seq",
            "indices_bond",
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
    private final String seqIndexes;
    private final String bondIndexes;
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

    public CSVRow(RNASecondaryStructure rnaSecondaryStructure, Match<WeakBond> bondMatch, Match<Character> seqMatch) {
        this.RNAKey = rnaSecondaryStructure.getDescription();
        this.sequenceLength = rnaSecondaryStructure.getSequence().length();
        this.seqSolutionLength = seqMatch.getLength();
        this.bondSolutionLength = bondMatch.getLength();

        this.seqIndexes = IntStream.range(seqMatch.getCol() - seqMatch.getLength(), seqMatch.getCol())
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a + ";" + b)
                .orElse("");

        this.bondIndexes = bondMatch.getEditOperations().stream()
                .map(EditOperation::getSecond)
                .map(CSVRow::getBondString)
                .reduce((a, b) -> a + ";" + b)
                .orElse("");

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
        return "\"" + RNAKey + "\","
                + "\"" + sequenceLength + "\","
                + "\"" + seqSolutionLength + "\","
                + "\"" + bondSolutionLength + "\","
                + "\"" + seqIndexes + "\","
                + "\"" + bondIndexes + "\","
                + "\"" + strMatchSeq + "\","
                + "\"" + strMatchBond + "\","
                + "\"" + scoreSeq + "\","
                + "\"" + scoreBond + "\","
                + "\"" + seqCustomMatchString + "\","
                + "\"" + bondCustomMatchString + "\","
                + "\"" + seqTolerance + "\","
                + "\"" + bondTolerance + "\","
                + "\"" + notPairedTolerance + "\","
                + "\"" + notConsecutiveTolerance + "\","
                + "\"" + fullSeq + "\"\n";
    }

    private static String getBondString(WeakBond bond) {
        return "(" + bond.getLeft() + "," + bond.getRight() + ")";
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
