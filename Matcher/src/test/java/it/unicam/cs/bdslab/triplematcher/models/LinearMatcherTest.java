package it.unicam.cs.bdslab.triplematcher.models;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinearMatcherTest {

    @Test
    void solve_returnsCorrectMatches_whenPatternIsFound() {
        LinearMatcher<Character> matcher = new LinearMatcher<>(2, 5);
        RNASecondaryStructure rna = new RNASecondaryStructure();
        List<Character> text = Arrays.asList('A', 'A', 'A', 'C', 'A', 'A', 'A', 'A');
        Character pattern = 'A';

        List<Match<Character>> matches = matcher.solve(rna, text, pattern);

        assertEquals(1, matches.size());
        assertEquals(7, matches.get(0).getRow());
        assertEquals(8, matches.get(0).getCol());
    }

    @Test
    void solve_returnsEmptyList_whenPatternIsNotFound() {
        LinearMatcher<Character> matcher = new LinearMatcher<>(2, 3);
        RNASecondaryStructure rna = new RNASecondaryStructure();
        List<Character> text = Arrays.asList('A', 'C', 'G', 'U');
        Character pattern = 'T';

        List<Match<Character>> matches = matcher.solve(rna, text, pattern);

        assertTrue(matches.isEmpty());
    }

    @Test
    void solve_returnsEmptyList_whenTextIsEmpty() {
        LinearMatcher<Character> matcher = new LinearMatcher<>(2, 3);
        RNASecondaryStructure rna = new RNASecondaryStructure();
        List<Character> text = new ArrayList<>();
        Character pattern = 'A';

        List<Match<Character>> matches = matcher.solve(rna, text, pattern);

        assertTrue(matches.isEmpty());
    }

    @Test
    void solve_returnsEmptyList_whenPatternIsEmpty() {
        LinearMatcher<Character> matcher = new LinearMatcher<>(2, 0);
        RNASecondaryStructure rna = new RNASecondaryStructure();
        List<Character> text = Arrays.asList('A', 'A', 'A', 'C', 'A', 'A', 'A', 'A');
        Character pattern = 'A';

        List<Match<Character>> matches = matcher.solve(rna, text, pattern);

        assertTrue(matches.isEmpty());
    }

    @Test
    void solve_returnsCorrectMatches_whenToleranceIs0() {
        LinearMatcher<Character> matcher = new LinearMatcher<>(0, 1);
        RNASecondaryStructure rna = new RNASecondaryStructure();
        List<Character> text = Arrays.asList('A', 'A', 'A', 'C', 'A', 'A', 'A', 'A');
        Character pattern = 'A';

        List<Match<Character>> matches = matcher.solve(rna, text, pattern);

        assertEquals(2, matches.size());
        assertEquals(3, matches.get(0).getRow());
        assertEquals(3, matches.get(0).getCol());
        assertEquals(4, matches.get(1).getRow());
        assertEquals(8, matches.get(1).getCol());
    }

    @Test
    void solve_checkDistance() {
        // ARRANGE
        LinearMatcher<Character> matcher = new LinearMatcher<>(2, 0);
        RNASecondaryStructure rna = new RNASecondaryStructure();
        List<Character> text = Arrays.asList('A', 'A', 'A', 'A', 'A', 'A','C', 'A', 'A', 'A', 'A');
        Character pattern = 'A';
        // ACT
        List<Match<Character>> matches = matcher.solve(rna, text, pattern);
        // ASSERT
        assertEquals(2, matches.size());
        assertEquals(1, matches.get(0).getDistance());
        assertEquals(text.size(), matches.get(0).getLength());
    }
}