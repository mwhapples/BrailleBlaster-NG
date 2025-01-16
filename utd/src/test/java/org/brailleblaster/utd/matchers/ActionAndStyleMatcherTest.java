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
package org.brailleblaster.utd.matchers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brailleblaster.utd.NamespaceMap;
import org.brailleblaster.utd.ComparableStyle;
import org.brailleblaster.utd.properties.UTDElements;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import nu.xom.Attribute;
import nu.xom.Element;

public class ActionAndStyleMatcherTest {
    @DataProvider(name = "matchingActionsProvider")
    private Iterator<Object[]> matchingActionsProvider() {
        List<Object[]> dataList = new ArrayList<>();
        Element e = new Element("p");
        e.addAttribute(new Attribute(UTDElements.UTD_ACTION_ATTRIB, "p"));
        List<String> actions = new ArrayList<>();
        actions.add("p");
        dataList.add(new Object[]{e, actions});
        e = new Element("t");
        e.addAttribute(new Attribute(UTDElements.UTD_ACTION_ATTRIB, "Box"));
        actions = new ArrayList<>();
        actions.add("Box");
        actions.add("Bold");
        dataList.add(new Object[]{e, actions});
        e = new Element("t");
        e.addAttribute(new Attribute(UTDElements.UTD_ACTION_ATTRIB, "Box"));
        e.addAttribute(new Attribute(UTDElements.UTD_STYLE_ATTRIB, "Blocked Text"));
        actions = new ArrayList<>();
        actions.add("Box");
        actions.add("Bold");
        dataList.add(new Object[]{e, actions});
        return dataList.iterator();
    }

    @Test(dataProvider = "matchingActionsProvider")
    public void testMatchAction(Element e, List<String> actions) {
        ActionAndStyleMatcher matcher = new ActionAndStyleMatcher();

        matcher.setActions(actions);
        NamespaceMap namespaces = new NamespaceMap();
        assertTrue(matcher.isMatch(e, namespaces));
    }

    @DataProvider(name = "matchingStylesProvider")
    private Iterator<Object[]> matchingStylesProvider() {
        List<Object[]> dataList = new ArrayList<>();
        Element e = new Element("p");
        e.addAttribute(new Attribute(UTDElements.UTD_STYLE_ATTRIB, "p"));
        List<ComparableStyle> styles = new ArrayList<>();
        styles.add(new ComparableStyle("p"));
        dataList.add(new Object[]{e, styles});
        e = new Element("t");
        e.addAttribute(new Attribute(UTDElements.UTD_STYLE_ATTRIB, "Box"));
        styles = new ArrayList<>();
        styles.add(new ComparableStyle("Box"));
        styles.add(new ComparableStyle("Bold"));
        dataList.add(new Object[]{e, styles});
        e = new Element("t");
        e.addAttribute(new Attribute(UTDElements.UTD_ACTION_ATTRIB, "Box"));
        e.addAttribute(new Attribute(UTDElements.UTD_STYLE_ATTRIB, "Blocked Text"));
        styles = new ArrayList<>();
        styles.add(new ComparableStyle("Blocked Text"));
        styles.add(new ComparableStyle("Bold"));
        dataList.add(new Object[]{e, styles});
        return dataList.iterator();
    }

    @Test(dataProvider = "matchingStylesProvider")
    public void testMatchStyle(Element e, List<ComparableStyle> styles) {
        ActionAndStyleMatcher matcher = new ActionAndStyleMatcher();

        matcher.setStyles(styles);
        NamespaceMap namespaces = new NamespaceMap();
        assertTrue(matcher.isMatch(e, namespaces));
    }

    @DataProvider(name = "nonMatchingActionsProvider")
    private Iterator<Object[]> nonMatchingActionsProvider() {
        List<Object[]> dataList = new ArrayList<>();
        Element e = new Element("p");
        e.addAttribute(new Attribute(UTDElements.UTD_ACTION_ATTRIB, "n"));
        List<String> actions = new ArrayList<>();
        actions.add("p");
        dataList.add(new Object[]{e, actions});
        e = new Element("t");
        e.addAttribute(new Attribute(UTDElements.UTD_ACTION_ATTRIB, "Line"));
        actions = new ArrayList<>();
        actions.add("Box");
        actions.add("Bold");
        dataList.add(new Object[]{e, actions});
        e = new Element("t");
        e.addAttribute(new Attribute(UTDElements.UTD_ACTION_ATTRIB, "Heading"));
        e.addAttribute(new Attribute(UTDElements.UTD_STYLE_ATTRIB, "Box"));
        actions = new ArrayList<>();
        actions.add("Box");
        actions.add("Bold");
        dataList.add(new Object[]{e, actions});
        return dataList.iterator();
    }

    @Test(dataProvider = "nonMatchingActionsProvider")
    public void testNonMatchAction(Element e, List<String> actions) {
        ActionAndStyleMatcher matcher = new ActionAndStyleMatcher();

        matcher.setActions(actions);
        NamespaceMap namespaces = new NamespaceMap();
        assertFalse(matcher.isMatch(e, namespaces));
    }

    @DataProvider(name = "nonMatchingStylesProvider")
    private Iterator<Object[]> nonMatchingStylesProvider() {
        List<Object[]> dataList = new ArrayList<>();
        Element e = new Element("p");
        e.addAttribute(new Attribute(UTDElements.UTD_STYLE_ATTRIB, "n"));
        List<ComparableStyle> styles = new ArrayList<>();
        styles.add(new ComparableStyle("p"));
        dataList.add(new Object[]{e, styles});
        e = new Element("t");
        e.addAttribute(new Attribute(UTDElements.UTD_STYLE_ATTRIB, "Line"));
        styles = new ArrayList<>();
        styles.add(new ComparableStyle("Box"));
        styles.add(new ComparableStyle("Bold"));
        dataList.add(new Object[]{e, styles});
        e = new Element("t");
        e.addAttribute(new Attribute(UTDElements.UTD_ACTION_ATTRIB, "Box"));
        e.addAttribute(new Attribute(UTDElements.UTD_STYLE_ATTRIB, "Blocked Text"));
        styles = new ArrayList<>();
        styles.add(new ComparableStyle("Box"));
        styles.add(new ComparableStyle("Bold"));
        dataList.add(new Object[]{e, styles});
        return dataList.iterator();
    }

    @Test(dataProvider = "nonMatchingStylesProvider")
    public void testNonMatchStyle(Element e, List<ComparableStyle> styles) {
        ActionAndStyleMatcher matcher = new ActionAndStyleMatcher();

        matcher.setStyles(styles);
        NamespaceMap namespaces = new NamespaceMap();
        assertFalse(matcher.isMatch(e, namespaces));
    }
}
