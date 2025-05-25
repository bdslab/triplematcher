package it.unicam.cs.bdslab.triplematcher.filter.distance;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.CSVRow;
import it.unicam.cs.bdslab.triplematcher.filter.distance.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ParserTest {
    @Test
    public void testParser() throws URISyntaxException {
        Parser parser = new Parser();

        Path path = Paths.get(this.getClass().getResource("/complete.csv").toURI());
        List<CSVRow> rows = parser.parse(path);
        Assertions.assertEquals(115, rows.size());
    }
}
