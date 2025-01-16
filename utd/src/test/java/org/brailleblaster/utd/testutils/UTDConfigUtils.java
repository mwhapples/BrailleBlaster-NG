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
package org.brailleblaster.utd.testutils;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

public final class UTDConfigUtils {
    private UTDConfigUtils() {
    }

    public static final File TEST_FOLDER = new File("src/test/resources/org/brailleblaster/utd/testutils");
    public static final File TEST_ACTION_FILE = new File(TEST_FOLDER, "test.actionMap.xml");
    public static final File TEST_STYLE_FILE = new File(TEST_FOLDER, "test.styleMap.xml");
    public static final File TEST_STYLEDEFS_FILE = new File(TEST_FOLDER, "styleDefs.xml");
    public static final File TEST_SAVE_STYLEDEFS_FILE = new File(TEST_FOLDER, "styleDefs_save.xml");
    public static final File TEST_PAGE_SETTINGS_FILE = new File(TEST_FOLDER, "pageSettings.xml");
    public static final File TEST_BRAILLE_SETTINGS_FILE = new File(TEST_FOLDER, "brailleSettings.xml");

    public static String input2String(InputStream input) {
        Scanner tmpScanner = new Scanner(input, Charset.defaultCharset());
        String savedOutput = tmpScanner.useDelimiter("\\Z").next().trim();
        tmpScanner.close();
        return savedOutput;
    }

    public static String normalizeEndOfLine(String rawString) {
        String normalString = rawString;
        normalString = StringUtils.replace(normalString, "\r\n", "\n");
        normalString = StringUtils.replace(normalString, "\r", "\n");
        normalString = normalString.trim();
        return normalString;
    }

    public static void compareOutputToSaved(File generatedFile, File savedFile) {
        try {
            String saved = input2String(new FileInputStream(savedFile));
            saved = normalizeEndOfLine(saved);

            String generated = input2String(new FileInputStream(generatedFile));
            generated = normalizeEndOfLine(generated);

            Assert.assertEquals(generated, saved, "Given output does not match expected saved output");
        } catch (Exception e) {
            throw new RuntimeException("Generated " + generatedFile + " saved " + savedFile, e);
        }
    }
}
