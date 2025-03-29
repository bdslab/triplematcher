package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.WeakBond;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RNAApproximatePatternMatcherTest {

    /**
     * Test class for the solve method in the RNAApproximatePatternMatcher.
     * Methodality:
     * - Checks if the RNASecondaryStructure aligns approximately with the provided pattern of WeakBonds.
     * - Computes the optimal alignment matrix and tracks necessary edit operations.
     */

    @Test
    public void testSolveWithExactMatch() throws Exception {
        RNAApproximatePatternMatcher<WeakBond> matcher = new RNAApproximatePatternMatcher<>(getWeakBonds(
                Arrays.asList(1, 2, 3), Arrays.asList(2, 3, 4)
        ));

        List<WeakBond> pattern = getWeakBonds(
                Arrays.asList(1, 2, 3), Arrays.asList(2, 3, 4)
        );
        boolean result = matcher.solve(pattern);

        assertTrue(result, "solve should return true for an unsolved sequence.");
        assertNotNull(matcher.getAlignmentMatrix());
        assertNotNull(matcher.getOptimalEditOperations());
    }

    @Test
    public void testSolveWithPartialMismatch() throws Exception {
        RNAApproximatePatternMatcher<WeakBond> matcher = new RNAApproximatePatternMatcher<>(getWeakBonds(
                Arrays.asList(1, 3, 5), Arrays.asList(2, 4, 6)
        ));

        List<WeakBond> pattern = getWeakBonds(
                Arrays.asList(1, 3), Arrays.asList(2, 5)
        );
        boolean result = matcher.solve(pattern);

        assertTrue(result, "solve should return true for matching pattern with mismatches.");
        assertNotNull(matcher.getAlignmentMatrix());
        assertEquals(2, matcher.getOptimalEditOperations().size());
    }

    @Test
    public void testSolveWithEmptyPattern() throws Exception {
        RNAApproximatePatternMatcher<WeakBond> matcher = new RNAApproximatePatternMatcher<>(getWeakBonds(
                Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6)
        ));

        List<WeakBond> pattern = new ArrayList<>();
        boolean result = matcher.solve(pattern);

        assertTrue(result, "solve should return true for an empty pattern.");
        assertNotNull(matcher.getAlignmentMatrix());
        assertEquals(0, matcher.getOptimalEditOperations().get(0).size());
    }

    @Test
    public void testSolveAlreadySolved() throws Exception {
        RNAApproximatePatternMatcher<WeakBond> matcher = new RNAApproximatePatternMatcher<>(getWeakBonds(
                Arrays.asList(10, 15, 20), Arrays.asList(11, 16, 21)
        ));

        List<WeakBond> pattern = getWeakBonds(
                Arrays.asList(10, 15), Arrays.asList(11, 16)
        );
        matcher.solve(pattern);
        boolean result = matcher.solve(pattern);

        assertFalse(result, "solve should return false if called after being solved.");
    }

    @Test
    public void testAlignmentMatrixAfterSolve() throws Exception {
        RNAApproximatePatternMatcher<WeakBond> matcher = new RNAApproximatePatternMatcher<>(getWeakBonds(
                Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6)
        ));

        List<WeakBond> pattern = getWeakBonds(
                Arrays.asList(1, 2), Arrays.asList(4, 6)
        );
        matcher.solve(pattern);

        int[][] matrix = matcher.getAlignmentMatrix();
        assertNotNull(matrix, "Alignment matrix should not be null after solving.");
        assertEquals(3, matrix.length, "Matrix should have correct number of rows.");
        assertEquals(4, matrix[0].length, "Matrix should have correct number of columns.");
    }

    @Test
    public void testCorrectAlignmentMatrix() throws Exception {
        RNAApproximatePatternMatcher<WeakBond> matcher = new RNAApproximatePatternMatcher<>(getWeakBonds(
                Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6)
        ));
        List<WeakBond> pattern = getWeakBonds(
                Arrays.asList(1, 2), Arrays.asList(4, 6)
        );
        matcher.solve(pattern);
        int[][] matrix = matcher.getAlignmentMatrix();
        assertTrue(Arrays.stream(matrix[0]).allMatch(x -> x == 0),
                "First row should be all zeros.");
        assertEquals(0, matrix[1][1], "Should match the first element of the pattern.");
        assertEquals(1, matrix[1][2], "Should match the second element of the pattern.");
        assertEquals(1, matrix[1][3], "Should match the third element of the pattern.");
        assertArrayEquals(new int[]{2, 1, 1, 2}, matrix[2], "Should match the last element of the pattern.");
        List<List<EditOperation<WeakBond>>> alignments = matcher.getOptimalEditOperations();
        assertEquals(2, alignments.size(), "Should have two alignments.");
        List<EditOperation<WeakBond>> firstAlignment = alignments.get(0);
        List<EditOperation<WeakBond>> secondAlignment = alignments.get(1);
        List<EditOperation<WeakBond>> expectedFirstAlignment = Arrays.asList(
                new EditOperation<>(new WeakBond(1, 4), new WeakBond(1, 4)),
                new EditOperation<>(new WeakBond(2, 6), null)
        );
        List<EditOperation<WeakBond>> expectedSecondAlignment = Arrays.asList(
                new EditOperation<>(new WeakBond(1, 4), new WeakBond(1, 4)),
                new EditOperation<>(new WeakBond(2, 6), new WeakBond(2, 5))
        );
        assertEquals(expectedFirstAlignment, firstAlignment, "First alignment should be correct.");
        assertEquals(expectedSecondAlignment, secondAlignment, "Second alignment should be correct.");
    }

    private List<WeakBond> getWeakBonds(List<Integer> lefts, List<Integer> rights) {
        List<WeakBond> weakBonds = new ArrayList<>();
        for (int i = 0; i < lefts.size(); i++) {
            weakBonds.add(new WeakBond(lefts.get(i), rights.get(i)));
        }
        return weakBonds;
    }
}