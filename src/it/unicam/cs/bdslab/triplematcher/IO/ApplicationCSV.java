package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.IO.models.CSVRow;
import it.unicam.cs.bdslab.triplematcher.IO.utils.FolderIterator;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.BasicFilter;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.MatchCombiner;
import it.unicam.cs.bdslab.triplematcher.models.RNAApproximatePatternMatcher;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
        List<WeakBond> bondPattern = settings.getBondPatternList();
        List<Character> seqPattern = settings.getSeqPatternList();
        MatchCombiner<WeakBond, Character> combiner = new MatchCombiner<>();
        try {
            OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(output));
            writer.write(CSVRow.HEADERS);
            while (folderIterator.hasNext()) {
                RNASecondaryStructure structure = folderIterator.next();
                List<Pair<Match<WeakBond>, Match<Character>>> matches = getMatches(structure, combiner, bondPattern, seqPattern);
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

    private List<Pair<Match<WeakBond>, Match<Character>>> getMatches(RNASecondaryStructure structure, MatchCombiner<WeakBond, Character> combiner, List<WeakBond> bondPattern, List<Character> seqPattern) {
        List<WeakBond> bondText = structure.getBonds();
        List<Character> seqText = new ArrayList<>();
        for (char c : structure.getSequence().toCharArray()) {
            seqText.add(c);
        }
        bondText.sort(WeakBond::compareTo);
        return combiner.combine(
                getMatches(bondText, bondPattern, settings.getTolerance()),
                getMatches(seqText, seqPattern, settings.getTolerance())
        );

    }

    private<T> List<Match<T>> getMatches(List<T> text, List<T> pattern, int tolerance) {
        BasicFilter<T> filter = new BasicFilter<>(text, pattern);
        RNAApproximatePatternMatcher<T> matcher = new RNAApproximatePatternMatcher<>(text);
        return filter.filter(matcher, tolerance, settings.getMinPatternLength());
    }
}
