package it.unicam.cs.bdslab.triplematcher.IO.models;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.EditOperation;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterNotConsecutiveBond;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterOnlyPseudoknot;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterUnpairedNucletides;

import java.util.List;
import java.util.stream.IntStream;

public class CSVRow {
    public static final String[] HEADERSARRAY = {
            "file_name",
            "id_from_file",
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
            "tolerance_pseudoknot",
            "full_seq",
    };
    public static final String HEADERS = String.join(",", HEADERSARRAY) + "\n";
    private final String idFromFile;
    private final String fileName;
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
    private final int pseudoknotTolerance;
    private final String fullSeq;

    public CSVRow(RNASecondaryStructure rnaSecondaryStructure, Match<CompleteWeakBond> bondMatch, Match<Character> seqMatch) {
        this.fileName = rnaSecondaryStructure.getDescription();
        this.idFromFile = rnaSecondaryStructure.getDescription().split("\\.")[0];
        this.sequenceLength = rnaSecondaryStructure.getSequence().length();
        this.seqSolutionLength = seqMatch.getLength();
        this.bondSolutionLength = bondMatch.getLength();

        this.seqIndexes = IntStream.range(seqMatch.getCol() - seqMatch.getLength() + 1, seqMatch.getCol() + 1)
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
        this.notPairedTolerance = seqMatch.getFilterTolerance(FilterUnpairedNucletides.class.getName());
        this.notConsecutiveTolerance = bondMatch.getFilterTolerance(FilterNotConsecutiveBond.class.getName());
        this.pseudoknotTolerance = bondMatch.getFilterTolerance(FilterOnlyPseudoknot.class.getName());
        this.fullSeq = rnaSecondaryStructure.getSequence();
    }

    public String getRow() {
        return "\"" + fileName + "\","
                + "\"" + idFromFile + "\","
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
                + "\"" + pseudoknotTolerance + "\","
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
