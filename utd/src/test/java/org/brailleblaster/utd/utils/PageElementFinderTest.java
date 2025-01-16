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

import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.brailleblaster.utd.ITranslationEngine;
import org.brailleblaster.utd.Style;
import org.brailleblaster.utd.StyleMap;
import org.brailleblaster.utd.UTDTranslationEngine;
import org.brailleblaster.utd.matchers.NodeNameMatcher;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

public class PageElementFinderTest {
    final ITranslationEngine engine = new UTDTranslationEngine();

    public Node documentBuilder() {
        StyleMap styleMap = new StyleMap();

        Element root = new Element("book");

        Element h1 = new Element("h1");
        h1.appendChild("Document Title");

        Element test = new Element("test");
        h1.appendChild(test);
        root.appendChild(h1);

        Element pageNum = new Element("pagenum");
        pageNum.appendChild("1");
        root.appendChild(pageNum);
        Style style = new Style();
        styleMap.put(new NodeNameMatcher("pagenum"), style);

        Element filler = new Element("brl");
        root.appendChild(filler);

        Element pageNum2 = new Element("pagenum");
        pageNum2.appendChild("ii");
        root.appendChild(pageNum2);
        Style style2 = new Style();
        styleMap.put(new NodeNameMatcher("pagenum"), style2);

        engine.setStyleMap(styleMap);
        return root;
    }

    @Test
    public void findPageNumbersTest() {
        Node root = documentBuilder();

        PageElementFinder finder = new PageElementFinder("pagenum");
        List<String> list = finder.findPageNumbers(root, engine);

        assertEquals(list.size(), 2);
        assertEquals(list.get(0), "1");
        assertEquals(list.get(1), "ii");
        assertTrue(engine.getStyle(root.getChild(1)).isPageNum());
        assertTrue(engine.getStyle(root.getChild(3)).isPageNum());
    }
}
