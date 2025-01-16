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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nu.xom.Node;
import org.brailleblaster.utd.NamespaceMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class XPathMatcherTest {
    @Test(
            dataProvider = "xpathExpressions",
            dataProviderClass = XPathDataTest.class)
    public void constructor(String expression)
            throws SecurityException,
            IllegalArgumentException {
        XPathMatcher matcher = new XPathMatcher(expression);
        assertEquals(matcher.getExpression(), expression);
    }

    @Test(
            dataProvider = "matcherData",
            dataProviderClass = XPathDataTest.class)
    public void isMatch(String expression, NamespaceMap nsMap, Node node, boolean expected) {
        INodeMatcher matcher = new XPathMatcher(expression);
        assertEquals(matcher.isMatch(node, nsMap), expected);
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(XPathMatcher.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }
}
