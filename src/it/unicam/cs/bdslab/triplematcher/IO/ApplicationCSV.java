package it.unicam.cs.bdslab.triplematcher.IO;

import it.unicam.cs.bdslab.triplematcher.IO.models.CSVRow;
import it.unicam.cs.bdslab.triplematcher.IO.utils.FolderIterator;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.*;
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
        List<WeakBond> bondPattern = settings.getBondPatternList();
        List<Character> seqPattern = settings.getSeqPatternList();
        MatchCombiner<WeakBond, Character> combiner = new MatchCombiner<>();
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(output))){
            writer.write(CSVRow.HEADERS);
            while (folderIterator.hasNext()) {
                RNASecondaryStructure structure = folderIterator.next();
                List<Pair<Match<WeakBond>, Match<Character>>> matches = getMatches(structure, combiner, bondPattern, seqPattern);
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

    private List<Pair<Match<WeakBond>, Match<Character>>> getMatches(RNASecondaryStructure structure, MatchCombiner<WeakBond, Character> combiner, List<WeakBond> bondPattern, List<Character> seqPattern) {
        List<WeakBond> bondText = structure.getBonds();
        List<Character> seqText = new ArrayList<>();
        for (char c : structure.getSequence().toCharArray()) {
            seqText.add(c);
        }
        bondText.sort(WeakBond::compareTo);
        return filter(structure, combiner.combine(
                getMatches(bondText, bondPattern),
                getMatches(seqText, seqPattern)
        ));

    }

    private List<Pair<Match<WeakBond>, Match<Character>>> filter(RNASecondaryStructure structure, List<Pair<Match<WeakBond>, Match<Character>>> matches) {
        return filterConsecutiveBondMatches(filterSequence(structure, matches));
    }

    private List<Pair<Match<WeakBond>, Match<Character>>> filterSequence(RNASecondaryStructure structure, List<Pair<Match<WeakBond>, Match<Character>>> matches) {
        return  matches.stream().filter(m -> {
           int numberOfBonds = 0;
           for (int i = m.getSecond().getCol() - 1; i >= m.getFirst().getCol() - m.getSecond().getLength(); i--) {
               if (!structure.isNotWeakBond(i)) {
                   numberOfBonds++;
                   if (numberOfBonds > settings.getTolerance()) {
                          return false;
                   }
               }
           }
           return true;
        }).collect(Collectors.toList());
    }

    private List<Pair<Match<WeakBond>, Match<Character>>> filterConsecutiveBondMatches(List<Pair<Match<WeakBond>, Match<Character>>> matches) {
        List<Pair<Match<WeakBond>, Match<Character>>> filteredMatches = new ArrayList<>();
        for (Pair<Match<WeakBond>, Match<Character>> match : matches) {
            Match<WeakBond> bond = match.getFirst();
            int notConsecutive = 0;
            boolean consecutive = true;
            for (int i = 1; i < bond.getEditOperations().size(); i++) {
                WeakBond previous = bond.getEditOperations().get(i - 1).getSecond();
                WeakBond current = bond.getEditOperations().get(i).getSecond();
                if (previous.getLeft() != current.getLeft() - 1) {
                    notConsecutive++;
                    if (notConsecutive > settings.getTolerance()) {
                        consecutive = false;
                        break;
                    }
                }
            }
            if (consecutive) {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }

    private<T> List<Match<T>> getMatches(List<T> text, List<T> pattern) {
        BasicFilter<T> filter = new BasicFilter<>(text, pattern);
        RNAApproximatePatternMatcher<T> matcher = new RNAApproximatePatternMatcher<>(text);
        return filter.filter(matcher, settings.getTolerance(), settings.getMinPatternLength());
    }
}
