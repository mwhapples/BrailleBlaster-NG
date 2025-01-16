/*
 * Copyright (C) 2025 American Printing House for the Blind
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.brailleblaster.utd.formatting;

import org.brailleblaster.utd.UTDTranslationEngine;
import org.brailleblaster.utd.properties.UTDElements;
import org.brailleblaster.utd.testutils.UTDDocumentAsserter;
import org.brailleblaster.utd.testutils.UTDTestUtils;
import org.testng.annotations.Test;

import nu.xom.Document;

public class FormattingTest {
	private static final String BODY_TEXT_STYLE = " linesBefore='1' firstLineIndent='2' lineIndent='0'";
	private static final String HEADING_STYLE = " linesBefore='2' linesAfter='2' align='CENTERED'";
	
	@Test
	public void basicFormattingTest(){
		Document doc = UTDTestUtils.generateBookDoc("",
				"<p" + HEADING_STYLE + ">Heading</p>" +
				"<p" + BODY_TEXT_STYLE + ">Test 1</p>" +
				"<p" + BODY_TEXT_STYLE + ">Test 2</p>");
		UTDTranslationEngine engine = UTDTestUtils.translateAndFormat(doc);
		new UTDDocumentAsserter(doc, engine)
			.hasTextAt("Heading", 16, 0)
			.hasTextAt("Test 1", 2, 2)
			.hasTextAt("Test 2", 2, 3)
			.hasNoOtherMoveTo();
	}
	
	@Test
	public void basicLineWrapTest(){
		Document doc = UTDTestUtils.generateBookDoc("",
				"<p" + BODY_TEXT_STYLE + ">This sentence is exactly 44 characters long.</p>");
		UTDTranslationEngine engine = UTDTestUtils.translateAndFormat(doc);
		new UTDDocumentAsserter(doc, engine)
			.hasTextAt("This sentence is exactly 44 characters ", 2, 0)
			.hasTextAt("long.", 0, 1)
			.hasNoOtherMoveTo();
	}
	
	@Test
	public void longWordLineWrapTest(){
		Document doc = UTDTestUtils.generateBookDoc("",
				UTDTestUtils.elementToString(UTDElements.NEW_LINE, "") +
				"<p>123456789012345678901234567890123456789012345</p>");
		UTDTranslationEngine engine = UTDTestUtils.translateAndFormat(doc);
		new UTDDocumentAsserter(doc, engine)
			.hasTextAt("1234567890123456789012345678901234567890", 0, 1)
			.hasTextAt("12345", 0, 2)
			.hasNoOtherMoveTo();
	}
	
	@Test
	public void braillePageNumberTest(){
		Document doc = UTDTestUtils.generateBookDoc("",
				"<p>Test</p>");
		UTDTranslationEngine engine = UTDTestUtils.translateAndFormat(doc);
		new UTDDocumentAsserter(doc, engine)
			.hasTextAt("Test", 0, 0)
			.hasBraillePageNumber("#a")
			.hasNoOtherMoveTo();
	}
}
