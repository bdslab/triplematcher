package it.unicam.cs.bdslab.triplematcher.filter.distance;

import it.unicam.cs.bdslab.triplematcher.filter.distance.filter.RNA3DFilter;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Parser;
import org.apache.commons.cli.*;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
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
            System.out.println("[INFO] Tolerance set to " + threshold);
            System.out.println("[INFO] Input folder: " + inputFolder);
            System.out.println("[INFO] Output file: " + outputFile);
            Parser csvParser = new Parser();
            List<CSVRow> rows = csvParser.parse(inputFolder);
            RNA3DFilter filter = new RNA3DFilter(threshold);
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(outputFile))) {
                writer.write(CSVRow.HEADERS);
                rows.forEach(row -> {
                    try {
                        if (filter.filter(row)) {
                            writer.write(row.getCsv());
                            System.out.println("[INFO] Filtered row: " + row.getCsv());
                        } else
                            System.out.println("[INFO] Row not filtered: " + row.getCsv());
                    } catch (Exception e) {
                        System.err.println("[ERROR] An error occurred while writing the output file");
                    }
                });
            } catch (Exception e) {
                System.err.println("[ERROR] An error occurred while writing the output file");
            }

        } catch (Exception e) {
            formatter.printHelp("3DFilter", options);
        }
    }
}
