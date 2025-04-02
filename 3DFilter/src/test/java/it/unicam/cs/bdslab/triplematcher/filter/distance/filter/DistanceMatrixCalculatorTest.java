package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistanceMatrixCalculatorTest {

    @Test
    public void testDistanceMatrixOn4plx() throws StructureException, IOException {
        String sequence = "GAAGGUUUUUCUUUUCCUGAGAAAACAACACGUAUUGUUUUCUCAGGUUUUGCUUUUUGGCCUUUUUCUAGCUUAAAAAAAAAAAAAGCAAAA";
        DistanceMatrixCalculator calculator = new DistanceMatrixCalculator(
                StructureIO.getStructure("4plx"),
                "RNA",
                sequence
        );
        double[][] matrix = calculator.getDistanceMatrix();
        assertEquals(1, matrix[0].length);
        assertTrue(calculator.getChain().isNucleicAcid());
        assertTrue(calculator.getChain().getSeqResSequence().contains("UUUUUCUUUU"));
    }


}
