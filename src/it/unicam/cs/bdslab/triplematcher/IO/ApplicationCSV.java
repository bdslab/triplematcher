package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.IO.models.CSVRow;
import it.unicam.cs.bdslab.triplematcher.IO.utils.FolderIterator;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.*;
import it.unicam.cs.bdslab.triplematcher.models.algorithms.RNAApproximateMatcher;
import it.unicam.cs.bdslab.triplematcher.models.algorithms.RNALinearTripleMatcher;
import it.unicam.cs.bdslab.triplematcher.models.algorithms.RNATripleMatcher;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        //RNATripleMatcher matcher = new RNAApproximateMatcher(this.settings.getTolerance(), this.settings.getMinPatternLength(), this.settings.getMaxPatternLength());
        RNATripleMatcher matcher = new RNAApproximateMatcher(this.settings);
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(output))){
            writer.write(CSVRow.HEADERS);
            while (folderIterator.hasNext()) {
                RNASecondaryStructure structure = folderIterator.next();
                List<Pair<Match<WeakBond>, Match<Character>>> matches = matcher.match(structure, this.settings.getBondPattern(), this.settings.getSeqPattern());
                System.out.println("[INFO] start processing " + structure.getDescription());
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
        } catch (IOException exception) {
            System.err.println("[ERROR] An error occurred while writing the output file");
        }
    }
}
