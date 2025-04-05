package it.unicam.cs.bdslab.triplematcher.filter.distance;

import it.unicam.cs.bdslab.triplematcher.filter.distance.filter.RNA3DFilter;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Parser;
import org.apache.commons.cli.*;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.io.PDBFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class App {
    private static final Logger logger = LoggerFactory.getLogger("filtered");
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        Options options = new Options()
                .addOption("h", "help", false, "Print this message")
                .addOption("t", "tolerance", true, "tolerance in angstroms, default 12");
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cli = parser.parse(options, args);
            if (cli.hasOption("h")) {
                formatter.printHelp("3DFilter", options);
                System.exit(1);
            }
            double threshold = Double.parseDouble(cli.getOptionValue("t", "-3"));
            Path inputFolder = Paths.get(cli.getArgs()[0]);
            Path outputFile = Paths.get(cli.getArgs()[1] + ".csv");
            logger.info("Tolerance set to {}", threshold);
            logger.info("Input folder: {}", inputFolder);
            logger.info("Output file: {}", outputFile);

            Parser csvParser = new Parser();
            List<CSVRow> rows = csvParser.parse(inputFolder);
            Map<String, List<CSVRow>> groupedRows = rows.stream()
                    .collect(Collectors.groupingBy(CSVRow::getAccessionNumber));

            Map<CSVRow, Boolean> results = new ConcurrentHashMap<>();

            groupedRows.forEach((key, value) -> {
                try {
                    RNA3DFilter filter = new RNA3DFilter(threshold, StructureIO.getStructure(key));
                    value.forEach(row -> {
                        boolean isFiltered = filter.filter(row);
                        results.put(row, isFiltered);
                        if (isFiltered) {
                            logger.info(GREEN + "Filtered row: {}" + RESET, row.getCsv());
                        } else {
                            logger.info("Row not filtered: {}", row.getAccessionNumber());
                        }
                    });
                } catch (Exception e) {
                    logger.error("An error occurred while processing rows for AccessionNumber: {}", key, e);
                }
            });

            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(outputFile))) {
                writer.write(CSVRow.HEADERS);
                results.forEach((row, isFiltered) -> {
                    if (isFiltered) {
                        try {
                            writer.write(row.getCsv());
                        } catch (Exception e) {
                            logger.error("An error occurred while writing the output file", e);
                        }
                    }
                });
            } catch (Exception e) {
                logger.error("An error occurred while writing the output file", e);
            }
        } catch (Exception e) {
            formatter.printHelp("3DFilter", options);
        }
    }
}