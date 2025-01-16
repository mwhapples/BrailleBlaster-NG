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

import com.google.common.base.CaseFormat;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.brailleblaster.utd.matchers.NodeNameMatcher;
import org.brailleblaster.utd.properties.Align;
import org.brailleblaster.utd.properties.NumberLinePosition;
import org.brailleblaster.utd.properties.PageNumberType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class StyleTest {
    private Style style;

    @BeforeMethod
    public void setupMethod() {
        style = new Style();
    }

    @Test
    public void defaultConstructor() {
        Style lStyle = new Style();
        assertEquals(lStyle.getLinesBefore(), 0);
        assertEquals(lStyle.getLinesAfter(), 0);
        assertNull(lStyle.getFirstLineIndent());
        assertNull(lStyle.getIndent());
        assertEquals(lStyle.getName(), "DEFAULT");
        assertEquals(lStyle.getOrphanControl(), 1);
        assertNull(lStyle.getLineLength());
        assertFalse(lStyle.isDontSplit());
        assertFalse(lStyle.isKeepWithNext());
        assertFalse(lStyle.isKeepWithPrevious());
        assertEquals(lStyle.getNewPagesAfter(), 0);
        assertEquals(lStyle.getNewPagesBefore(), 0);
        assertEquals(lStyle.getSkipNumberLines(), NumberLinePosition.NONE);
        assertEquals(lStyle.getLineSpacing(), 0);
        assertFalse(lStyle.isGuideWords());
        assertFalse(lStyle.isPageNum());
        assertFalse(lStyle.isVolumeEnd());
    }

    @Test
    public void nameGetAndSet() {
        style.setName("Test style");
        assertEquals(style.getName(), "Test style");
    }

    @Test
    public void dontSplitGetAndSet() {
        style.setDontSplit(true);
        assertTrue(style.isDontSplit());
    }

    @Test
    public void linesAfterGetAndSet() {
        style.setLinesAfter(1);
        assertEquals(style.getLinesAfter(), 1);
    }

    @Test
    public void linesBeforeGetAndSet() {
        style.setLinesBefore(1);
        assertEquals(style.getLinesBefore(), 1);
    }

    @Test
    public void newPageBeforeGetAndSet() {
        style.setNewPagesBefore(1);
        assertEquals(style.getNewPagesBefore(), 1);
    }

    @Test
    public void newPageAfterGetAndSet() {
        style.setNewPagesAfter(1);
        assertEquals(style.getNewPagesAfter(), 1);
    }

    @Test
    public void firstLineIndentGetAndSet() {
        style.setFirstLineIndent(1);
        assertEquals(style.getFirstLineIndent(), 1.0, 0.01);
    }

    @Test
    public void keepWithNextGetAndSet() {
        style.setKeepWithNext(true);
        assertTrue(style.isKeepWithNext());
    }

    @Test
    public void keepWithPreviousGetAndSet() {
        style.setKeepWithPrevious(true);
        assertTrue(style.isKeepWithPrevious());
    }

    @Test
    public void skipNumberLinesGetAndSet() {
        style.setSkipNumberLines(NumberLinePosition.TOP);
        assertEquals(style.getSkipNumberLines(), NumberLinePosition.TOP);
        style.setSkipNumberLines(NumberLinePosition.BOTTOM);
        assertEquals(style.getSkipNumberLines(), NumberLinePosition.BOTTOM);
        style.setSkipNumberLines(NumberLinePosition.BOTH);
        assertEquals(style.getSkipNumberLines(), NumberLinePosition.BOTH);

        style.setGuideWords(true);
        assertEquals(style.getSkipNumberLines(), NumberLinePosition.BOTTOM);

    }


    //START METHODS TO TEST VALUES FOR EACH STYLE

    @DataProvider(name = "positiveNumbers")
    public Object[][] positiveNumbers() {
        return new Object[][]{{1}, {2}, {3}, {10}, {20}, {25},
                {50}};
    }

    @DataProvider(name = "negativeNumbers")
    public Object[][] negativeNumbers() {
        return new Object[][]{{-4}, {-5}, {-6}, {-15}, {-30}, {-45},
                {-50}};
    }

    @DataProvider(name = "zeroAndNegative")
    public Object[][] zeroAndNegative() {
        return new Object[][]{{0}, {-10}, {-25}, {-50}};
    }

    @Test(dataProvider = "positiveNumbers")
    public void linesBeforeGetAndSet(int linesBefore) {
        style.setLinesBefore(linesBefore);
        assertEquals(style.getLinesBefore(), linesBefore);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class},
            dataProvider = "negativeNumbers")
    public void linesBeforeSetNegativeNumber(int linesBefore) {
        style.setLinesBefore(linesBefore);
    }

    @Test(dataProvider = "positiveNumbers")
    public void linesAfterGetAndSet(int linesAfter) {
        style.setLinesAfter(linesAfter);
        assertEquals(style.getLinesAfter(), linesAfter);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class},
            dataProvider = "negativeNumbers")
    public void linesAfterSetNegativeNumber(int linesAfter) {
        style.setLinesAfter(linesAfter);
    }

    @Test(dataProvider = "positiveNumbers")
    public void indentGetAndSet(Integer indent) {
        assertNull(style.getIndent());
        style.setIndent(indent);
        assertEquals(style.getIndent(), indent.doubleValue(), 0.01);
    }

    @Test(dataProvider = "negativeNumbers")
    public void lineLengthSetNegativeNumber(Integer lineLength) {
        this.lineLengthGetAndSet(lineLength);
    }

    @Test(dataProvider = "positiveNumbers")
    public void lineLengthGetAndSet(Integer lineLength) {
        assertNull(style.getLineLength());
        style.setLineLength(lineLength);
        assertEquals(style.getLineLength(), lineLength.doubleValue(), 0.01);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class},
            dataProvider = "negativeNumbers")
    public void indentSetNegativeNumber(Integer indent) {
        style.setIndent(indent);
    }

    @Test(dataProvider = "positiveNumbers")
    public void orphanControlGetAndSet(int orphanControl) {
        style.setOrphanControl(orphanControl);
        assertEquals(style.getOrphanControl(), orphanControl);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class},
            dataProvider = "zeroAndNegative")
    public void orphanControlSetNegativeNumber(int orphanControl) {
        style.setOrphanControl(orphanControl);
    }

    @Test
    public void testEquals() {
        Style test = new Style();
        style.setFirstLineIndent(5);
        assertEquals(style, style);
        assertNotEquals(test, style);
    }

    @Test
    public void testGuideWords() {
        Style style = new Style();
        style.setGuideWords(true);
        assertTrue(style.isGuideWords());
    }

    @Test
    public void testPageNum() {
        Style style = new Style();
        style.setPageNum(true);
        assertTrue(style.isPageNum());
    }

    @Test
    public void testVolumeEnd() {
        Style style = new Style();
        style.setVolumeEnd(true);
        assertTrue(style.isVolumeEnd());
    }

    @Test
    public void testHashCode() {
        Style style1 = new Style();
        Style style2 = new Style();
        assertEquals(style1.hashCode(), style2.hashCode());
    }

    @DataProvider(name = "stylesProvider")
    public Iterator<Object[]> stylesProvider() {
        List<Object[]> styles = new ArrayList<>();
        Style style = new Style();
        style.setName("My Style");
        ConditionalValue<Integer> condition1 = new ConditionalValue<>();
        condition1.setMatcher(new NodeNameMatcher("h1"));
        condition1.setValue(1);
        style.getLinesBeforeWhen().add(condition1);
        condition1 = new ConditionalValue<>();
        condition1.setMatcher(new NodeNameMatcher("p"));
        condition1.setValue(2);
        style.getLinesAfterWhen().add(condition1);
        style.setIndent(5);
        style.setBraillePageNumberFormat(PageNumberType.T_PAGE);
        style.setAlign(Align.CENTERED);
        style.setFirstLineIndent(3);
        style.setLineLength(1);
        style.setKeepWithNext(true);
        styles.add(new Object[]{style});
        return styles.iterator();
    }

    @Test(dataProvider = "stylesProvider")
    public void testCopyStyleCreatesNewInstance(IStyle origStyle) {
        IStyle copyStyle = origStyle.copy("DEFAULT", "DEFAULT");
        assertNotNull(copyStyle);
        assertTrue(copyStyle instanceof Style);
        assertNotSame(copyStyle, origStyle);
    }

    @Test(dataProvider = "stylesProvider")
    public void testCopyStyleCreatesEqual(IStyle origStyle) {
        IStyle copyStyle = origStyle.copy("DEFAULT", origStyle.getName());
        assertEquals(copyStyle, origStyle);
    }

    @Test(dataProvider = "stylesProvider")
    public void testCopyStyleMaxLinesDifferentInstances(IStyle origStyle) {
        IStyle copyStyle = origStyle.copy("DEFAULT", "DEFAULT");
        List<ConditionalValue<Integer>> actual = copyStyle.getLinesBeforeWhen();
        List<ConditionalValue<Integer>> expected = origStyle.getLinesBeforeWhen();
        assertEquals(actual, expected);
        assertNotSame(actual, expected);
        actual = copyStyle.getLinesAfterWhen();
        expected = origStyle.getLinesAfterWhen();
        assertEquals(actual, expected);
        assertNotSame(actual, expected);
    }

    @Test(dataProvider = "stylesProvider")
    public void testCopyStyleIsNotJustModification(IStyle origStyle) {
        IStyle copyStyle = origStyle.copy("DEFAULT", "DEFAULT");
        assertNotSame(((Style) copyStyle).getBaseStyle(), origStyle);
    }

    /**
     * This test is needed as BB depends on accurate style option names and fields
     */
    @Test
    public void testStyleOptionNamesConsistent() {
        for (Style.StyleOption curOption : Style.StyleOption.values()) {
            assertEquals(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, curOption.name()), curOption.getOptionName());

            List<Method> methods = Arrays.asList(Style.class.getMethods());
            String methodSuffix = StringUtils.capitalize(curOption.getOptionName());
            assertTrue(methods.stream()
                            .anyMatch(curMethod -> curMethod.getName().equals("set" + methodSuffix)),
                    "Method set" + methodSuffix + " not found"
            );
            System.out.println(curOption.getOptionName() + " ");

            String methodName;
            String getPrefix;
            if (curOption.getOptionName().startsWith("is")) {
                methodName = StringUtils.uncapitalize(methodSuffix);
            } else {
                getPrefix = (curOption.getDefaultValue() != null && curOption.getDefaultValue().getClass().isAssignableFrom(Boolean.class))
                        ? "is"
                        : "get";
                methodName = getPrefix + methodSuffix;
            }
            assertTrue(methods.stream()
                            .anyMatch(curMethod -> curMethod.getName().equals(methodName)),
                    "Method " + methodName + " not found"
            );
        }
    }

}
