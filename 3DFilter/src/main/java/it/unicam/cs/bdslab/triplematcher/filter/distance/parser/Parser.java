package it.unicam.cs.bdslab.triplematcher.filter.distance.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public class Parser {
    
    public List<CSVRow> parse(Path path){
        List<CSVRow> rows = new ArrayList<>();
        try (CSVParser parser = new CSVParser(Files.newBufferedReader(path), CSVFormat.RFC4180.withFirstRecordAsHeader())) {
            parser.forEach(record -> {
                String fileName = record.get("FileName");
                int length = Integer.parseInt(record.get("length"));
                int startWindowSeq = Integer.parseInt(record.get("start_window_seq"));
                int stopWindowSeq = Integer.parseInt(record.get("stop_window_seq"));
                Pair<Integer> startWindowBond = parsePair(record.get("start_window_bond"));
                Pair<Integer> stopWindowBond = parsePair(record.get("stop_window_bond"));
                String type = "";//record.get("Rna Type");
                rows.add(new CSVRow(fileName, length, startWindowSeq, stopWindowSeq, startWindowBond, stopWindowBond, type));
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private Pair<Integer> parsePair(String pair){
        String[] list = pair.replace("(", "").replace(")", "").split(";");
        return new Pair<>(Integer.parseInt(list[0]), Integer.parseInt(list[1]));
    }

}
