package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Direction;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.DistanceInfo;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.Triple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class TripleHelixNotationTest {

    @Test
    void generateTripleHelixNotation_correctlyGeneratesNotationForValidInput() {
        CSVRow row = new CSVRow.Builder()
                .setFullSeq("ACGUACGU")
                .setSeqIndexes("1")
                .setBondIndexes("(1,2)")
                .build();
        row.setMeanDirection(Direction.LEFT_TO_RIGHT_FIRST_BOND);

        List<Triple<DistanceInfo>> distanceInfoList = new ArrayList<>();
        distanceInfoList.add(new Triple<>(new DistanceInfo(1, 2, 1), new DistanceInfo(3, 4, 1), new DistanceInfo(4, 5, 1)));
        row.setDistanceInfo(distanceInfoList);

        String result = TripleHelixNotationGenerator.generateTripleHelixNotation(row);

        assertEquals("zZ------", result);
    }

    @Test
    void generateTripleHelixNotation_handlesEmptySequence() {
        CSVRow row = new CSVRow.Builder()
                .setFullSeq("")
                .setSeqIndexes("1")
                .setBondIndexes("(1,2)")
                .build();
        row.setMeanDirection(Direction.LEFT_TO_RIGHT_FIRST_BOND);
        row.setDistanceInfo(new ArrayList<>());

        String result = TripleHelixNotationGenerator.generateTripleHelixNotation(row);

        assertEquals("", result);
    }

    @Test
    void generateTripleHelixNotation_handlesNoDistanceInfo() {
        CSVRow row = new CSVRow.Builder()
                .setFullSeq("ACGUACGU")
                .setSeqIndexes("1")
                .setBondIndexes("(1,2)")
                .build();
        row.setMeanDirection(Direction.LEFT_TO_RIGHT_FIRST_BOND);
        row.setDistanceInfo(new ArrayList<>());

        String result = TripleHelixNotationGenerator.generateTripleHelixNotation(row);

        assertEquals("--------", result);
    }

    @Test
    void generateTripleHelixNotation_handlesMultipleTriplets() {
        CSVRow row = new CSVRow.Builder()
                .setFullSeq("ACGUACGU")
                .setSeqIndexes("1")
                .setBondIndexes("(1,2)")
                .build();
        row.setMeanDirection(Direction.LEFT_TO_RIGHT_FIRST_BOND);

        List<Triple<DistanceInfo>> distanceInfoList = new ArrayList<>();
        distanceInfoList.add(new Triple<>(new DistanceInfo(1, 2, 1), new DistanceInfo(3, 2, 3), new DistanceInfo(4, 5, 1)));
        distanceInfoList.add(new Triple<>(new DistanceInfo(7, 8, 1), new DistanceInfo(1, 2, 1), new DistanceInfo(2, 3, 1)));
        row.setDistanceInfo(distanceInfoList);

        String result = TripleHelixNotationGenerator.generateTripleHelixNotation(row);

        assertEquals("zZ----yY", result);
    }
}

