package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.DistanceInfo;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.Triple;

import java.util.List;
import java.util.stream.Collectors;

public class TripleHelixNotationGenerator {

    public static String generateTripleHelixNotation(CSVRow row) {
        if (row.getDistanceInfoList() == null) {
            return "";
        }
        StringBuilder result = new StringBuilder(row.getFullSeq().length());
        List<DistanceInfo> distances = row.getDistanceInfoList().stream()
                .map(triple -> row.getMeanDirection().isFirstBond()
                    ? triple.getFirst()
                    : triple.getSecond()
                )
                .collect(Collectors.toList());
        for (int i = 0; i < row.getFullSeq().length(); i++) {
            result.append("-");
        }
        char character = 'z';
        for (DistanceInfo distance : distances) {
            result.setCharAt(distance.getIndexFirst(), character);
            result.setCharAt(distance.getIndexSecond(), toUpperCase(character));
            character--;
        }

        return result.toString();
    }

    private static char toUpperCase(char c) {
        return (char) (c & 0x5f);
    }
}
