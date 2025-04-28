package it.unicam.cs.bdslab.triplematcher.filter.distance.utils;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GenericLoaderTest {

    @Test
    public void testLoadIfDirectoryIsNull() throws StructureException, IOException {
        GenericFileLoader fileLoader = new GenericFileLoader(null);
        Structure structure = fileLoader.getStructure("4plx");
        assertNotNull(structure, "Structure should not be null");
        assertEquals("4PLX", structure.getPdbId().getId(), "Structure ID should be 4plx");
    }

    @Test
    public void testLoadFilesMALAT1() throws StructureException, IOException, URISyntaxException {
        GenericFileLoader fileLoader = new GenericFileLoader(
                Paths.get(getClass().getResource("/PDB_Examples").toURI())
        );
        Structure structure = fileLoader.getStructure("MALAT1");
        assertNotNull(structure, "Structure should not be null");
        assertEquals("", structure.getName(), "Structure ID should be null because the file is custom");
    }

    @Test
    public void testNotLoadMALAT1() throws StructureException, IOException, URISyntaxException {
        GenericFileLoader fileLoader = new GenericFileLoader(null);
        assertThrows(StructureException.class, () -> fileLoader.getStructure("MALAT1"));
    }

    @Test
    public void testLoadChainMalat1() throws StructureException, IOException, URISyntaxException {
        GenericFileLoader fileLoader = new GenericFileLoader(
                Paths.get(getClass().getResource("/PDB_Examples").toURI())
        );
        Structure structure = fileLoader.getStructure("MALAT1");
        assertNotNull(structure, "Structure should not be null");
        assertEquals(1, structure.getChains().size(), "Structure chain should contain 1 chain");
    }


}
