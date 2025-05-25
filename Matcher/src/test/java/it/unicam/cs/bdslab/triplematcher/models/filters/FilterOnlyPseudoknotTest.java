package it.unicam.cs.bdslab.triplematcher.models.filters;

import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.EditOperation;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is a placeholder for tests related to the FilterOnlyPseudoknot filter.
 */
public class FilterOnlyPseudoknotTest {

    @Test
    void testFilterOnlyPseudoknot() {
        // ARRANGE
        List<EditOperation<CompleteWeakBond>> editOperations = Arrays.asList(
                new EditOperation<>(new CompleteWeakBond(1, 10, 'A', 'A', true), new CompleteWeakBond(1, 10, 'A', 'A', true)),
                new EditOperation<>(new CompleteWeakBond(2, 11, 'B', 'B', true), new CompleteWeakBond(2, 11, 'B', 'B', true)),
                new EditOperation<>(new CompleteWeakBond(3, 12, 'C', 'C', true), new CompleteWeakBond(3, 12, 'C', 'C', true)),
                new EditOperation<>(new CompleteWeakBond(4, 13, 'D', 'D', true), new CompleteWeakBond(4, 13, 'D', 'D', true))
        );
        Match<CompleteWeakBond> bond1 = new Match<>(1, 2, 0, new ArrayList<>(), new ArrayList<>(), editOperations);
        Match<Character> match = new Match<>(1, 2, 0, new ArrayList<>(), new ArrayList<>(), Collections.emptyList());
        Pair<Match<CompleteWeakBond>, Match<Character>> pair = new Pair<>(bond1, match);
        FilterOnlyPseudoknot filter = new FilterOnlyPseudoknot(2);
        // ACT
        boolean result = filter.test(null, pair);
        // ASSERT
        assertTrue(result, "Expected filter to pass for pseudoknot bonds");
        assertEquals(0, pair.getFirst().getFilterTolerance(FilterOnlyPseudoknot.class.getName()),
                "Expected filter tolerance to be 0 for pseudoknot bonds");
    }

    @Test
    void testFalseIfNotPseudoknot() {
        List<EditOperation<CompleteWeakBond>> editOperations = Arrays.asList(
                new EditOperation<>(new CompleteWeakBond(1, 10, 'A', 'A', false), new CompleteWeakBond(1, 10, 'A', 'A', false)),
                new EditOperation<>(new CompleteWeakBond(2, 11, 'B', 'B', false), new CompleteWeakBond(2, 11, 'B', 'B', false)),
                new EditOperation<>(new CompleteWeakBond(3, 12, 'C', 'C', false), new CompleteWeakBond(3, 12, 'C', 'C', false)),
                new EditOperation<>(new CompleteWeakBond(4, 13, 'D', 'D', false), new CompleteWeakBond(4, 13, 'D', 'D', false))
        );
        Match<CompleteWeakBond> bond1 = new Match<>(1, 2, 0, new ArrayList<>(), new ArrayList<>(), editOperations);
        Match<Character> match = new Match<>(1, 2, 0, new ArrayList<>(), new ArrayList<>(), Collections.emptyList());
        Pair<Match<CompleteWeakBond>, Match<Character>> pair = new Pair<>(bond1, match);
        FilterOnlyPseudoknot filter = new FilterOnlyPseudoknot(2);
        // ACT
        boolean result = filter.test(null, pair);
        // ASSERT
        assertFalse(result, "Expected filter to fail for non-pseudoknot bonds");
        assertEquals(3, pair.getFirst().getFilterTolerance(FilterOnlyPseudoknot.class.getName()), 0,
                "Expected filter tolerance to be 4 for non-pseudoknot bonds");

    }
}
