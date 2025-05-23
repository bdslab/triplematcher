package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.IO.models.CSVRow;
import it.unicam.cs.bdslab.triplematcher.IO.utils.FolderIterator;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.*;
import it.unicam.cs.bdslab.triplematcher.models.algorithms.RNAApproximateMatcher;
import it.unicam.cs.bdslab.triplematcher.models.algorithms.RNATripleMatcher;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

public class ApplicationCSV implements Application {
    private final ApplicationSettings settings;
    public ApplicationCSV(ApplicationSettings settings) {
        this.settings = settings;
    }
    @Override
    public void exportFile(Path input, Path output) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void exportFolder(Path inputFolder, Path output) {
        FolderIterator folderIterator = new FolderIterator(inputFolder, false);
        MatchCombiner<WeakBond, Character> combiner = new MatchCombiner<>();
        RNATripleMatcher matcher = new RNAApproximateMatcher(this.settings);
        int allResults = 0;
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(output))) {
            writer.write(CSVRow.HEADERS);
            while (folderIterator.hasNext()) {
                RNASecondaryStructure structure = folderIterator.next();
                structure.isNotWeakBond(0);
                List<Pair<Match<CompleteWeakBond>, Match<Character>>> matches = matcher.match(structure, this.settings.getBondPattern(), this.settings.getSeqPattern());
                System.out.println("[INFO] start processing " + structure.getDescription());
                allResults += matches.size();
                if (matches.isEmpty()) {
                    System.err.println("[INFO] no matches found for " + structure.getDescription());
                } else {
                    System.out.println("[INFO] found " + matches.size() + " matches for " + structure.getDescription());
                }
                matches.stream()
                        .map(pair -> new CSVRow(structure, pair.getFirst(), pair.getSecond()))
                        .forEach(row -> {
                            try {
                                writer.write(row.getRow());
                            } catch (IOException exception) {
                                System.err.println("[ERROR] An error occurred while writing the output file");
                            }
                        });
                System.out.println("[INFO] end processing " + structure.getDescription());
            }
            System.out.println("[INFO] end processing all structures");
            System.out.println("[INFO] output file written to " + output);
            System.out.println("[INFO] total results: " + allResults);
        } catch (IOException exception) {
            System.err.println("[ERROR] An error occurred while writing the output file");
        }
    }
}
