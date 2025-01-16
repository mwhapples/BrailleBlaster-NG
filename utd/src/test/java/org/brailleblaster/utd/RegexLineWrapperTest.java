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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.brailleblaster.utd.exceptions.NoLineBreakPointException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RegexLineWrapperTest {

    @DataProvider(name = "nemethBreakPointProvider")
    public Object[][] nemethBreakPointProvider() {
        LineWrapper basicLineWrapper = new RegexLineWrapper();
        RegexLineWrapper defaultWrapper = new RegexLineWrapper();
        RegexLineWrapper nemethWrapper = new RegexLineWrapper(RegexLineWrapper.NEMETH_BREAK_POINTS);
        RegexLineWrapper uebMathWrapper = new RegexLineWrapper(RegexLineWrapper.UEB_MATH_BREAK_POINTS);
        return new Object[][]{
                {basicLineWrapper, "y+r-t .k 2x+4ac", 6, 6},
                {defaultWrapper, "y .k 2x+3", 5, 5},
                {defaultWrapper, "x+c .k y", 6, 4},
                {defaultWrapper, "y .k 2x+4ac-bx", 10, 5},
                {defaultWrapper, "y+r-t .k 2x+4ac", 6, 6},
                {defaultWrapper, "ab", 10, 2},
                {nemethWrapper, "y .k 2x+3", 5, 2},
                {nemethWrapper, "x+c .k y", 6, 4},
                {nemethWrapper, "y .k 2x+4ac-bx", 10, 2},
                {nemethWrapper, "y+r-t .k 2x+4ac", 5, 3},
                {nemethWrapper, "ab", 10, 2},
                {uebMathWrapper, "y \"7#fx \"6#d;ac", 10, 2},
                {uebMathWrapper, "y \"6#fx \"-#e;a \"7#j", 11, 8},
        };
    }

    @Test(dataProvider = "nemethBreakPointProvider")
    public void findNextBreakPoint(LineWrapper wrapper, String inStr, Integer lineLength, Integer expectedBreak) {
        int bp = 0;
        try {
            bp = wrapper.findNextBreakPoint(inStr, 0, lineLength).lineBreakPosition;
        } catch (NoLineBreakPointException e) {
            fail(String.format("No line break was found when one was expected. String was \"%s\" line length is %d and expected break point is %d", inStr, lineLength, expectedBreak));
            return;
        }
        assertEquals(bp, expectedBreak.intValue());
    }

    @DataProvider(name = "checkStartLineInsertionPositionProvider")
    public Object[][] checkStartLineInsertionPositionProvider() {
        RegexLineWrapper nemethWrapper = new RegexLineWrapper();
        Map<String, String> nemethStartLineInsertions = new HashMap<>();
        nemethStartLineInsertions.put("-?(?=[0-9])", "#");
        nemethWrapper.setLineStartInsertions(nemethStartLineInsertions);
        LineWrapper uebWrapper = new RegexLineWrapper();
        RegexLineWrapper testWrapper = new RegexLineWrapper();
        Map<String, String> testInsertions = new HashMap<>();
        testInsertions.put("(?=[a-zA-Z])", ";");
        testWrapper.setLineStartInsertions(testInsertions);
        return new Object[][]{
                // Nemeth
                {nemethWrapper, "some text", 0, -1},
                {nemethWrapper, "some text", 4, -1},
                {nemethWrapper, "1+2", 0, 0},
                {nemethWrapper, "1+2", 1, -1},
                {nemethWrapper, "1+2", 2, 2},
                {nemethWrapper, "1-2", 0, 0},
                {nemethWrapper, "1-2", 1, 2},
                {nemethWrapper, "1-2", 2, 2},
                {nemethWrapper, "-1+2", 0, 1},
                {nemethWrapper, "1-a", 1, -1},
                // UEB
                {uebWrapper, "some text", 0, -1},
                {uebWrapper, "some text", 4, -1},
                {uebWrapper, "1+2", 0, -1},
                {uebWrapper, "1+2", 1, -1},
                {uebWrapper, "1+2", 2, -1},
                {uebWrapper, "1-2", 0, -1},
                {uebWrapper, "1-2", 1, -1},
                {uebWrapper, "1-2", 2, -1},
                {uebWrapper, "-1+2", 0, -1},
                {uebWrapper, "1-a", 1, -1},
                // Test wrapper to check regex is used
                {testWrapper, "some text", 0, 0},
                {testWrapper, "some text", 4, -1},
                {testWrapper, "1+2", 0, -1},
                {testWrapper, "1+2", 1, -1},
                {testWrapper, "1+2", 2, -1},
                {testWrapper, "1-2", 0, -1},
                {testWrapper, "1-2", 1, -1},
                {testWrapper, "1-2", 2, -1},
                {testWrapper, "-1+2", 0, -1},
                {testWrapper, "1-a", 1, -1},
                {testWrapper, "1-a", 2, 2},
        };
    }

    @Test(dataProvider = "checkStartLineInsertionPositionProvider")
    public void checkStartLineInsertion(LineWrapper wrapper, String brlText, int start, int expected) {
        int actual = wrapper.checkStartLineInsertion(brlText, start).insertionPosition;
        assertEquals(actual, expected);
    }

    @DataProvider(name = "checkStartLineInsertionDotsProvider")
    public Object[][] checkStartLineInsertionDotsProvider() {
        RegexLineWrapper wrapper = new RegexLineWrapper();
        Map<String, String> insertions = new HashMap<>();
        insertions.put("(?=[0-9])", "#");
        insertions.put("(?=[a-zA-Z])", ";");
        wrapper.setLineStartInsertions(insertions);
        return new Object[][]{
                {wrapper, "1+2", 0, "#"},
                {wrapper, "1+2", 2, "#"},
                {wrapper, "a+b", 0, ";"},
                {wrapper, "a+b", 2, ";"},
        };
    }

    @Test(dataProvider = "checkStartLineInsertionDotsProvider")
    public void checkStartLineInsertionCharacter(LineWrapper wrapper, String brlText, int start, String expected) {
        String actual = wrapper.checkStartLineInsertion(brlText, start).insertionDots;
        assertEquals(actual, expected);
    }

    @DataProvider(name = "setAndGetLineStartInsertionsProvider")
    public Object[][] setAndGetLineStartInsertionsProvider() {
        Map<String, String> insertions = new LinkedHashMap<>();
        insertions.put("(?=[a-zA-Z])", ";");
        insertions.put("(?=[0-9])", "#");
        Map<String, String> insertions2 = new LinkedHashMap<>();
        insertions2.put("jki", ";");
        insertions2.put("-", ";");
        insertions2.put("/", "_");
        RegexLineWrapper wrapper = new RegexLineWrapper();
        wrapper.setLineStartInsertions(insertions);
        RegexLineWrapper lastInsertionsOnlyWrapper = new RegexLineWrapper();
        lastInsertionsOnlyWrapper.setLineStartInsertions(insertions);
        lastInsertionsOnlyWrapper.setLineStartInsertions(insertions2);
        RegexLineWrapper wrapper2 = new RegexLineWrapper();
        wrapper2.setLineStartInsertions(insertions2);
        return new Object[][]{
                {wrapper, insertions},
                {lastInsertionsOnlyWrapper, insertions2},
                {wrapper2, insertions2},
        };
    }

    @Test(dataProvider = "setAndGetLineStartInsertionsProvider")
    public void setAndGetLineStartInsertions(RegexLineWrapper wrapper, Map<String, String> insertions) {
        Map<String, String> actualInsertions = wrapper.getLineStartInsertions();
        Set<Map.Entry<String, String>> expectedEntries = insertions.entrySet();
        Set<Map.Entry<String, String>> actualEntries = actualInsertions.entrySet();
        assertEquals(actualEntries.size(), expectedEntries.size());
        Iterator<Map.Entry<String, String>> expectedIt = expectedEntries.iterator();
        Iterator<Map.Entry<String, String>> actualIt = actualEntries.iterator();
        while (expectedIt.hasNext() && actualIt.hasNext()) {
            Map.Entry<String, String> expectedEntry = expectedIt.next();
            Map.Entry<String, String> actualEntry = actualIt.next();
            assertEquals(actualEntry.getKey(), expectedEntry.getKey());
            assertEquals(actualEntry.getValue(), expectedEntry.getValue());
        }
    }
}
