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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;
import org.brailleblaster.utd.NamespaceMap;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class XPathDataTest {
    @DataProvider(name = "xpathExpressions")
    public static Object[][] provideXPathExpressions() {
        return new Object[][]{{"//p"}, {"p"}, {"b"}, {"*"}};
    }

    @DataProvider(name = "matcherData")
    public static Iterator<Object[]> provideMatcherData() {
        String xhtmlPrefix = "xhtml";
        String xhtmlUri = "http://www.w3.org/1999/xhtml";
        String dsyUri = "http://www.daisy.org/z3986/2005/dtbook";
        NamespaceMap emptyNsMap = new NamespaceMap();
        NamespaceMap nsMap = new NamespaceMap();
        nsMap.addNamespace(xhtmlPrefix, xhtmlUri);
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"//p", emptyNsMap, new Element("p"), true});
        data.add(new Object[]{"../strong", emptyNsMap, new Element("strong"), true});
        data.add(new Object[]{".[local-name()='strong']", emptyNsMap, new Element("strong"), true});
        data.add(new Object[]{"*[local-name()='strong']", emptyNsMap, new Text("Strong text"), false});
        data.add(new Object[]{"//b", emptyNsMap, new Element("b"), true});
        data.add(new Object[]{"//p", emptyNsMap, new Element("b"), false});
        data.add(new Object[]{"//b", emptyNsMap, new Element("p"), false});
        Element e = new Element("e");
        e.addAttribute(new Attribute("a", "The value"));
        data.add(new Object[]{"//e[@b]", emptyNsMap, e, false});
        e = new Element("e");
        e.addAttribute(new Attribute("a", "The value"));
        data.add(new Object[]{"//e[@a]", emptyNsMap, e, true});
        e = new Element("e");
        e.addAttribute(new Attribute("a", "Different value"));
        data.add(new Object[]{"//e[@a]", emptyNsMap, e, true});
        e = new Element("e");
        e.addAttribute(new Attribute("a", "The value"));
        data.add(new Object[]{"//e[@b='The value']", emptyNsMap, e, false});
        e = new Element("e");
        e.addAttribute(new Attribute("a", "The value"));
        data.add(new Object[]{"//e[@a='The value']", emptyNsMap, e, true});
        e = new Element("e");
        e.addAttribute(new Attribute("a", "Different value"));
        data.add(new Object[]{"//e[@a='The value']", emptyNsMap, e, false});
        data.add(new Object[]{"//xhtml:p", nsMap, new Element("p", xhtmlUri), true});
        data.add(new Object[]{"//xhtml:p", nsMap, new Element("h1", xhtmlUri), false});
        data.add(new Object[]{"//xhtml:p", nsMap, new Element("p", dsyUri), false});
        e = new Element("e");
        e.addAttribute(new Attribute("xhtml:a", xhtmlUri, "value"));
        data.add(new Object[]{"//e[@xhtml:a]", nsMap, e, true});
        e = new Element("e");
        e.addAttribute(new Attribute("dsy:a", dsyUri, "The value"));
        data.add(new Object[]{"//e[@xhtml:a]", nsMap, e, false});
        e = new Element("e");
        e.addAttribute(new Attribute("xhtml:b", xhtmlUri, "The value"));
        data.add(new Object[]{"//e[@xhtml:a]", nsMap, e, false});
        e = new Element("e");
        e.addAttribute(new Attribute("dsy:b", dsyUri, "The value"));
        data.add(new Object[]{"//e[@xhtml:a]", nsMap, e, false});
        Element root = new Element("root");
        root.appendChild(new Element("p"));
        data.add(new Object[]{"//p", emptyNsMap, root, false});
        return data.iterator();
    }

    @Test
    public void createMatcherData() {
        XPathDataTest.provideMatcherData();
    }
}
