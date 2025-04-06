package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BioJavaTest {

    @Test
    public void testBioJava() throws StructureException, IOException {
        Structure structure = StructureIO.getStructure("4plx");
        assert structure != null;
        System.out.println(structure);
    }
}
