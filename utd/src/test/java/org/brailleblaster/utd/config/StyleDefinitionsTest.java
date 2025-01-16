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
package org.brailleblaster.utd.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.brailleblaster.utd.IStyle;
import org.brailleblaster.utd.Style;
import org.brailleblaster.utd.properties.Align;
import org.brailleblaster.utd.properties.PageNumberType;
import org.brailleblaster.utd.testutils.UTDConfigUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class StyleDefinitionsTest {
	public static final String DEFS_TEST_PATH = "/org/brailleblaster/utd/cli/styledefs.xml";

	/**
	 * Test that the defaults we changed were persisted
	 */
	@Test
	public void loadTest() {
		StyleDefinitions defs = UTDConfig.loadStyleDefinitions(UTDConfigUtils.TEST_STYLEDEFS_FILE);
		Style style = defs.getStyleByName("test");
		Style testStyle = style;
		IStyle defaultStyle = defs.getDefaultStyle();
		assertNotNull(style, "Style test doesn't exist!");
		assertSame(style.getBaseStyle(), defaultStyle);
		assertTrue(style.isDontSplit(), "dontSplit default not changed");
		assertEquals(style.getLinesAfter(), 55, "linesAfter default not changed");
		assertEquals(style.getBraillePageNumberFormat(), PageNumberType.T_PAGE,
				"Default braille page number format didn't propagate");
		assertEquals(defs.getStyles().indexOf(style), 1, "test style in wrong position");

		style = defs.getStyleByName("test2");
		assertSame(style.getBaseStyle(), defaultStyle);
		assertEquals(style.getAlign(), Align.RIGHT, "Default align didn't propagate");
		assertEquals(style.getBraillePageNumberFormat(), PageNumberType.P_PAGE,
				"Page number format default not overridden in child style");
		
		style = defs.getStyleByName("test3");
		assertSame(style.getBaseStyle(), testStyle);
		assertEquals(style.getAlign(), Align.CENTERED);
	}

	@Test
	public void saveTest() throws IOException {
		StyleDefinitions defs = new StyleDefinitions();

		Style defaultStyle = new Style();
		defaultStyle.setId(StyleDefinitions.DEFAULT_STYLE);
		defaultStyle.setName(StyleDefinitions.DEFAULT_STYLE);
		defaultStyle.setLineSpacing(3);
		assertNotEquals(defaultStyle.getAlign(), Align.RIGHT, "Defaults changed, must update tests");
		defaultStyle.setAlign(Align.RIGHT);
		defaultStyle.setBraillePageNumberFormat(PageNumberType.T_PAGE);
		defs.addStyle(defaultStyle);

		Style style = new Style(defaultStyle, "test/test", "test");
		style.setDontSplit(true);
		style.setLinesAfter(55);
		defs.addStyle(style);

		Style style2 = new Style(defaultStyle, "test/test2", "test2");
		style2.setKeepWithNext(true);
		style2.setIndent(5);
		style2.setBraillePageNumberFormat(PageNumberType.P_PAGE);
		style2.setEndSeparator("d");
		defs.addStyle(style2);
		
		Style style3 = new Style(style, "test/test3", "test3");
		style3.setAlign(Align.CENTERED);
		defs.addStyle(style3);

		File tempFile = File.createTempFile("styleDefsTest", "test");
		UTDConfig.saveStyleDefinitions(tempFile, defs);
		UTDConfigUtils.compareOutputToSaved(tempFile, UTDConfigUtils.TEST_SAVE_STYLEDEFS_FILE);
	}

	@Test
	public void loadBadTest() {
		File badFile = new File(UTDConfigUtils.TEST_FOLDER, "styleDefs.bad.xml");
		try {
			UTDConfig.loadStyleDefinitions(badFile);
		} catch (Exception e) {
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			assertTrue(rootCause.getMessage().contains("orphanControl"), 
					"Unexpected exception: " + ExceptionUtils.getStackTrace(e));
			return;
		}
		throw new AssertionError("File styleDefs.bad.xml shouldn't of parsed sucesfully");
	}
}
