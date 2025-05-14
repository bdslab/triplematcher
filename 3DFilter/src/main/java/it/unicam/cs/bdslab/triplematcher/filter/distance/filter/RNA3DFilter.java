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
    private double[][] distanceMatrix;
    private boolean isDistanceMatrixSetFromConstructor = false;
    public static final double ANGSTROMS_THRESHOLD = 10.0;
    private CSVRow lastRow = null;
    private DistanceMatrixCalculator calculator = null;

    public RNA3DFilter(double tolerance, Structure structure) {
        this.tolerance = tolerance;
        this.structure = structure;
    }

    public RNA3DFilter(double tolerance) {
        this.tolerance = tolerance;
        this.structure = null;
    }

    protected RNA3DFilter(double[][] distanceMatrix, double tolerance) {
        this.tolerance = tolerance;
        this.structure = null;
        this.distanceMatrix = distanceMatrix;
        this.isDistanceMatrixSetFromConstructor = true;
    }

    public boolean filter(CSVRow row) {
        try {
            Structure structure = this.structure == null && !this.isDistanceMatrixSetFromConstructor
                    ? StructureIO.getStructure(row.getAccessionNumber())
                    : this.structure;
            calculator = this.isDistanceMatrixSetFromConstructor
                    ? null
                    : this.lastRow != null
                        && this.lastRow.getRNAKey().equals(row.getRNAKey())
                        && this.lastRow.getRNAType().equals(row.getRNAType())
                            ? this.calculator
                            : new DistanceMatrixCalculator(structure, row.getRNAType(), row.getFullSeq());
            if (calculator != null) {
                row.setSelectedChainId(calculator.getChain().getId());
                row.setSelectedChainDescription(calculator.getChain().toString());
                // calculate the distance matrix only if the row is not the same as the last one
                this.distanceMatrix = calculator.getDistanceMatrix();
            }
            this.lastRow = row;
            int seqStart = row.getSeqWindowStart();
            int seqEnd = row.getSeqWindowEnd() - 1;
            Pair<Integer> bondStart = row.getBondWindowStart();
            Pair<Integer> bondEnd = row.getBondWindowEnd();
            // Lists to store triplets
            List<Triple<DistanceInfo>> distanceInfoLeft = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoRight = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoLeftCross = new ArrayList<>();
            List<Triple<DistanceInfo>> distanceInfoRightCross = new ArrayList<>();

            double meanDistanceLeft = 0;
            double meanDistanceRight = 0;
            double meanDistanceLeftCross = 0;
            double meanDistanceRightCross = 0;
            // Calculate means and triplets for the inverse
            int countNormal = 0;
            for (int i = seqStart; i <= seqEnd; i++) {
                int delta = i - seqStart;
                if (isOutOfBounds(i, bondStart.getFirst() - 1 + delta)
                        || isOutOfBounds(i, bondStart.getSecond() - 1 - delta)
                        || isOutOfBounds(i, bondEnd.getFirst() - 1 - delta)
                        || isOutOfBounds(i, bondEnd.getSecond() - 1 + delta)
                ) {
                    continue;
                }
                meanDistanceLeft += accessMatrix(distanceMatrix, i, bondStart.getFirst() - 1 + delta);
                meanDistanceRight += accessMatrix(distanceMatrix, i, bondEnd.getSecond() - 1 + delta);
                distanceInfoLeft.add(createTriplet(distanceMatrix, i, bondStart.getFirst() - 1 + delta, bondStart.getSecond() - 1 - delta));
                distanceInfoRight.add(createTriplet(distanceMatrix, i, bondEnd.getFirst() - 1 - delta, bondEnd.getSecond() - 1 + delta));
                countNormal++;
            }

            int countNormalCross = 0;
            for (int i = seqStart; i <= seqEnd; i++) {
                int delta = i - seqStart;
                if (isOutOfBounds(i, bondStart.getFirst() - 1 + delta)
                        || isOutOfBounds(i, bondStart.getSecond() - 1 - delta)
                        || isOutOfBounds(i, bondEnd.getFirst() - 1 - delta)
                        || isOutOfBounds(i, bondEnd.getSecond() - 1 + delta)
                ) {
                    continue;
                }
                meanDistanceLeftCross += accessMatrix(distanceMatrix, i, bondEnd.getFirst() - 1 - delta);
                meanDistanceRightCross += accessMatrix(distanceMatrix, i, bondStart.getSecond() - 1 - delta);
                distanceInfoLeftCross.add(createTriplet(distanceMatrix, i, bondEnd.getFirst() - 1 - delta, bondEnd.getSecond() - 1 + delta));
                distanceInfoRightCross.add(createTriplet(distanceMatrix, i, bondStart.getFirst() - 1 + delta, bondStart.getSecond() - 1 - delta));
                countNormalCross++;
            }

            return setMean(row,
                    calculateMean(meanDistanceLeft, countNormal),
                    calculateMean(meanDistanceRight, countNormal),
                    calculateMean(meanDistanceLeftCross, countNormalCross),
                    calculateMean(meanDistanceRightCross, countNormalCross),
                    distanceInfoLeft,
                    distanceInfoRight,
                    distanceInfoLeftCross,
                    distanceInfoRightCross);
        } catch (IOException | StructureException e) {
            logger.error("An error occurred while filtering the file {};", row.getAccessionNumber(), e);
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
            double meanDistanceLeftCross,
            double meanDistanceRightCross,
            List<Triple<DistanceInfo>> distanceInfoLeft,
            List<Triple<DistanceInfo>> distanceInfoRight,
            List<Triple<DistanceInfo>> distanceInfoLeftCross,
            List<Triple<DistanceInfo>> distanceInfoRightCross) {
        if (isDistanceInThreshold(meanDistanceLeft)) {
            row.setMeanAngstroms(meanDistanceLeft);
            row.setMeanDirection(Direction.LEFT_TO_RIGHT_FIRST_BOND);
            row.setDistanceInfo(distanceInfoLeft);
    } else if (isDistanceInThreshold(meanDistanceRight)) {
            row.setMeanAngstroms(meanDistanceRight);
            row.setMeanDirection(Direction.LEFT_TO_RIGHT_SECOND_BOND);
            row.setDistanceInfo(distanceInfoRight);
        } else if (isDistanceInThreshold(meanDistanceLeftCross)) {
            row.setMeanAngstroms(meanDistanceLeftCross);
            row.setMeanDirection(Direction.CROSS_LEFT_TO_RIGHT_FIRST_BOND);
            row.setDistanceInfo(distanceInfoLeftCross);
        } else if (isDistanceInThreshold(meanDistanceRightCross)) {
            row.setMeanAngstroms(meanDistanceRightCross);
            row.setMeanDirection(Direction.CROSS_LEFT_TO_RIGHT_SECOND_BOND);
            row.setDistanceInfo(distanceInfoRightCross);
        } else {
            row.setMeanAngstroms(Double.MAX_VALUE);
            row.setMeanDirection(null);
            row.setDistanceInfo(new ArrayList<>());
            return false;
        }
        return true;
    }

    private Triple<DistanceInfo> createTriplet(double[][] distanceMatrix, int sequence, int pairFirst, int pairSecond) {
        return new Triple<>(
                new DistanceInfo(sequence + 1, pairFirst + 1, accessMatrix(distanceMatrix, sequence, pairFirst)),
                new DistanceInfo(sequence + 1, pairSecond + 1, accessMatrix(distanceMatrix, sequence, pairSecond)),
                new DistanceInfo(pairFirst + 1, pairSecond + 1, accessMatrix(distanceMatrix, pairFirst, pairSecond))
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
            boolean isLeftToRight,
            boolean isCross,
            List<Triple<DistanceInfo>> triplets) {

        double meanDistance = 0;
        int count = 0;
        for (int i = 0; i < seqEnd - seqStart; i++) {
            int seqIndex = isLeftToRight ? seqStart + i : seqEnd - i;
            int bondIndex1 = isCross ? bondStart + i : (isLeftToRight ? bondStart + i : bondEnd - i);
            int bondIndex2 = isCross ? bondEnd - i : (isLeftToRight ? bondEnd + i : bondStart - i);
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

    private double calculateMean(double sum, int count) {
        return count == 0 ? Double.MAX_VALUE : sum / count;
    }

    private boolean isOutOfBounds(int i, int j) {
        return i < 0 || j < 0 || i >= distanceMatrix.length || j >= distanceMatrix.length;
    }
}
