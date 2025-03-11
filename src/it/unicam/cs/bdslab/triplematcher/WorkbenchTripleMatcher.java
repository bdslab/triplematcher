package it.unicam.cs.bdslab.triplematcher;

import it.unicam.cs.bdslab.triplematcher.IO.Application;
import it.unicam.cs.bdslab.triplematcher.IO.ApplicationCSV;
import it.unicam.cs.bdslab.triplematcher.IO.ApplicationSettings;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <code>Java -jar WorkbenchTripleMatcher inputFolder output</code>
 * result is a CSV file named output_(nucleotide)_(base pair)_(tolerance).csv
 * OPTIONS:
 * - n char -> nucleotide letter (A, C, G, U), default U
 * - b charchar -> canonical base pair (AU, UA, GC, CG), default UA
 * - t tolerance -> number of allowed mismatches, default 2
 */
public class WorkbenchTripleMatcher {

    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("n", true, "nucleotide letter (A, C, G, U), default U")
            .addOption("b", true, "canonical base pair (AU, UA, GC, CG), default UA")
            .addOption("t", true, "number of allowed mismatches, default 2")
            .addOption("ml", true, "minimum length of the pattern, default 4")
            .addOption("h", "help", false, "print this message");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            String nucleotide = cmd.getOptionValue("n", "U");
            String basePair = cmd.getOptionValue("b", "UA");
            int tolerance = Integer.parseInt(cmd.getOptionValue("t", "0"));
            int minPatternLength = Integer.parseInt(cmd.getOptionValue("ml", "4"));

            if (cmd.getArgs().length < 2) {
                throw new IllegalArgumentException("Input folder and output file must be specified.");
            }

            Path inputFolder = Paths.get(cmd.getArgs()[0]);
            Path outputFile = Paths.get(cmd.getArgs()[1] + "_" + nucleotide + "_" + basePair + "_" + tolerance + ".csv");
            ApplicationSettings settings = new ApplicationSettings(
                    new CompleteWeakBond(1, 2, basePair.charAt(0), basePair.charAt(1))
                    , nucleotide.charAt(0)
                    , tolerance
                    , minPatternLength
            );
            Application app = new ApplicationCSV(settings);
            app.exportFolder(inputFolder, outputFile);

        } catch (ParseException | IllegalArgumentException e) {
            System.err.println("Error parsing command line options: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Java -jar WorkbenchTripleMatcher inputFolder output", options);
        }
    }
}
