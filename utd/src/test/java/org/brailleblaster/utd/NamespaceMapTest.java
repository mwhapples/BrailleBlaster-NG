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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class NamespaceMapTest {
    @DataProvider(name = "namespaceMapData")
    private Object[][] provideNamespaceMapData() {
        return new Object[][]{
                {"xhtml", "http://www.w3.org/1999/xhtml"},
                {"m", "http://www.w3.org/1998/Math/MathML"}
        };
    }

    @Test
    public void defaultConstructor() {
        NamespaceMap nsMap = new NamespaceMap();
        assertEquals(nsMap.getPrefixes().size(), 1);
        assertTrue(nsMap.getPrefixes().contains("xml"));
        assertEquals(nsMap.getNamespace("xml"), "http://www.w3.org/XML/1998/namespace");
    }

    @Test(dataProvider = "namespaceMapData")
    public void constructWithSingleMapping(String prefix, String uri) {
        NamespaceMap namespaces = new NamespaceMap(prefix, uri);
        assertEquals(namespaces.getPrefixes().size(), 2);
        assertTrue(namespaces.getPrefixes().contains(prefix));
        assertEquals(namespaces.getNamespace(prefix), uri);
        assertTrue(namespaces.getPrefixes().contains("xml"));
        assertEquals(namespaces.getNamespace("xml"), "http://www.w3.org/XML/1998/namespace");
    }

    @Test(dataProvider = "namespaceMapData")
    public void addNamespace(String prefix, String uri) {
        NamespaceMap nsMap = new NamespaceMap();
        nsMap.addNamespace(prefix, uri);
        // Should appear in the prefix set
        assertTrue(nsMap.getPrefixes().contains(prefix));
        // Should be able to get the namespace
        assertEquals(nsMap.getNamespace(prefix), uri);
    }
}
