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

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Class for transforming ANTLR 4 Syntax Errors into
 * RNAInputFileParserExceptions.
 * 
 * @author Luca Tesei
 *
 */
public class RNASecondaryStructureFileReaderErrorListener
	extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
	    Object offendingSymbol, int line, int charPositionInLine,
	    String msg, RecognitionException e) {
	String m = "Line " + line + " Character " + (charPositionInLine + 1)
		+ ": " + msg;
	throw new RNAInputFileParserException(m);
    }

}
