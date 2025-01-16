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

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import org.brailleblaster.utd.config.StyleDefinitions;
import org.brailleblaster.utd.config.UTDConfig;
import org.brailleblaster.utd.matchers.INodeMatcher;
import org.brailleblaster.utd.matchers.NodeNameMatcher;
import org.brailleblaster.utd.matchers.XPathMatcher;
import org.brailleblaster.utd.testutils.UTDConfigUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import nu.xom.XPathContext;

public class StyleMapTest extends NodeMatcherMapTest {
    public static final String DEFS_TEST_PATH = "/org/brailleblaster/utd/cli/styleMap.xml";

    @Test
    public void getAndSetNamespaceMap() {
        super.getAndSetNamespaceMap(new StyleMap());
    }

    @DataProvider
    private Object[][] styleMapDataProvider() {
        StyleDefinitions styleDefs = new StyleDefinitions();
        StyleMap map = new StyleMap();
        map.getNamespaces().addNamespace("xhtml", "http://www.w3.org/555555/xhtml");

        Style style = new Style();
        style.setName(StyleDefinitions.DEFAULT_STYLE);
        style.setLinesBefore(89);
        styleDefs.addStyle(style);

        style = new Style();
        style.setName("test");
        style.setLinesAfter(55);
        style.setDontSplit(true);
        styleDefs.addStyle(style);
        map.put(new XPathMatcher("//p"), style);

        return new Object[][]{
                new Object[]{map, styleDefs},
        };
    }

    @Test(dataProvider = "styleMapDataProvider")
    public void save(StyleMap map, StyleDefinitions styleDefs) throws Exception {
        File tempOutput = File.createTempFile("styleMap", "test");
        UTDConfig.saveStyle(tempOutput, map);
        UTDConfigUtils.compareOutputToSaved(tempOutput, UTDConfigUtils.TEST_STYLE_FILE);
    }

    @Test(dataProvider = "styleMapDataProvider")
    public void load(StyleMap mapExpected, StyleDefinitions styleDefs) throws Exception {
        //TODO: Only handles ActionMap impl do to get(int)
        StyleMap mapLoaded = UTDConfig.loadStyle(UTDConfigUtils.TEST_STYLE_FILE, styleDefs);

        assertEquals(mapLoaded.getDefaultValue(), styleDefs.getDefaultStyle(), "StyleMap not using Style Defs default style");

        //Validate action map
        assertEquals(mapLoaded.size(), mapExpected.size(), "Map sizes are different");
        for (int i = 0; i < mapExpected.size(); i++) {
            INodeMatcher matcherExpected = mapExpected.get(i);
            INodeMatcher matcherLoaded = mapLoaded.get(i);
            assertEquals(matcherLoaded.getClass(), matcherExpected.getClass(), "Matcher classes are different at index " + i
                    + System.lineSeparator() + " expected keys " + mapExpected.keyList()
                    + System.lineSeparator() + " loaded keys " + mapLoaded.keyList());

            if (matcherExpected instanceof XPathMatcher) {
                assertEquals(((XPathMatcher) matcherLoaded).getExpression(), ((XPathMatcher) matcherExpected).getExpression(),
                        "XPath expressions are different at index " + i);
            } else if (matcherExpected instanceof NodeNameMatcher) {
                assertEquals(((NodeNameMatcher) matcherLoaded).getNamespace(), ((NodeNameMatcher) matcherExpected).getNamespace(),
                        "Namespace are different at index " + i);
                assertEquals(((NodeNameMatcher) matcherLoaded).getNodeName(), ((NodeNameMatcher) matcherExpected).getNodeName(),
                        "Node name are different at index " + i);
            } else {
                Assert.fail("Matcher " + matcherExpected.getClass() + " not handled");
            }
        }

        //Validate internal namespace map
        assertEquals(mapLoaded.getNamespaces().getPrefixes(), mapExpected.getNamespaces().getPrefixes(), "Namespace prefixes are different");

        //Forcibly rip out the internal map of XPathContext to compare since it doesn't expose any get methods
        //There's not really a better way to do this
        Field xpathInternalMap = XPathContext.class.getDeclaredField("namespaces");
        xpathInternalMap.setAccessible(true);
        Map<?, ?> xpathLoaded = (Map<?, ?>) xpathInternalMap.get(mapLoaded.getNamespaces().getXPathContext());
        Map<?, ?> xpathExpected = (Map<?, ?>) xpathInternalMap.get(mapExpected.getNamespaces().getXPathContext());
        assertEquals(xpathLoaded, xpathExpected, "XPathContext maps are different");
    }
}
