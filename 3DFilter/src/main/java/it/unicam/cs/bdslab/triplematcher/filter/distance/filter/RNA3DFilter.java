package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Direction;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Pair;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.DistanceInfo;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.Triple;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RNA3DFilter {
    private static final Logger logger = LoggerFactory.getLogger("filtered");
    private final double tolerance;
    private final Structure structure;
    public static final double ANGSTROMS_THRESHOLD = 10.0;
    public RNA3DFilter(double tolerance, Structure structure) {
        this.tolerance = tolerance;
        this.structure = structure;
    }

    public RNA3DFilter(double tolerance) {
        this.tolerance = tolerance;
        this.structure = null;
    }

    public boolean filter(CSVRow row) {
        try {
            Structure structure = this.structure == null ? StructureIO.getStructure(row.getAccessionNumber()) : this.structure;
            DistanceMatrixCalculator calculator = new DistanceMatrixCalculator(structure, row.getRNAType(), row.getFullSeq());
            row.setSelectedChainId(calculator.getChain().getId());
            row.setSelectedChainDescription(calculator.getChain().toString());
            double[][] distanceMatrix = calculator.getDistanceMatrix();
            int seqStart = row.getSeqWindowStart() - 1;
            int seqEnd = row.getSeqWindowEnd() - 1;
            Pair<Integer> bondStart = row.getBondWindowStart();
            Pair<Integer> bondEnd = row.getBondWindowEnd();
            // Lists to store triplets
            List<Triple<DistanceInfo>> distanceInfoLeft = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoRight = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoLeftInverse = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoRightInverse = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoLeftCross = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoRightCross = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoLeftCrossInverse = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoRightCrossInverse = new ArrayList<>();

            // Calculate means and triplets
            double meanDistanceLeft = calculateMeanAndTriplets(distanceMatrix, seqStart, seqEnd, bondStart.getFirst(), bondStart.getSecond(), true, false, distanceInfoLeft);
            double meanDistanceRight = calculateMeanAndTriplets(distanceMatrix, seqStart, seqEnd, bondStart.getSecond(), bondStart.getFirst(), true, false, distanceInfoRight);
            double meanDistanceLeftInverse = calculateMeanAndTriplets(distanceMatrix, seqStart, seqEnd, bondStart.getFirst(), bondStart.getSecond(), false, false, distanceInfoLeftInverse);
            double meanDistanceRightInverse = calculateMeanAndTriplets(distanceMatrix, seqStart, seqEnd, bondStart.getSecond(), bondStart.getFirst(), false, false, distanceInfoRightInverse);
            double meanDistanceLeftCross = calculateMeanAndTriplets(distanceMatrix, seqStart, seqEnd, bondEnd.getFirst(), bondEnd.getFirst(), true, true, distanceInfoLeftCross);
            double meanDistanceRightCross = calculateMeanAndTriplets(distanceMatrix, seqStart, seqEnd, bondEnd.getSecond(), bondEnd.getSecond(), true, true, distanceInfoRightCross);
            double meanDistanceLeftCrossInverse = calculateMeanAndTriplets(distanceMatrix, seqStart, seqEnd, bondStart.getFirst(), bondStart.getFirst(), false, true, distanceInfoLeftCrossInverse);
            double meanDistanceRightCrossInverse = calculateMeanAndTriplets(distanceMatrix, seqStart, seqEnd, bondStart.getSecond(), bondStart.getSecond(), false, true, distanceInfoRightCrossInverse);
            return setMean(row,
                    meanDistanceLeft,
                    meanDistanceRight,
                    meanDistanceLeftInverse,
                    meanDistanceRightInverse,
                    meanDistanceLeftCross,
                    meanDistanceRightCross,
                    meanDistanceLeftCrossInverse,
                    meanDistanceRightCrossInverse,
                    distanceInfoLeft,
                    distanceInfoRight,
                    distanceInfoLeftInverse,
                    distanceInfoRightInverse,
                    distanceInfoLeftCross,
                    distanceInfoRightCross,
                    distanceInfoLeftCrossInverse,
                    distanceInfoRightCrossInverse);
        } catch (IOException | StructureException e) {
            logger.error("An error occurred while filtering the file {}", row.getAccessionNumber());
            return false;
        }
    }

    private boolean isDistanceInThreshold(double distance) {
        return Math.abs(distance - tolerance) < ANGSTROMS_THRESHOLD;
    }

    private boolean setMean(
            CSVRow row,
            double meanDistanceLeft,
            double meanDistanceRight,
            double meanDistanceLeftInverse,
            double meanDistanceRightInverse,
            double meanDistanceLeftCross,
            double meanDistanceRightCross,
            double meanDistanceLeftCrossInverse,
            double meanDistanceRightCrossInverse,
            List<Triple<DistanceInfo>> distanceInfoLeft,
            List<Triple<DistanceInfo>> distanceInfoRight,
            List<Triple<DistanceInfo>> distanceInfoLeftInverse,
            List<Triple<DistanceInfo>> distanceInfoRightInverse,
            List<Triple<DistanceInfo>> distanceInfoLeftCross,
            List<Triple<DistanceInfo>> distanceInfoRightCross,
            List<Triple<DistanceInfo>> distanceInfoLeftCrossInverse,
            List<Triple<DistanceInfo>> distanceInfoRightCrossInverse) {

        if (isDistanceInThreshold(meanDistanceLeft)) {
            row.setMeanAngstroms(meanDistanceLeft);
            row.setMeanDirection(Direction.LEFT_TO_RIGHT_FIRST_BOND);
            row.setDistanceInfo(distanceInfoLeft);
        } else if (isDistanceInThreshold(meanDistanceRight)) {
            row.setMeanAngstroms(meanDistanceRight);
            row.setMeanDirection(Direction.LEFT_TO_RIGHT_SECOND_BOND);
            row.setDistanceInfo(distanceInfoRight);
        } else if (isDistanceInThreshold(meanDistanceLeftInverse)) {
            row.setMeanAngstroms(meanDistanceLeftInverse);
            row.setMeanDirection(Direction.RIGHT_TO_LEFT_FIRST_BOND);
            row.setDistanceInfo(distanceInfoLeftInverse);
        } else if (isDistanceInThreshold(meanDistanceRightInverse)) {
            row.setMeanAngstroms(meanDistanceRightInverse);
            row.setMeanDirection(Direction.RIGHT_TO_LEFT_SECOND_BOND);
            row.setDistanceInfo(distanceInfoRightInverse);
        } else if (isDistanceInThreshold(meanDistanceLeftCross)) {
            row.setMeanAngstroms(meanDistanceLeftCross);
            row.setMeanDirection(Direction.CROSS_LEFT_TO_RIGHT_FIRST_BOND);
            row.setDistanceInfo(distanceInfoLeftCross);
        } else if (isDistanceInThreshold(meanDistanceRightCross)) {
            row.setMeanAngstroms(meanDistanceRightCross);
            row.setMeanDirection(Direction.CROSS_LEFT_TO_RIGHT_SECOND_BOND);
            row.setDistanceInfo(distanceInfoRightCross);
        } else if (isDistanceInThreshold(meanDistanceLeftCrossInverse)) {
            row.setMeanAngstroms(meanDistanceLeftCrossInverse);
            row.setMeanDirection(Direction.CROSS_RIGHT_TO_LEFT_FIRST_BOND);
            row.setDistanceInfo(distanceInfoLeftCrossInverse);
        } else if (isDistanceInThreshold(meanDistanceRightCrossInverse)) {
            row.setMeanAngstroms(meanDistanceRightCrossInverse);
            row.setMeanDirection(Direction.CROSS_RIGHT_TO_LEFT_SECOND_BOND);
            row.setDistanceInfo(distanceInfoRightCrossInverse);
        } else {
            return false;
        }
        return true;
    }

    private Triple<DistanceInfo> createTriplet(double[][] distanceMatrix, int sequence, int pairFirst, int pairSecond) {
        return new Triple<>(
                new DistanceInfo(sequence, pairFirst - 1, accessMatrix(distanceMatrix, sequence, pairFirst - 1)),
                new DistanceInfo(sequence, pairSecond - 1, accessMatrix(distanceMatrix, sequence, pairSecond - 1)),
                new DistanceInfo(pairFirst - 1, pairSecond - 1, accessMatrix(distanceMatrix, pairFirst - 1, pairSecond - 1))
        );
    }

    private double accessMatrix(double[][] matrix, int i, int j) {
        return i < j ? matrix[j][i] : matrix[i][j];
    }

    private double calculateMeanAndTriplets(
            double[][] distanceMatrix,
            int seqStart,
            int seqEnd,
            int bondStart,
            int bondEnd,
            boolean isIncremental,
            boolean isCross,
            List<Triple<DistanceInfo>> triplets) {

        double meanDistance = 0;
        int count = 0;
        for (int i = 0; i < seqEnd - seqStart; i++) {
            int seqIndex = isIncremental ? seqStart + i : seqEnd - i;
            int bondIndex1 = isCross ? bondStart + i : (isIncremental ? bondStart + i : bondEnd - i);
            int bondIndex2 = isCross ? bondEnd - i : (isIncremental ? bondEnd + i : bondStart - i);
            bondIndex1 -= 1;
            bondIndex2 -= 1;
            if (seqIndex < distanceMatrix.length && bondIndex1 < distanceMatrix.length && bondIndex2 < distanceMatrix.length && bondIndex1 > 0 && bondIndex2 > 0) {
                triplets.add(createTriplet(distanceMatrix, seqIndex, bondIndex1, bondIndex2));
                meanDistance += accessMatrix(distanceMatrix, seqIndex, bondIndex1);
                count++;
            } else
                return Double.MAX_VALUE;
        }
        if (meanDistance == 0 || count == 0) {
            return Double.MAX_VALUE;
        }
        return meanDistance / count;
    }

}
