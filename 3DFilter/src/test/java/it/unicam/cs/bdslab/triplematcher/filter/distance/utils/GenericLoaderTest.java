package it.unicam.cs.bdslab.triplematcher.filter.distance.utils;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GenericLoaderTest {

    private static GenericFileLoader fileLoaderWithPath;
    private static GenericFileLoader fileLoaderWithNull;

    @BeforeAll
    public static void setUp() throws URISyntaxException {
        fileLoaderWithPath = new GenericFileLoader(
                Paths.get(GenericLoaderTest.class.getResource("/PDB_Examples").toURI())
        );
        fileLoaderWithNull = new GenericFileLoader(null);
    }

    @Test
    public void testLoadIfDirectoryIsNull() throws StructureException, IOException {
        Structure structure = fileLoaderWithNull.getStructure("4plx");
        assertNotNull(structure, "Structure should not be null");
        assertEquals("4PLX", structure.getPdbId().getId(), "Structure ID should be 4plx");
    }

    @Test
    public void testLoadFilesMALAT1() throws StructureException, IOException {
        Structure structure = fileLoaderWithPath.getStructure("MALAT1");
        assertNotNull(structure, "Structure should not be null");
        assertEquals("", structure.getName(), "Structure ID should be null because the file is custom");
    }

    @Test
    public void testNotLoadMALAT1() {
        assertThrows(StructureException.class, () -> fileLoaderWithNull.getStructure("MALAT1"));
    }

    @Test
    public void testLoadChainMalat1() throws StructureException, IOException {
        Structure structure = fileLoaderWithPath.getStructure("MALAT1");
        assertNotNull(structure, "Structure should not be null");
        assertEquals(1, structure.getChains().size(), "Structure chain should contain 1 chain");
    }

    @Test
    public void testLoadPan() throws StructureException, IOException {
        Structure structure = fileLoaderWithPath.getStructure("PAN");
        assertNotNull(structure, "Structure should not be null");

    }

    @Test
    public void testLoad4plx() throws StructureException, IOException {
        Structure structure = fileLoaderWithPath.getStructure("4plx");
        assertNotNull(structure, "Structure should not be null");
    }
}