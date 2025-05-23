package it.unicam.cs.bdslab.triplematcher.models;
import org.junit.jupiter.api.Test;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.models.filters.FilterNotConsecutiveBond;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
class BasicFilterTest {

    @Test
    void filterReturnsMatchesWithinTolerance() {
        List<Character> text = Arrays.asList('A', 'C', 'G', 'T');
        List<Character> pattern = Arrays.asList('A', 'G');
        it.unicam.cs.bdslab.triplematcher.models.RNAPatternMatcher<Character> patternMatcher = mock(RNAPatternMatcher.class);
        when(patternMatcher.getAlignmentMatrix()).thenReturn(new int[][]{
                {0, 0, 0, 0, 0},
                {1, 0, 1, 2, 3},
                {2, 1, 0, 1, 2}
        });
        when(patternMatcher.traceback(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        BasicFilter<Character> filter = new BasicFilter<>(text, pattern);
        List<Match<Character>> matches = filter.filter(patternMatcher, 1, 1);

        assertEquals(3, matches.size());
    }

    @Test
    void filterReturnsEmptyListWhenNoMatchesWithinTolerance() {
        List<Character> text = Arrays.asList('A', 'C', 'G', 'T');
        List<Character> pattern = Arrays.asList('A', 'G');
        RNAPatternMatcher<Character> patternMatcher = mock(RNAPatternMatcher.class);
        when(patternMatcher.getAlignmentMatrix()).thenReturn(new int[][]{
                {0, 0, 0, 0, 0},
                {1, 2, 3, 4, 5},
                {2, 3, 4, 5, 6}
        });

        BasicFilter<Character> filter = new BasicFilter<>(text, pattern);
        List<Match<Character>> matches = filter.filter(patternMatcher, 1, 1);

        assertEquals(1, matches.size());
    }

    @Test
    void filterHandlesEmptyTextAndPattern() {
        List<Character> text = Collections.emptyList();
        List<Character> pattern = Collections.emptyList();
        RNAPatternMatcher<Character> patternMatcher = mock(RNAPatternMatcher.class);
        when(patternMatcher.getAlignmentMatrix()).thenReturn(new int[][]{
                {0}
        });

        BasicFilter<Character> filter = new BasicFilter<>(text, pattern);
        List<Match<Character>> matches = filter.filter(patternMatcher, 1, 1);

        assertTrue(matches.isEmpty());
    }

    @Test
    void filterHandlesZeroTolerance() {
        List<Character> text = Arrays.asList('A', 'C', 'G', 'T');
        List<Character> pattern = Arrays.asList('A', 'G');
        RNAPatternMatcher<Character> patternMatcher = mock(RNAPatternMatcher.class);
        when(patternMatcher.getAlignmentMatrix()).thenReturn(new int[][]{
                {0, 0, 0, 0, 0},
                {1, 0, 1, 2, 3},
                {2, 1, 0, 1, 2}
        });
        when(patternMatcher.traceback(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        BasicFilter<Character> filter = new BasicFilter<>(text, pattern);
        List<Match<Character>> matches = filter.filter(patternMatcher, 0, 1);

        assertEquals(1, matches.size());
    }

    @Test
    void filtersGetAllMatchesLessThenTolerance() throws NoSuchFieldException, IllegalAccessException {
        // ARRANGE
        List<Character> text = Arrays.asList('G', 'A', 'G', 'U');
        List<Character> pattern = Arrays.asList('G', 'G');
        RNAApproximatePatternMatcher<Character> characterRNAPatternMatcher = new RNAApproximatePatternMatcher<>(text);
        // matrix is
        // 0 0 0 0 0
        // 1 1 0 1 1
        // 2 1 1 1 1
        characterRNAPatternMatcher.solve(pattern);
        BasicFilter<Character> filter = new BasicFilter<>(text, pattern);
        // ACT
         List<Match<Character>> matches = filter.filter(characterRNAPatternMatcher, 1, 1);
        // ASSERT
        assertEquals(9, matches.size());
    }

    @Test
    void filtersGetAllMatchesLessThenTolerance0() throws NoSuchFieldException, IllegalAccessException {
        // ARRANGE
        List<Character> text = Arrays.asList('G', 'A', 'G', 'U');
        List<Character> pattern = Arrays.asList('G', 'G');
        RNAApproximatePatternMatcher<Character> characterRNAPatternMatcher = new RNAApproximatePatternMatcher<>(text);
        // matrix is
        // [0, 0, 0, 0, 0]
        // [1, 0, 1, 0, 1]
        // [2, 1, 1, 1, 1]

        characterRNAPatternMatcher.solve(pattern);
        BasicFilter<Character> filter = new BasicFilter<>(text, pattern);
        // ACT
        List<Match<Character>> matches = filter.filter(characterRNAPatternMatcher, 0, 1);
        // ASSERT
        assertEquals(2, matches.size());
    }

    @Test
    void consecutiveFilterTestOnNonConsecutiveBonds() {
        // ARRANGE
        List<EditOperation<CompleteWeakBond>> editOperations = Arrays.asList(
                new EditOperation<>(new CompleteWeakBond(1, 10, 'A', 'A', false), new CompleteWeakBond(1, 10, 'A', 'A', false)),
                new EditOperation<>(new CompleteWeakBond(2, 11, 'B', 'B', false), new CompleteWeakBond(2, 11, 'B', 'B', false)),
                new EditOperation<>(new CompleteWeakBond(3, 12, 'C', 'C', false), new CompleteWeakBond(3, 12, 'C', 'C', false)),
                new EditOperation<>(new CompleteWeakBond(6, 13, 'D', 'D', false), new CompleteWeakBond(6, 13, 'D', 'D', false))
        );
        Match<CompleteWeakBond> bond1 = new Match<>(1, 2, 0, new ArrayList<>(), new ArrayList<>(), editOperations);
        Match<Character> match = new Match<>(1, 2, 0, new ArrayList<>(), new ArrayList<>(), Collections.emptyList());
        Pair<Match<CompleteWeakBond>, Match<Character>> pair = new Pair<>(bond1, match);
        FilterNotConsecutiveBond filter = new FilterNotConsecutiveBond(1);
        // ACT
        boolean result = filter.test(new RNASecondaryStructure(), pair);
        // ASSERT
        assertFalse(result);
        assertEquals(2, bond1.getFilterTollerance());
    }

    @Test 
    void consecutiveFilterTestOnConsecutiveBonds() {
        // ARRANGE
        List<EditOperation<CompleteWeakBond>> editOperations = Arrays.asList(
                new EditOperation<>(new CompleteWeakBond(1, 10, 'A', 'A', false), new CompleteWeakBond(1, 10, 'A', 'A', false)),
                new EditOperation<>(new CompleteWeakBond(2, 11, 'B', 'B', false), new CompleteWeakBond(2, 11, 'B', 'B', false)),
                new EditOperation<>(new CompleteWeakBond(3, 12, 'C', 'C', false), new CompleteWeakBond(3, 12, 'C', 'C', false)),
                new EditOperation<>(new CompleteWeakBond(4, 13, 'D', 'D', false), new CompleteWeakBond(4, 13, 'D', 'D', false))
        );
        Match<CompleteWeakBond> bond1 = new Match<>(1, 2, 0, new ArrayList<>(), new ArrayList<>(), editOperations);
        Match<Character> match = new Match<>(1, 2, 0, new ArrayList<>(), new ArrayList<>(), Collections.emptyList());
        Pair<Match<CompleteWeakBond>, Match<Character>> pair = new Pair<>(bond1, match);
        FilterNotConsecutiveBond filter = new FilterNotConsecutiveBond(2);
        // ACT
        boolean result = filter.test(new RNASecondaryStructure(), pair);
        // ASSERT
        assertTrue(result);
        assertEquals(0, bond1.getFilterTollerance());
    } 
}
