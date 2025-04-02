package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Pair;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;

import java.io.IOException;

public class RNA3DFilter {
    private final double tolerance;

    public static final double ANGSTROMS_THRESHOLD = 12.0;
    public RNA3DFilter(double tolerance) {
        this.tolerance = tolerance;
    }

    public boolean filter(CSVRow row) {
        try {
            Structure structure = StructureIO.getStructure(row.getAccessionNumber());
            DistanceMatrixCalculator calculator = new DistanceMatrixCalculator(structure, row.getRNAType(), row.getFullSeq());
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
            return isDistanceInThreshold(meanDistanceLeft) || isDistanceInThreshold(meanDistanceRight)
                    || isDistanceInThreshold(meanDistanceLeftInverse) || isDistanceInThreshold(meanDistanceRightInverse);
        } catch (IOException | StructureException e) {
            System.err.println("[ERROR] An error occurred while filtering the file" + row.getAccessionNumber());
            return false;
        }
    }

    private boolean isDistanceInThreshold(double distance) {
        return Math.abs(distance - tolerance) < ANGSTROMS_THRESHOLD;
    }

    private double accessMatrix(double[][] matrix, int i, int j) {
        return i < j ? matrix[j][i] : matrix[i][j];
    }
}
