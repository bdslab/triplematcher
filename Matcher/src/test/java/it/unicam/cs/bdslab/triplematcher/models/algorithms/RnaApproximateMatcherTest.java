package it.unicam.cs.bdslab.triplematcher.models.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructureFileReader;
import it.unicam.cs.bdslab.triplematcher.WeakBond;
import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import it.unicam.cs.bdslab.triplematcher.models.Match;
import it.unicam.cs.bdslab.triplematcher.models.utils.Pair;

public class RnaApproximateMatcherTest {
    /**
     * Test to ensure that without the findAllMatches flag set to false, the filter on PAN2_PKISS prediction
     * does not return a single match.
     */
    @Test 
    void findAllMatchesDisabledOnPAN2() throws IOException, URISyntaxException {
        // ARRANGE
        RNASecondaryStructure s = RNASecondaryStructureFileReader
            .readStructure(Paths.get(this.getClass().getResource("/PAN2_PKISS.db").toURI()).toString(), false);
        RNAApproximateMatcher matcher = new RNAApproximateMatcher(1,
            4,
            1,
            1, 
            1, 
            1, 
            10, 
            false
        );
        CompleteWeakBond bondPattern = new CompleteWeakBond(1, 2, 'U', 'A', false);
        Character seqPattern = 'U';
        
        // ACT
        List<Pair<Match<CompleteWeakBond>, Match<Character>>> result = matcher.match(s, bondPattern, seqPattern);

        // ASSERT
        assertEquals(0, result.size(), "The filter should not return any matches when findAllMatches is false.");
    }

    @Test 
    void findAllMatchesEnabledOnPAN2() throws IOException, URISyntaxException {
        // ARRANGE
        RNASecondaryStructure s = RNASecondaryStructureFileReader
            .readStructure(Paths.get(this.getClass().getResource("/PAN2_PKISS.db").toURI()).toString(), false);
        RNAApproximateMatcher matcher = new RNAApproximateMatcher(1,
            4,
            1,
            1, 
            1, 
            1, 
            10, 
            true
        );
        CompleteWeakBond bondPattern = new CompleteWeakBond(1, 2, 'U', 'A', false);
        Character seqPattern = 'U';
        
        // ACT
        List<Pair<Match<CompleteWeakBond>, Match<Character>>> result = matcher.match(s, bondPattern, seqPattern);

        // ASSERT
        assertNotEquals(0, result.size(), "The filter should not return any matches when findAllMatches is false.");
    }
}
