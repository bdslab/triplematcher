package it.unicam.cs.bdslab.triplematcher.filter.distance.parser;

public class CSVRow {
    private String[] headers = new String[] {
        "FileName",
        "length",
        "start_window_seq",
        "stop_window_seq",
        "start_window_bond",
        "stop_window_bond"
    };

    private String fileName;
    private int length;
    private int startWindowSeq;
    private int stopWindowSeq;
    private Pair<Integer> startWindowBond;
    private Pair<Integer> stopWindowBond;
    private String type;

    public CSVRow(String fileName
    , int length
    , int startWindowSeq
    , int stopWindowSeq
    , Pair<Integer> startWindowBond
    , Pair<Integer> stopWindowBond
    , String type
    ) {
        this.fileName = fileName;
        this.length = length;
        this.startWindowSeq = startWindowSeq;
        this.stopWindowSeq = stopWindowSeq;
        this.startWindowBond = startWindowBond;
        this.stopWindowBond = stopWindowBond;
        this.type = type;
    }




}
