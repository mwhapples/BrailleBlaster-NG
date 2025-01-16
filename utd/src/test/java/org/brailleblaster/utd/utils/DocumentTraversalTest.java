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

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

public class DocumentTraversalTest {
    private static class TestHandler extends DocumentTraversal {
        private final StringBuilder sb = new StringBuilder();

        public String getResult() {
            return sb.toString();
        }

        @Override
        public void onStartDocument(@NotNull Document d) {
            sb.append("Start of document\n");
        }

        @Override
        public void onEndDocument(@NotNull Document d) {
            sb.append("End of document\n");
        }

        @Override
        public boolean onStartElement(Element e) {
            sb.append("Start of element: ");
            sb.append(e.getLocalName());
            sb.append("\n");
            return "descend".equals(e.getLocalName());
        }

        @Override
        public void onEndElement(Element e) {
            sb.append("End of element: ");
            sb.append(e.getLocalName());
            sb.append("\n");
        }

        @Override
        public void onComment(Comment c) {
            sb.append("On comment: ");
            sb.append(c.getValue());
            sb.append("\n");
        }

        @Override
        public void onProcessingInstruction(ProcessingInstruction pi) {
            sb.append("On processing instruction: ");
            sb.append(pi.getTarget());
            sb.append("\n");
        }

        @Override
        public void onText(Text t) {
            sb.append("On text: ");
            sb.append(t.getValue());
            sb.append("\n");
        }

        @Override
        public void onUnknownNode(@NotNull Node n) {
            sb.append("Unknown node");
        }
    }

    @Test
    public void testTraverseDocument() {
        Element root = new Element("descend");
        Element e = new Element("E1");
        e.appendChild(new Comment("C1"));
        e.appendChild(new Text("T1"));
        e.appendChild(new Element("E2"));
        root.appendChild(e);
        e = new Element("descend");
        e.appendChild(new Comment("C2"));
        e.appendChild(new ProcessingInstruction("PI1", "data1"));
        e.appendChild(new Text("T2"));
        e.appendChild(new Element("E3"));
        root.appendChild(e);
        Document doc = new Document(root);
        TestHandler handler = new TestHandler();
        handler.traverseDocument(doc);
        String expected = """
                Start of document
                Start of element: descend
                Start of element: E1
                End of element: E1
                Start of element: descend
                On comment: C2
                On processing instruction: PI1
                On text: T2
                Start of element: E3
                End of element: E3
                End of element: descend
                End of element: descend
                End of document
                """;
        assertEquals(handler.getResult(), expected);
    }
}
