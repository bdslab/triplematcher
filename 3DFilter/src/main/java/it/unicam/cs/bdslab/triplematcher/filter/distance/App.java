package it.unicam.cs.bdslab.triplematcher.filter.distance;

import it.unicam.cs.bdslab.triplematcher.filter.distance.filter.RNA3DFilter;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Parser;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.GenericFileLoader;
import org.apache.commons.cli.*;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.io.PDBFileParser;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class App {
    private static final Logger logger = LoggerFactory.getLogger("filtered");
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final String USAGE = "Usage: java -jar 3DFilter.jar <input_file> <output_file> [Options]";
    public static void main(String[] args) {
        double filterTh = RNA3DFilter.ANGSTROMS_THRESHOLD;
        Options options = new Options()
                .addOption("h", "help", false, "print this message")
                .addOption("t", "tolerance", true, "tolerance in angstroms added to a base distance of " + filterTh + " (e.g., distance < "+ filterTh + " + tolerance), default is 0")
                .addOption("p", "pdb-files", true, "path to a folder containing PDB files, if not provided, the program will download the files");
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cli = parser.parse(options, args);
            if (cli.hasOption("h")) {
                formatter.printHelp(USAGE, options);
                System.exit(1);
            }
            double threshold = Double.parseDouble(cli.getOptionValue("t", "0"));
            Path inputFolder = Paths.get(cli.getArgs()[0]);
            String outputFileName = cli.getArgs()[1];
            if (!outputFileName.endsWith(".csv")) {
                outputFileName += ".csv";
            }
            Path outputFile = Paths.get(outputFileName);
            Path pdbFolder = cli.getOptionValue("p") != null ? Paths.get(cli.getOptionValue("p")) : null;
            logger.info("Tolerance set to {}", threshold);
            logger.info("Input file: {}", inputFolder);
            logger.info("Output file: {}", outputFile);
            if (pdbFolder != null) {
                logger.info("PDB folder: {}", pdbFolder);
            } else {
                logger.info("PDB folder not provided, files will be downloaded");
            }
            Parser csvParser = new Parser();
            List<CSVRow> rows = csvParser.parse(inputFolder);
            logger.info("Read {} rows", rows.size());
            Map<String, List<CSVRow>> groupedRows = rows.stream()
                    .collect(Collectors.groupingBy(CSVRow::getAccessionNumber));
            logger.info("Read {} groups", groupedRows.size());
            GenericFileLoader loader = new GenericFileLoader(pdbFolder);
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(outputFile))) {
                writer.write(CSVRow.HEADERS);
                groupedRows.forEach((key, value) -> {
                    try {
                        RNA3DFilter filter = new RNA3DFilter(threshold, loader.getStructure(key));
                        value.forEach(row -> {
                            boolean isFiltered = false;
                            try {
                                isFiltered = filter.filter(row);
                            } catch (Exception e) {
                                logger.error("An error occurred while filtering row with AccessionNumber: {}", row.getCsv(), e);
                            }
                            if (isFiltered) {
                                try {
                                    writer.write(row.getCsv());
                                    logger.info(GREEN + "Filtered row: {}" + RESET, row.getCsv());
                                } catch (IOException e) {
                                    logger.error("cannot write to file the row {}", row.getAccessionNumber());
                                }
                            } else {
                                logger.info("Row not filtered: {}", row.getAccessionNumber());
                            }
                        });
                    } catch (Exception e) {
                        logger.error("An error occurred while processing rows for AccessionNumber: {}", key, e);
                    }
                });
            }
        } catch (Exception e) {
            formatter.printHelp(USAGE, options);
        }
    }
}