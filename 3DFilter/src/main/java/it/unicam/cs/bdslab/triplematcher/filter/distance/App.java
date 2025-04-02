package it.unicam.cs.bdslab.triplematcher.filter.distance;

import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Parser;
import org.apache.commons.cli.*;

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
            double threshold = Double.parseDouble(cli.getOptionValue("t", "12"));
            Path inputFolder = Paths.get(cli.getArgs()[0]);
            Path outputFile = Paths.get(cli.getArgs()[1]);
            System.out.println("[INFO] Tolerance set to " + threshold);
            System.out.println("[INFO] Input folder: " + inputFolder);
            System.out.println("[INFO] Output file: " + outputFile);
            Parser csvParser = new Parser();
            List<CSVRow> rows = csvParser.parse(inputFolder);
            rows.forEach(r -> {
                System.out.println("[INFO] start processing: " + r.getAccessionNumber());

            });
        } catch (Exception e) {
            formatter.printHelp("3DFilter", options);
        }
    }
}
