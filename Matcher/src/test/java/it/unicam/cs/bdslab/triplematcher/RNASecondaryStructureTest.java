/**
 * SERNAlign - Structural sEquence RNA secondary structure Alignment
 * 
 * Copyright (C) 2023 Luca Tesei, Francesca Levi, Michela Quadrini, 
 * Emanuela Merelli - BioShape and Data Science Lab at the University of 
 * Camerino, Italy - http://www.emanuelamerelli.eu/bigdata/
 *  
 * This file is part of SERNAlign.
 * 
 * SERNAlign is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * SERNAlign is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SERNAlign. If not, see <http://www.gnu.org/licenses/>.
 */
package it.unicam.cs.bdslab.triplematcher;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import it.unicam.cs.bdslab.triplematcher.models.CompleteWeakBond;
import org.junit.jupiter.api.Test;

/**
 * Tests for the class RNASecondaryStructure
 * 
 * @author Luca Tesei
 *
 */
class RNASecondaryStructureTest {

    @Test
    void testIsPseudoknotted() throws IOException, URISyntaxException {
		RNASecondaryStructure s1 = RNASecondaryStructureFileReader
			.readStructure(Paths.get(this.getClass().getResource("/pseuduoknot.db").toURI()).toString(), false);
		assertTrue(s1.isPseudoknotted());
    }
    @Test
    void testIsNotPseudoknotted() throws IOException, URISyntaxException {
        RNASecondaryStructure s1 = RNASecondaryStructureFileReader
            .readStructure(Paths.get(this.getClass().getResource("/notPseudoknot.db").toURI()).toString(), false);
        assertFalse(s1.isPseudoknotted(), "Expected structure to not be pseudoknotted");
    }

    @Test
    void testHasCrossingBonds() throws IOException, URISyntaxException {
        RNASecondaryStructure s1 = RNASecondaryStructureFileReader
            .readStructure(Paths.get(this.getClass().getResource("/pseuduoknot.db").toURI()).toString(), false);
        assertTrue(s1.getBonds().stream().anyMatch(CompleteWeakBond::isCross), "Expected structure to have crossing bonds");
    }

    @Test
    void testHasNoCrossingBonds() throws IOException, URISyntaxException {
        RNASecondaryStructure s1 = RNASecondaryStructureFileReader
            .readStructure(Paths.get(this.getClass().getResource("/notPseudoknot.db").toURI()).toString(), false);
        assertFalse(s1.getBonds().stream().anyMatch(CompleteWeakBond::isCross), "Expected structure to not have crossing bonds");
    }

    @Test
    void testIndeces() throws IOException, URISyntaxException {
        RNASecondaryStructure s1 = RNASecondaryStructureFileReader
            .readStructure(Paths.get(this.getClass().getResource("/testIndexes.db").toURI()).toString(), false);
        assertEquals(1, s1.getBonds().get(0).getLeft(), "Expected first bond to start at index 0");
        assertEquals(3, s1.getBonds().get(0).getRight(), "Expected first bond to end at index 2");
    }


}
