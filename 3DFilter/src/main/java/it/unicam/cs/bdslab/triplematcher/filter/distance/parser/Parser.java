package it.unicam.cs.bdslab.triplematcher.filter.distance.parser;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {
    private static final Logger logger = LoggerFactory.getLogger("filtered");

    public List<CSVRow> parse(Path path){
        List<CSVRow> rows = new ArrayList<>();
        try (CSVParser parser = new CSVParser(Files.newBufferedReader(path), CSVFormat.RFC4180.withFirstRecordAsHeader())) {
            parser.forEach(record -> {
                CSVRow row = new CSVRow.Builder()
                        .setRNAKey(record.get("FileName"))
                        .setSequenceLength(Integer.parseInt(record.get("length")))
                        .setSeqSolutionLength(Integer.parseInt(record.get("solution_length_seq")))
                        .setBondSolutionLength(Integer.parseInt(record.get("solution_length_bond")))
                        .setSeqWindowStart(Integer.parseInt(record.get("start_window_seq")))
                        .setSeqWindowEnd(Integer.parseInt(record.get("stop_window_seq")))
                        .setBondWindowStart(record.get("start_window_bond"))
                        .setBondWindowEnd(record.get("stop_window_bond"))
                        .setStrMatchSeq(record.get("str_match_seq"))
                        .setStrMatchBond(record.get("str_match_bond"))
                        .setScoreSeq(Integer.parseInt(record.get("real_num_seq")))
                        .setScoreBond(Integer.parseInt(record.get("real_num_bond")))
                        .setSeqCustomMatchString(record.get("match_str_seq"))
                        .setBondCustomMatchString(record.get("match_str_bond"))
                        .setSeqTolerance(Integer.parseInt(record.get("tolerance_seq")))
                        .setBondTolerance(Integer.parseInt(record.get("tolerance_bond")))
                        .setNotPairedTolerance(Integer.parseInt(record.get("tolerance_not_paired")))
                        .setNotConsecutiveTolerance(Integer.parseInt(record.get("tolerance_not_consecutive")))
                        .setFullSeq(record.get("full_seq"))
                        .setRNAType(record.get("Rna Type"))
                        .setAccessionNumber(record.get("Accession number"))
                        .build();
                rows.add(row);
            });
        }
        catch (NoSuchFileException e){
            logger.error("An error occurred while parsing the input file; the field is not present");
        }
        catch (Exception e) {
            logger.error("An error occurred while parsing the input file");
        }

        return rows;
    }
}