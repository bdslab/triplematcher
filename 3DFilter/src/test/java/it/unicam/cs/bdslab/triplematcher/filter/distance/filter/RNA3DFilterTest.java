package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Direction;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.DistanceInfo;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.GenericFileLoader;
import it.unicam.cs.bdslab.triplematcher.filter.distance.utils.Triple;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RNA3DFilterTest {
    private static GenericFileLoader fileLoader;

    @BeforeAll
    public static void setUp() throws URISyntaxException, StructureException, IOException {
        fileLoader = new GenericFileLoader(
                Paths.get(RNA3DFilterTest.class.getResource("/PDB_Examples").toURI())
        );
    }

    @Test
    public void testFilterOn4plx() throws StructureException, IOException {
        CSVRow row = new CSVRow.Builder()
                .setAccessionNumber("4plx")
                .setRNAType("RNA")
                .setFullSeq("GAAGGUUUUUCUUUUCCUGAGAAAACAACACGUAUUGUUUUCUCAGGUUUUGCUUUUUGGCCUUUUUCUAGCUUAAAAAAAAAAAAAGCAAAA")
                .setBondIndexes("(36,75);(37,74);(38,73);(39,72);(40,71);(41,70);(42,69);(43,68);(44,67);(45,66);(46,65)")
                .setSeqIndexes("7;8;9;10;11;12;13;14;15;16")
                .build();
        RNA3DFilter filter = new RNA3DFilter(1.0, fileLoader.getStructure("4plx"));

        assertTrue(filter.filter(row));
    }

    @Test
    public void testCSVRowIsWellFormatted() throws StructureException, IOException {
        CSVRow row = new CSVRow.Builder()
                .setAccessionNumber("4plx")
                .setRNAType("RNA")
                .setFullSeq("GAAGGUUUUUCUUUUCCUGAGAAAACAACACGUAUUGUUUUCUCAGGUUUUGCUUUUUGGCCUUUUUCUAGCUUAAAAAAAAAAAAAGCAAAA")
                .setBondIndexes("(36,75);(37,74);(38,73);(39,72);(40,71);(41,70);(42,69);(43,68);(44,67);(45,66);(46,65)")
                .setSeqIndexes("7;8;9;10;11;12;13;14;15;16")
                .setRNAKey("4plx")
                .setBondTolerance(1)
                .setBondCustomMatchString("MMMM")
                .setSeqCustomMatchString("MMMM")
                .setScoreBond(1)
                .setScoreSeq(1)
                .setSeqTolerance(1)
                .setBondTolerance(1)
                .setNotPairedTolerance(1)
                .build();
        row.setMeanDirection(Direction.LEFT_TO_RIGHT_FIRST_BOND);
        row.setDistanceInfo(new java.util.ArrayList<>());
        row.setMeanAngstroms(1.0);
        RNA3DFilter filter = new RNA3DFilter(1.0, fileLoader.getStructure("4plx"));
        filter.filter(row);
        assertNotNull(row.getCsv(), "CSVRow is null");
        CSVParser parser = CSVParser.parse(row.getCsv(), CSVFormat.DEFAULT);
        assertEquals(CSVRow.HEADERSARRAY.length, parser.getRecords().stream().findFirst().get().size(), "CSVRow is not well formatted, it should have " + CSVRow.HEADERS + " columns" + " but it has " + Arrays.toString(row.getCsv().split(",")));
    }

    // Test for the mean calculation
    @Test
    public void testFilterWithPredefinedDistanceMatrix() {
        double[][] predefinedMatrix = {//    0
            /*0*/ {0.0}, // 1
            /*1*/ {1.0, 0.0}, // 2
            /*2*/ {2.0, 1.5, 0.0}, // 3
            /*3*/ {3.0, 2.5, 1.0, 0.0}, // 4
            /*4*/ {4.0, 3.5, 2.0, 1.5, 0.0}, // 5
            /*5*/ {5.0, 4.5, 3.0, 2.5, 1.0, 0.0}, // 6
            /*6*/ {6.0, 5.5, 4.0, 3.5, 2.0, 1.5, 0.0}, // 7
            /*7*/ {7.0, 6.5, 5.0, 4.5, 3.0, 2.5, 1.0, 0.0}, // 8
            /*8*/ {8.0, 7.5, 6.0, 5.5, 4.0, 3.5, 2.0, 1.5, 0.0}, // 9
            /*9*/ {9.0, 8.5, 7.0, 6.5, 5.0, 4.5, 3.0, 2.5, 1.0, 0.0} // 10
        };

        RNA3DFilter filter = new RNA3DFilter(predefinedMatrix, 1.0);

        CSVRow row = new CSVRow.Builder()
                .setAccessionNumber("test")
                .setRNAType("RNA")
                .setFullSeq("ACGUACGU")
                .setBondIndexes("(2,8);(3,7);(4,7)")
                .setSeqIndexes("1;2;3")
                .build();

        // Esecuzione del metodo da testare
        boolean result = filter.filter(row);
        // Calcolo della media
        double expectedMean = (predefinedMatrix[2][1]
                + predefinedMatrix[3][2]
                + predefinedMatrix[4][3]
        ) / 3.0;
        // Verifica del risultato
        assertTrue(result, "Il filtro dovrebbe restituire true per i dati forniti.");
        assertEquals(expectedMean, row.getMeanAngstroms(), 0.001, "La media calcolata non è corretta.");
        assertEquals(Direction.LEFT_TO_RIGHT_FIRST_BOND, row.getMeanDirection(), "La direzione calcolata non è corretta.");
    }

}