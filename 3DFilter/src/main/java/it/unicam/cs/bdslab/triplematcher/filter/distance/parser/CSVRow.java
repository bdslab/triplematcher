package it.unicam.cs.bdslab.triplematcher.filter.distance.parser;

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
            "Rna Type",
            "Accession Number",
    };
    public static final String HEADERS = String.join(",", HEADERSARRAY) + "\n";
    private final String RNAKey;
    private final int sequenceLength;
    private final int seqSolutionLength;
    private final int bondSolutionLength;
    private final int seqWindowStart;
    private final int seqWindowEnd;
    private final String bondWindowStart;
    private final String bondWindowEnd;
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

    private CSVRow(Builder builder) {
        this.RNAKey = builder.RNAKey;
        this.sequenceLength = builder.sequenceLength;
        this.seqSolutionLength = builder.seqSolutionLength;
        this.bondSolutionLength = builder.bondSolutionLength;
        this.seqWindowStart = builder.seqWindowStart;
        this.seqWindowEnd = builder.seqWindowEnd;
        this.bondWindowStart = builder.bondWindowStart;
        this.bondWindowEnd = builder.bondWindowEnd;
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
    }

    public String getRNAKey() {
        return RNAKey;
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

    public int getSeqWindowStart() {
        return seqWindowStart;
    }

    public int getSeqWindowEnd() {
        return seqWindowEnd;
    }

    public Pair<Integer> getBondWindowStart() {
        return parseBondWindow(bondWindowStart);
    }

    public Pair<Integer> getBondWindowEnd() {
        return parseBondWindow(bondWindowEnd);
    }

    public String getCsv() {
        return RNAKey + "," +
                sequenceLength + "," +
                seqSolutionLength + "," +
                bondSolutionLength + "," +
                seqWindowStart + "," +
                seqWindowEnd + "," +
                bondWindowStart + "," +
                bondWindowEnd + "," +
                strMatchSeq + "," +
                strMatchBond + "," +
                scoreSeq + "," +
                scoreBond + "," +
                seqCustomMatchString + "," +
                bondCustomMatchString + "," +
                seqTolerance + "," +
                bondTolerance + "," +
                notPairedTolerance + "," +
                notConsecutiveTolerance + "," +
                fullSeq + "," +
                RNAType + "," +
                accessionNumber + "\n";
    }

    private Pair<Integer> parseBondWindow(String bondWindow) {
        String[] split = bondWindow
                .replace("(", "")
                .replace(")", "")
                .split(";");
        return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }






    public static class Builder {
        private String RNAKey;
        private int sequenceLength;
        private int seqSolutionLength;
        private int bondSolutionLength;
        private int seqWindowStart;
        private int seqWindowEnd;
        private String bondWindowStart;
        private String bondWindowEnd;
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

        public Builder setRNAKey(String RNAKey) {
            this.RNAKey = RNAKey;
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

        public Builder setSeqWindowStart(int seqWindowStart) {
            this.seqWindowStart = seqWindowStart;
            return this;
        }

        public Builder setSeqWindowEnd(int seqWindowEnd) {
            this.seqWindowEnd = seqWindowEnd;
            return this;
        }

        public Builder setBondWindowStart(String bondWindowStart) {
            this.bondWindowStart = bondWindowStart;
            return this;
        }

        public Builder setBondWindowEnd(String bondWindowEnd) {
            this.bondWindowEnd = bondWindowEnd;
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