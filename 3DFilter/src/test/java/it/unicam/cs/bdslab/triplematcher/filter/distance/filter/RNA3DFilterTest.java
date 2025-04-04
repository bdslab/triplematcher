package it.unicam.cs.bdslab.triplematcher.filter.distance.filter;

import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import org.biojava.nbio.structure.Structure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RNA3DFilterTest {


    @Test
    public void testFilterOn4plx() {
        CSVRow row = new CSVRow.Builder()
                .setAccessionNumber("4plx")
                .setRNAType("RNA")
                .setFullSeq("GAAGGUUUUUCUUUUCCUGAGAAAACAACACGUAUUGUUUUCUCAGGUUUUGCUUUUUGGCCUUUUUCUAGCUUAAAAAAAAAAAAAGCAAAA")
                .setBondWindowStart("(10;66)")
                .setBondWindowEnd("(10;76)")
                .setSeqWindowStart(7)
                .setSeqWindowEnd(17)
                .build();
        RNA3DFilter filter = new RNA3DFilter(1.0);

        assertTrue(filter.filter(row));
    }

    @Test
    public void testCSVRowIsWellFormatted() {
        CSVRow row = new CSVRow.Builder()
                .setAccessionNumber("4plx")
                .setRNAType("RNA")
                .setFullSeq("GAAGGUUUUUCUUUUCCUGAGAAAACAACACGUAUUGUUUUCUCAGGUUUUGCUUUUUGGCCUUUUUCUAGCUUAAAAAAAAAAAAAGCAAAA")
                .setBondWindowStart("(10;66)")
                .setBondWindowEnd("(10;76)")
                .setSeqWindowStart(7)
                .setSeqWindowEnd(17)
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
        RNA3DFilter filter = new RNA3DFilter(1.0);
        filter.filter(row);
        assertEquals(CSVRow.HEADERSARRAY.length, row.getCsv().split(",").length, "CSVRow is not well formatted, it should have " + CSVRow.HEADERS + " columns" + " but it has " + Arrays.toString(row.getCsv().split(",")));
    }


}