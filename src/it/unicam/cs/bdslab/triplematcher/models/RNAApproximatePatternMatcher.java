package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.models.utils.Utils;

import java.util.*;

public class RNAApproximatePatternMatcher<T> implements  RNAPatternMatcher<T> {

    private int[][] matrix;
    private final List<T> text;
    private boolean solved;
    private List<T> pattern;
    private List<List<EditOperation<T>>> optimalEditOperations;
    public RNAApproximatePatternMatcher(List<T> text) {
        this.text = text;
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
                int mismatchCost = text.get(j - 1).equals(pattern.get(i - 1)) ? 0 : 1;
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

    @Override
    public int getScore() {
        return optimalEditOperations.get(0).size();
    }

    @Override
    public List<EditOperation<T>> traceback(int i, int j) {
        return singleTraceback(i, j);
    }

    private void initMatrix(int patternSize){
        matrix = new int[patternSize + 1][text.size() + 1];
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
        int[] minIndexes = Utils.getMins(lastRow);
        List<List<EditOperation<T>>> editOperations = new ArrayList<>(minIndexes.length);
        for (int minIndex : minIndexes) {
            List<EditOperation<T>> traceback = singleTraceback(minIndex);
            Collections.reverse(traceback);
            editOperations.add(traceback);
        }
        return editOperations;
    }

    private List<EditOperation<T>> singleTraceback(int row, int column) {
        int i = row;
        int j = column;
        List<EditOperation<T>> editOperations = new ArrayList<>();
        while (j > 0 && i > 0) {
            if (matrix[i][j] == matrix[i-1][j - 1]
                && Objects.equals(text.get(j - 1), pattern.get(i - 1))
                || matrix[i][j] == matrix[i-1][j - 1] + 1
            ) {
                editOperations.add(
                        new EditOperation<>(
                            pattern.get(i - 1),
                            text.get(i - 1)
                        )
                );
                i--;
                j--;
            } else if (matrix[i][j] == matrix[i - 1][j] + 1) {
                editOperations.add(new EditOperation<>(pattern.get(i - 1), null));
                i--;
            } else if (matrix[i][j] == matrix[i][j - 1] + 1) {
                editOperations.add(new EditOperation<>(null, text.get(j - 1)));
                j--;
            }
        }
        return editOperations;
    }

    private List<EditOperation<T>> singleTraceback(int column) {
        return singleTraceback(matrix.length - 1, column);
    }
}
