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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

public class NodeUtilsTest {
    @Test(dataProvider = "nodesProvider")
    public void testSortByDocumentOrderSameTree(List<Node> inputNodes, List<Node> expectedNodes) {
        List<Node> actualNodes = NodeUtils.sortByDocumentOrder(inputNodes);
        assertNotNull(actualNodes);
        assertEquals(actualNodes.size(), expectedNodes.size());
        for (int i = 0; i < actualNodes.size(); i++) {
            Node expected = expectedNodes.get(i);
            Node actual = actualNodes.get(i);
            assertSame(actual, expected);
        }
    }

    @DataProvider
    public Iterator<Object[]> nodesProvider() {
        List<Object[]> data = new ArrayList<>();
        Element root = new Element("root");
        new Document(root);
        List<Node> input = ImmutableList.of(root);
        List<Node> expected = ImmutableList.of(root);
        data.add(new Object[]{input, expected});
        Text t1 = new Text("Hello world");
        root = new Element("root");
        root.appendChild(t1);
        new Document(root);
        input = ImmutableList.of(root, t1);
        expected = ImmutableList.of(root, t1);
        data.add(new Object[]{input, expected});
        input = ImmutableList.of(t1, root);
        data.add(new Object[]{input, expected});
        t1 = new Text("Hello");
        Text t2 = new Text("World");
        root = new Element("p");
        root.appendChild(t1);
        root.appendChild(t2);
        new Document(root);
        input = ImmutableList.of(t1, t2);
        expected = ImmutableList.of(t1, t2);
        data.add(new Object[]{input, expected});
        input = ImmutableList.of(t2, t1);
        data.add(new Object[]{input, expected});
        input = ImmutableList.of(root, t2, t1);
        expected = ImmutableList.of(root, t1, t2);
        data.add(new Object[]{input, expected});
        t1 = new Text("Hello");
        t2 = new Text(", ");
        Text t3 = new Text("world");
        Element e1 = new Element("b");
        e1.appendChild(t2);
        root = new Element("p");
        root.appendChild(t1);
        root.appendChild(e1);
        root.appendChild(t3);
        new Document(root);
        input = ImmutableList.of(e1, t3, t2, t1, root);
        expected = ImmutableList.of(root, t1, e1, t2, t3);
        data.add(new Object[]{input, expected});
        return data.iterator();
    }
}
