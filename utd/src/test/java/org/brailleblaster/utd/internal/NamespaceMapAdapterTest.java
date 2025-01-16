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
package org.brailleblaster.utd.internal;

import java.util.ArrayList;
import java.util.List;

import org.brailleblaster.utd.NamespaceMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class NamespaceMapAdapterTest {
    private NamespaceMap xhtmlNsMap;
    private List<NamespaceDefinition> xhtmlNsList;
    private NamespaceMap mathmlNsMap;
    private List<NamespaceDefinition> mathmlNsList;

    @BeforeClass
    public void classSetup() {
        NamespaceDefinition xhtmlDef = new NamespaceDefinition("xhtml", "http://www.w3.org/1999/xhtml");
        xhtmlNsMap = new NamespaceMap(xhtmlDef.getPrefix(), xhtmlDef.getUri());
        xhtmlNsList = new ArrayList<>();
        xhtmlNsList.add(xhtmlDef);
        NamespaceDefinition mathmlDef = new NamespaceDefinition("m", "http://www.w3.org/1998/Math/MathML");
        mathmlNsMap = new NamespaceMap(mathmlDef.getPrefix(), mathmlDef.getUri());
        mathmlNsList = new ArrayList<>();
        mathmlNsList.add(mathmlDef);
    }

    @Test
    public void defaultConstructorMarshal() {
        NamespaceMapAdapter adapter = new NamespaceMapAdapter();
        AdaptedNamespaceMap result = adapter.marshal(xhtmlNsMap);
        assertEquals(result.getNamespaces(), xhtmlNsList);
        result = adapter.marshal(mathmlNsMap);
        assertEquals(result.getNamespaces(), mathmlNsList);
        assertNull(adapter.marshal(null));
    }

    @Test
    public void defaultConstructorUnmarshal() {
        NamespaceMapAdapter adapter = new NamespaceMapAdapter();
        assertNull(adapter.unmarshal(null));
        NamespaceMap result = adapter.unmarshal(new AdaptedNamespaceMap(xhtmlNsList));
        // Remember namespace maps have the addition of the XML prefix definition.
        assertEquals(result.getPrefixes().size(), xhtmlNsList.size() + 1);
        for (String prefix : result.getPrefixes()) {
            if (!"xml".equals(prefix)) {
                assertTrue(xhtmlNsList.contains(new NamespaceDefinition(prefix, result.getNamespace(prefix))));
            }
        }
        result = adapter.unmarshal(new AdaptedNamespaceMap(mathmlNsList));
        assertEquals(result.getPrefixes().size(), mathmlNsList.size() + 1);
        for (String prefix : result.getPrefixes()) {
            if (!"xml".equals(prefix)) {
                assertTrue(mathmlNsList.contains(new NamespaceDefinition(prefix, result.getNamespace(prefix))));
            }
        }
    }
}
