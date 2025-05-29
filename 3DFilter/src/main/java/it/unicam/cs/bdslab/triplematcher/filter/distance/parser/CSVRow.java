package it.unicam.cs.bdslab.triplematcher.filter.distance.parser;

import it.unicam.cs.bdslab.triplematcher.filter.distance.filter.TripleHelixNotationGenerator;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.DistanceInfo;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.Triple;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            "full_seq",
            "Rna Type",
            "Accession Number",
            "mean_angstroms",
            "mean_direction",
            "chain_id",
            "chain_description",
            "triple_notation",
            "distance_info",
    };
    public static final String HEADERS = String.join(",", HEADERSARRAY) + "\n";
    private final String fileName;
    private final String idFromFile;
    private final int sequenceLength;
    private final int seqSolutionLength;
    private final int bondSolutionLength;
    private final String seqIndexesString;
    private final List<Integer> seqIndexes;
    private final String bondIndexesString;
    private final List<Pair<Integer>> bondIndexes;
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
    private final String accessionNumber;
    private final String RNAType;

    private double meanAngstroms;
    private Direction meanDirection;
    private String SelectedChainId;
    private String SelectedChainDescription;
    private String distanceInfo;
    private List<Triple<DistanceInfo>> distanceInfoList;

    private CSVRow(Builder builder) {
        this.fileName = builder.fileName;
        this.idFromFile = builder.idFromFile;
        this.sequenceLength = builder.sequenceLength;
        this.seqSolutionLength = builder.seqSolutionLength;
        this.bondSolutionLength = builder.bondSolutionLength;
        this.seqIndexes = Arrays.stream(builder.seqIndexes.split(";"))
                .map(Integer::parseInt)
                .map(index -> index - 1) // Convert to 0-based index
                .collect(Collectors.toList());
        this.seqIndexesString = builder.seqIndexes;
        this.bondIndexes = Arrays.stream(builder.bondIndexes.split(";"))
                .map(CSVRow::parseBondWindow)
                .map(pair -> new Pair<>(pair.getFirst() - 1, pair.getSecond() - 1)) // Convert to 0-based index
                .collect(Collectors.toList());
        this.bondIndexesString = builder.bondIndexes;
        this.strMatchSeq = builder.strMatchSeq;
        this.strMatchBond = builder.strMatchBond;
        this.scoreSeq = builder.scoreSeq;
        this.scoreBond = builder.scoreBond;
        this.seqCustomMatchString = builder.seqCustomMatchString;
        this.bondCustomMatchString = builder.bondCustomMatchString;
        this.seqTolerance = builder.seqTolerance;
        this.bondTolerance = builder.bondTolerance;
        this.notPairedTolerance = builder.notPairedTolerance;
        this.notConsecutiveTolerance = builder.notConsecutiveTolerance;
        this.fullSeq = builder.fullSeq;
        this.accessionNumber = builder.accessionNumber;
        this.RNAType = builder.RNAType;
        this.meanAngstroms = Double.NaN;
        this.meanDirection = Direction.LEFT_TO_RIGHT_FIRST_BOND;
    }

    public String getIdFromFile() {
        return idFromFile;
    }

    public String getFullSeq() {
        return fullSeq;
    }

    public String getRNAType() {
        return RNAType;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public List<Integer> getSeqIndexes() {
        return seqIndexes;
    }

    public List<Pair<Integer>> getBondIndexes() {
        return bondIndexes;
    }

    public Direction getMeanDirection() {
        return meanDirection;
    }

    public List<Triple<DistanceInfo>> getDistanceInfoList() {
        return distanceInfoList;
    }

    public double getMeanAngstroms() {
        return meanAngstroms;
    }

    public void setMeanAngstroms(double meanAngstroms) {
        this.meanAngstroms = meanAngstroms;
    }

    public void setMeanDirection(Direction meanDirection) {
        this.meanDirection = meanDirection;
    }

    public void setSelectedChainId(String selectedChainId) {
        SelectedChainId = selectedChainId;
    }

    public void setDistanceInfo(List<Triple<DistanceInfo>> info) {
        this.distanceInfoList = info;
        if (info == null || info.isEmpty()) {
            this.distanceInfo = "[]";
            return;
        }
        // the capacity is calculated as 13 = 2 (the semicolon) + 3 (the number of digits)  * 3 (the numbers for triple) + 2 (the brackets)
        StringBuilder distanceInfoBuilder = new StringBuilder(info.size() * 11 + 2);
        distanceInfoBuilder.append("[");
        for (Triple<DistanceInfo> triple : info) {
            distanceInfoBuilder.append("(")
                    .append(triple.getFirst().formatForCSV())
                    .append("; ")
                    .append(triple.getSecond().formatForCSV()   )
                    .append("; ")
                    .append(triple.getThird().formatForCSV())
                    .append("); ");
        }
        distanceInfoBuilder.delete(distanceInfoBuilder.length() - 2, distanceInfoBuilder.length());
        distanceInfoBuilder.append("]");
        this.distanceInfo = distanceInfoBuilder.toString();
    }

    public void setSelectedChainDescription(String selectedChainDescription) {
        SelectedChainDescription = selectedChainDescription.replace(",", ";")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    public String getCsv() {
        return "\"" + fileName + "\"," +
                "\"" + idFromFile + "\"," +
                "\"" + sequenceLength + "\"," +
                "\"" + seqSolutionLength + "\"," +
                "\"" + bondSolutionLength + "\"," +
                "\"" + seqIndexesString + "\"," +
                "\"" + bondIndexesString + "\"," +
                "\"" + strMatchSeq + "\"," +
                "\"" + strMatchBond + "\"," +
                "\"" + scoreSeq + "\"," +
                "\"" + scoreBond + "\"," +
                "\"" + seqCustomMatchString + "\"," +
                "\"" + bondCustomMatchString + "\"," +
                "\"" + seqTolerance + "\"," +
                "\"" + bondTolerance + "\"," +
                "\"" + notPairedTolerance + "\"," +
                "\"" + notConsecutiveTolerance + "\"," +
                "\"" + fullSeq + "\"," +
                "\"" + RNAType + "\"," +
                "\"" + accessionNumber + "\"," +
                "\"" + Utils.formatDoubleCsv(meanAngstroms) + "\"," +
                "\"" + meanDirection.write() + "\"," +
                "\"" + SelectedChainId + "\"," +
                "\"" + SelectedChainDescription + "\"," +
                "\"" + TripleHelixNotationGenerator.generateTripleHelixNotation(this) + "\"," +
                "\"" + distanceInfo + "\"" +
                "\n";
    }

    private static Pair<Integer> parseBondWindow(String bondWindow) {
        String[] split = bondWindow
                .replace("(", "")
                .replace(")", "")
                .split(",");
        return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public static class Builder {
        private String fileName;
        private String idFromFile;
        private int sequenceLength;
        private int seqSolutionLength;
        private int bondSolutionLength;
        private String seqIndexes;
        private String bondIndexes;
        private String strMatchSeq;
        private String strMatchBond;
        private int scoreSeq;
        private int scoreBond;
        private String seqCustomMatchString;
        private String bondCustomMatchString;
        private int seqTolerance;
        private int bondTolerance;
        private int notPairedTolerance;
        private int notConsecutiveTolerance;
        private String fullSeq;
        private String accessionNumber;
        private String RNAType;

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setIdFromFile(String idFromFile) {
            this.idFromFile = idFromFile;
            return this;
        }

        public Builder setSequenceLength(int sequenceLength) {
            this.sequenceLength = sequenceLength;
            return this;
        }

        public Builder setSeqSolutionLength(int seqSolutionLength) {
            this.seqSolutionLength = seqSolutionLength;
            return this;
        }

        public Builder setBondSolutionLength(int bondSolutionLength) {
            this.bondSolutionLength = bondSolutionLength;
            return this;
        }

        public Builder setSeqIndexes(String seqIndexes) {
            this.seqIndexes = seqIndexes;
            return this;
        }

        public Builder setBondIndexes(String bondIndexes) {
            this.bondIndexes = bondIndexes;
            return this;
        }

        public Builder setStrMatchSeq(String strMatchSeq) {
            this.strMatchSeq = strMatchSeq;
            return this;
        }

        public Builder setStrMatchBond(String strMatchBond) {
            this.strMatchBond = strMatchBond;
            return this;
        }

        public Builder setScoreSeq(int scoreSeq) {
            this.scoreSeq = scoreSeq;
            return this;
        }

        public Builder setScoreBond(int scoreBond) {
            this.scoreBond = scoreBond;
            return this;
        }

        public Builder setSeqCustomMatchString(String seqCustomMatchString) {
            this.seqCustomMatchString = seqCustomMatchString;
            return this;
        }

        public Builder setBondCustomMatchString(String bondCustomMatchString) {
            this.bondCustomMatchString = bondCustomMatchString;
            return this;
        }

        public Builder setSeqTolerance(int seqTolerance) {
            this.seqTolerance = seqTolerance;
            return this;
        }

        public Builder setBondTolerance(int bondTolerance) {
            this.bondTolerance = bondTolerance;
            return this;
        }

        public Builder setNotPairedTolerance(int notPairedTolerance) {
            this.notPairedTolerance = notPairedTolerance;
            return this;
        }

        public Builder setNotConsecutiveTolerance(int notConsecutiveTolerance) {
            this.notConsecutiveTolerance = notConsecutiveTolerance;
            return this;
        }

        public Builder setFullSeq(String fullSeq) {
            this.fullSeq = fullSeq;
            return this;
        }

        public Builder setAccessionNumber(String accessionNumber) {
            this.accessionNumber = accessionNumber;
            return this;
        }

        public Builder setRNAType(String RNAType) {
            this.RNAType = RNAType;
            return this;
        }

        public CSVRow build() {
            return new CSVRow(this);
        }
    }
}