package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.WeakBond;

import java.util.*;

public class RNAApproximatePatternMatcher<T extends Comparable<T>> implements  RNAPatternMatcher<T> {

    private int[][] matrix;
    private final List<T> sequence;
    private boolean solved;
    private List<T> pattern;
    private List<List<EditOperation<T>>> optimalEditOperations;
    public RNAApproximatePatternMatcher(List<T> sequence) {
        this.sequence = sequence;
        this.sequence.sort(T::compareTo);
        solved = false;
    }

    @Override
    public boolean solve(List<T> pattern) {
        if (solved)
            return false;
        this.pattern = pattern;
        initMatrix(pattern.size());
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                int mismatchCost = sequence.get(j - 1).equals(pattern.get(i - 1)) ? 0 : 1;
                // first match/mismatch then insert lastly delete
                matrix[i][j] = getMin(matrix[i-1][j-1] + mismatchCost, matrix[i][j-1] + 1, matrix[i-1][j] + 1);
            }
        }
        this.optimalEditOperations = traceBack();
        solved = true;
        return true;
    }

    @Override
    public List<List<EditOperation<T>>> getOptimalEditOperations() {
        return optimalEditOperations;
    }

    @Override
    public int[][] getAlignmentMatrix() {
        return matrix;
    }

    private void initMatrix(int patternSize){
        matrix = new int[patternSize + 1][sequence.size() + 1];
        // first column is 1,2,...,n
        for (int i = 1; i < patternSize + 1; i++) {
            matrix[i][0] = i;
        }
    }

    private int getMin(int x, int y, int z){
        return Math.min(Math.min(x, y), z);
    }

    private List<List<EditOperation<T>>> traceBack() {
        int[] lastRow = matrix[matrix.length - 1];
        int[] minIndexes = getMinIndexes(lastRow);
        List<List<EditOperation<T>>> editOperations = new ArrayList<>(minIndexes.length);
        for (int minIndex : minIndexes) {
            List<EditOperation<T>> traceback = singleTraceback(minIndex);
            Collections.reverse(traceback);
            editOperations.add(traceback);
        }
        return editOperations;
    }

    private List<EditOperation<T>> singleTraceback(int column) {
        int i = matrix.length - 1;
        int j = column;
        List<EditOperation<T>> editOperations = new ArrayList<>();
        while (j > 0 && i > 0) {
            if (matrix[i][j] == matrix[i-1][j - 1]
                && Objects.equals(sequence.get(j - 1), pattern.get(i - 1))
                || matrix[i][j] == matrix[i-1][j - 1] + 1
            ) {
                editOperations.add(
                        new EditOperation<>(
                            pattern.get(i - 1),
                            sequence.get(i - 1)
                        )
                );
                i--;
                j--;
            } else if (matrix[i][j] == matrix[i - 1][j] + 1) {
                editOperations.add(new EditOperation<>(pattern.get(i - 1), null));
                i--;
            } else if (matrix[i][j] == matrix[i][j - 1] + 1) {
                editOperations.add(new EditOperation<>(null, sequence.get(j - 1)));
                j--;
            }
        }
        return editOperations;
    }

    private int[] getMinIndexes(int[] x) {
        int[] minIndexes = new int[x.length];
        int index = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < x.length; i++) {
            if (x[i] < min) {
                min = x[i];
                index = 0;
                minIndexes[index++] = i;
            } else if (x[i] == min) {
                minIndexes[index++] = i;
            }
        }
        return Arrays.copyOfRange(minIndexes, 0, index);
    }
}
