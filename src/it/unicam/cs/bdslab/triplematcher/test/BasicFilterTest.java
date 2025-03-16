package it.unicam.cs.bdslab.triplematcher.test;
import it.unicam.cs.bdslab.triplematcher.models.BasicFilter;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.RNAApproximatePatternMatcher;
import it.unicam.cs.bdslab.triplematcher.models.RNAPatternMatcher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
class BasicFilterTest {

    @Test
    void filterReturnsMatchesWithinTolerance() {
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
        List<Match<Character>> matches = filter.filter(patternMatcher, 1, 1);

        assertEquals(1, matches.size());
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
        // 0 0 0 0 0
        // 1 1 0 1 1
        // 2 1 1 1 1
        characterRNAPatternMatcher.solve(pattern);
        BasicFilter<Character> filter = new BasicFilter<>(text, pattern);
        // ACT
        List<Match<Character>> matches = filter.filter(characterRNAPatternMatcher, 0, 1);
        // ASSERT
        assertEquals(1, matches.size());
    }
}
