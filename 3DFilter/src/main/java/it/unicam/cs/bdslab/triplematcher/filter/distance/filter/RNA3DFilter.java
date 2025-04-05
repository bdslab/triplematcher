package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Direction;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Pair;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;

public class RNA3DFilter {
    private static final Logger logger = LoggerFactory.getLogger("filtered");
    private final double tolerance;
    private final Structure structure;
    public static final double ANGSTROMS_THRESHOLD = 12.0;
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
            int seqStart = row.getSeqWindowStart();
            int seqEnd = row.getSeqWindowEnd();
            Pair<Integer> bondStart = row.getBondWindowStart();
            Pair<Integer> bondEnd = row.getBondWindowEnd();
            double meanDistanceLeft = 0;
            double meanDistanceRight = 0;
            double meanDistanceLeftInverse = 0;
            double meanDistanceRightInverse = 0;
            for (int i = 0; i < seqEnd - seqStart && i + seqStart < distanceMatrix.length && i < distanceMatrix.length + bondStart.getFirst() && i + bondStart.getSecond() < distanceMatrix.length; i++) {
                meanDistanceLeft += accessMatrix(distanceMatrix, seqStart + i, bondStart.getFirst() + i);
                meanDistanceRight += accessMatrix(distanceMatrix, seqStart + i, bondStart.getSecond() + i);
            }
            for (int i = 0; i < seqEnd - seqStart && i + seqStart < distanceMatrix.length && i < distanceMatrix.length + bondStart.getFirst() && i + bondStart.getSecond() < distanceMatrix.length; i++) {
                meanDistanceLeftInverse += accessMatrix(distanceMatrix, bondEnd.getFirst() - i, seqEnd - i);
                meanDistanceRightInverse += accessMatrix(distanceMatrix, bondStart.getSecond() - i, seqEnd - i);
            }

            meanDistanceLeft = meanDistanceLeft / (seqEnd - seqStart);
            meanDistanceRight = meanDistanceRight / (seqEnd - seqStart);
            meanDistanceLeftInverse = meanDistanceLeftInverse / (seqEnd - seqStart);
            meanDistanceRightInverse = meanDistanceRightInverse / (seqEnd - seqStart);
            return setMean(row, meanDistanceLeft, meanDistanceRight, meanDistanceLeftInverse, meanDistanceRightInverse);
        } catch (IOException | StructureException e) {
            logger.error("An error occurred while filtering the file {}", row.getAccessionNumber());
            return false;
        }
    }

    private boolean isDistanceInThreshold(double distance) {
        return Math.abs(distance - tolerance) < ANGSTROMS_THRESHOLD;
    }

    private boolean setMean(CSVRow row, double meanDistanceLeft, double meanDistanceRight, double meanDistanceLeftInverse, double meanDistanceRightInverse) {
        if (isDistanceInThreshold(meanDistanceLeft)) {
            row.setMeanAngstroms(meanDistanceLeft);
            row.setMeanDirection(Direction.LEFT_TO_RIGHT_FIRST_BOND);
        } else if (isDistanceInThreshold(meanDistanceRight)) {
            row.setMeanAngstroms(meanDistanceRight);
            row.setMeanDirection(Direction.LEFT_TO_RIGHT_SECOND_BOND);
        } else if (isDistanceInThreshold(meanDistanceLeftInverse)) {
            row.setMeanAngstroms(meanDistanceLeftInverse);
            row.setMeanDirection(Direction.RIGHT_TO_LEFT_FIRST_BOND);
        } else if (isDistanceInThreshold(meanDistanceRightInverse)) {
            row.setMeanAngstroms(meanDistanceRightInverse);
            row.setMeanDirection(Direction.RIGHT_TO_LEFT_SECOND_BOND);
        } else {
            return false;
        }
        return true;
    }

    private double accessMatrix(double[][] matrix, int i, int j) {
        return i < j ? matrix[j][i] : matrix[i][j];
    }
}
