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

import java.io.StringReader;

import nu.xom.Document;
import nu.xom.Node;
import org.brailleblaster.utd.NamespaceMap;
import org.brailleblaster.utd.internal.xml.XMLHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ImmediatelyPrecedingMatcherTest {
    private static final Logger log = LoggerFactory.getLogger(ImmediatelyPrecedingMatcherTest.class);

    @DataProvider
    private Object[][] matchDataProvider() {
        return new Object[][]{
                new Object[]{
                        "<level2>"
                                + "<h2>Some big header</h2>"
                                + "<level3>\n"
                                + "<h3>Some nested header</h3>"
                                + "</level3>"
                                + "</level2>", true},
                new Object[]{
                        "<level2>"
                                + "<h2>Some big header</h2>"
                                + "<p>This text should cause the below header not to match that style</p>"
                                + "<level3>"
                                + "<h3>Some nested header</h3>"
                                + "</level3>"
                                + "</level2>", false},
                new Object[]{
                        "<level2>"
                                + "<h2>Some big header</h2>"
                                + "<level3>"
                                + "<p>This text should cause the below header not to match that style</p>"
                                + "<h3>Some nested header</h3>"
                                + "</level3>"
                                + "</level2>", false},
                new Object[]{
                        "<level1>"
                                + "<h1>Some big header</h1>"
                                + "<level2>"
                                + "<level3>"
                                + "<h3>Some nested header</h3>"
                                + "</level3>"
                                + "</level2>"
                                + "</level1>", true},
                new Object[]{
                        "<level1>"
                                + "<h1>Some big header</h1>"
                                + "<level2>"
                                + "<p>This text should cause the below header not to match that style</p>"
                                + "<level3>"
                                + "<h3>Some nested header</h3>"
                                + "</level3>"
                                + "</level2>"
                                + "</level1>", false},
                new Object[]{
                        "<level1>"
                                + "<h1>Some big header</h1>"
                                + "<p>This text should cause the below header not to match that style</p>"
                                + "<level2>"
                                + "<level3>"
                                + "<h3>Some nested header</h3>"
                                + "</level3>"
                                + "</level2>"
                                + "</level1>", false},};
    }

    @Test(dataProvider = "matchDataProvider")
    public void matchTest(String xml, boolean shouldMatch) {
        Document doc = new XMLHandler().load(new StringReader(xml));

        ImmediatelyPrecedingMatcher matcher = new ImmediatelyPrecedingMatcher();
        matcher.setSelfName("h3");
        matcher.setParentName("h2|h1");

        NamespaceMap ns = new NamespaceMap();
        Node node = doc.query("descendant::h3").get(0);
        log.debug("Found node {}", node);
        assertEquals(matcher.isMatch(node, ns), shouldMatch);
    }
}
