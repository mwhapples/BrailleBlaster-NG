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
package org.brailleblaster.utd;

import org.apache.commons.lang3.StringUtils;
import org.brailleblaster.utd.utils.UTDHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BRFWriterSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(BRFWriterSimpleTest.class);
    private BRFWriter brfWriter;
    private StringBuilder brfOutput;

    @BeforeMethod
    public void testInit() {
        brfOutput = new StringBuilder();
        UTDTranslationEngine engine = new UTDTranslationEngine();
        engine.getBrailleSettings().setUseAsciiBraille(true);
        brfWriter = new BRFWriter(engine, brfOutput::append, BRFWriter.OPTS_DEFAULT, BRFWriter.EMPTY_PAGE_LISTENER);
    }

    @DataProvider
    public Object[][] appendEOLDataProvider() {
        return new Object[][]{
                new Object[]{' '},
                new Object[]{UTDHelper.BRAILLE_SPACE},};
    }

    @Test(dataProvider = "appendEOLDataProvider")
    public void appendEOL(char spaceChar) {
        log.debug("Init");
        brfWriter.newPage(1);
        //This shouldn't throw an exception
        brfWriter.append("This" + StringUtils.repeat(spaceChar, 50));
        brfWriter.moveTo(0, 1);
        brfWriter.append("After");

        brfWriter.onEndOfFile();
        String output = brfOutput.toString();

        assertEquals(output.substring(0, 10), "This\nAfter", "START" + output + "END");
    }

    @Test(dataProvider = "appendEOLDataProvider")
    public void appendLongLineMultiSteps(char spaceChar) {
        log.debug("Init");
        brfWriter.newPage(1);
        //This shouldn't throw an exception
        brfWriter.append("This" + StringUtils.repeat(spaceChar, 50));
        // BRFWriter should not line wrap or strip the spacing as it is not trailing as the UTD contains occupied cells either side of the space
        // Appending in multiple calls should be the same as appending in one.
        // This is where the exception is expected
        try {
            brfWriter.append("After");
        } catch (BRFWriter.BRFOutputException e) {
            // success we got the expected exception when we wanted.
            return;
        }
        fail("No exception when attempting to write too much text to one line");
    }

    @Test(dataProvider = "appendEOLDataProvider")
    public void ignoreTrailingSpacesBeforeMoveTo(char spaceChar) {
        log.debug("Init");
        brfWriter.newPage(1);
        //This shouldn't throw an exception
        brfWriter.append("This" + StringUtils.repeat(spaceChar, 5));
        brfWriter.moveTo(6, 0);
        brfWriter.append("After");

        brfWriter.onEndOfFile();
        String output = brfOutput.toString();

        assertEquals(output.substring(0, 11), "This  After", "START" + output + "END");
    }
}
