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
package org.brailleblaster.utd.utils;

import org.brailleblaster.utd.UTDTranslationEngine;
import org.mwhapples.jlouis.Louis.TranslationModes;
import org.mwhapples.jlouis.TranslationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class BrailleUnicodeConverterTest {
    private static final Logger log = LoggerFactory.getLogger(BrailleUnicodeConverterTest.class);
    /**
     * Test with random character that isn't in the tables and so will translate as '\x308A'
     */
    final String TEST_STRING = "\\ word \u308A testing | this is a test";


    @Test
    public void convertTest() throws Exception {
        UTDTranslationEngine engine = new UTDTranslationEngine();

        engine.getBrailleSettings().setUseLibLouisAPH(false);
        TranslationResult louisResultAscii = engine.getBrailleTranslator().translate(
                engine.getBrailleSettings().getMainTranslationTable(),
                TEST_STRING,
                0,
                0
        );
        TranslationResult louisResultUnicode = engine.getBrailleTranslator().translate(
                engine.getBrailleSettings().getMainTranslationTable(),
                TEST_STRING,
                0,
                TranslationModes.DOTS_IO | TranslationModes.UC_BRL
        );


        log.debug("Ascii " + louisResultAscii.getTranslation());
        log.debug("Unicode " + louisResultUnicode.getTranslation());

        log.debug("Ascii " + Character.getName(louisResultAscii.getTranslation().charAt(2)));
        log.debug("Unicode " + Character.getName(louisResultUnicode.getTranslation().charAt(2)));

//		Assert.assertEquals(
        assertEqualsDetails(
                BrailleUnicodeConverter.INSTANCE.unicodeToAsciiLouis(louisResultUnicode.getTranslation()),
                louisResultAscii.getTranslation(),
                "unicode-to-ascii doesn't match liblouis ascii"
        );
//		Assert.assertEquals(
        assertEqualsDetails(
                BrailleUnicodeConverter.INSTANCE.asciiToUnicodeLouis(louisResultAscii.getTranslation()),
                louisResultUnicode.getTranslation(),
                "ascii-to-unicode doesn't match liblouis unicode"
        );
    }

    public static String compareStringDetails(String given, String expected) {
        String error = null;
        for (int i = 0; i < Math.max(given.length(), expected.length()); i++) {
            char givenChar;
            if (i < given.length()) {
                givenChar = given.charAt(i);
            } else {
                error = "given ends at " + i + " while expected ends at " + expected.length();
                break;
            }

            char expectedChar;
            if (i < expected.length()) {
                expectedChar = expected.charAt(i);
            } else {
                error = "expected ends at " + i + " while given ends at " + given.length();
                break;
            }

            if (givenChar != expectedChar) {
                error = "char " + i + " given '" + givenChar + "'" + Character.getName(givenChar)
                        + "  expected '" + expectedChar + "' " + Character.getName(expectedChar);
                break;
            }
        }
        return error;
    }

    public static void assertEqualsDetails(String given, String expected, String reason) {
        if (!given.equals(expected)) {
            throw new AssertionError("\n"
                    + "Expected: " + expected
                    + System.lineSeparator()
                    + "Given   : " + given
                    + System.lineSeparator()
                    + "Reason: " + compareStringDetails(given, expected)
                    + System.lineSeparator()
                    + reason
            );
        }
    }
}
