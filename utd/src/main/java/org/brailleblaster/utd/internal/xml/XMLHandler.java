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
package org.brailleblaster.utd.internal.xml;

import com.google.common.collect.Streams;
import nu.xom.*;
import org.apache.commons.io.input.BOMInputStream;
import org.brailleblaster.utd.exceptions.NodeException;
import org.brailleblaster.utd.utils.LocalEntityResolver;
import org.brailleblaster.utils.xom.NodeUtilsKt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import org.jetbrains.annotations.Nullable;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Handles processing of XML documents from and to the disk
 */
public class XMLHandler {
    private static final Logger log = LoggerFactory.getLogger(XMLHandler.class);

    public static @NotNull Stream<@NotNull Node> queryStream(
            @NotNull Node node, @NotNull String xpathPattern, Object... xpathArgs) {
        return Streams.stream(XMLHandler2.query(node, xpathPattern, xpathArgs));
    }

    public static Text findFirstText(@NotNull Element someElement) {
        return queryStream(someElement, "descendant::text()[not(ancestor::utd:brl)]")
                .map(n -> (Text) n)
                .filter(t -> !t.getValue().isBlank())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find text in " + someElement.toXML()));
    }

    public static List<Element> queryElements(@NotNull Node node, @NotNull String xpathPattern, Object... xpathArgs) {
        return queryStream(node, xpathPattern, xpathArgs).filter(n -> n instanceof Element).map(n -> (Element)n).toList();
    }


    @NotNull
    public Document load(Path xmlPathInput) {
        String baseUri = xmlPathInput.toUri().toString();
        // Due to an issue in XOM where the jar: protocol will fail XOM URI verification,
        // we alter the protocol to our custom one which will pass.
        // We will then need to handle it in the entity resolver.
        if (baseUri.startsWith("jar:")) {
            // Must re-encode for a valid url
            // Otherwise files with eg [ or ] will throw an MalformedURIException: Path contains invalid
            // character
            baseUri =
                    LocalEntityResolver.ENCODED_URI_PREFIX
                            + URLEncoder.encode(baseUri, StandardCharsets.UTF_8);
        }
        return load(xmlPathInput, baseUri);
    }

    public Document load(Path xmlPathInput, String baseUri) {
        try {
            Builder builder = LocalEntityResolver.createXomBuilder(false);
            return builder.build(BOMInputStream.builder().setInputStream(Files.newInputStream(xmlPathInput)).get(), baseUri);
        } catch (ValidityException e) {
            throw new RuntimeException("The document failed to validate in " + xmlPathInput, e);
        } catch (ParsingException e) {
            throw new RuntimeException("The document XML is malformed in " + xmlPathInput, e);
        } catch (IOException e) {
            throw new RuntimeException("Problem reading file " + xmlPathInput, e);
        } catch (SAXException | ParserConfigurationException e) {
            throw new RuntimeException(
                    "Problem creating the XML parser, may be Apache Xerces is not installed", e);
        }
    }

    public Document load(File xmlFileInput) {
        File xmlFile = xmlFileInput.getAbsoluteFile();
        if (!xmlFile.exists()) throw new RuntimeException("XML File " + xmlFile + " does not exist");
        try {
            Builder builder = LocalEntityResolver.createXomBuilder(false);
            return builder.build(xmlFile);
        } catch (ValidityException e) {
            throw new RuntimeException("The document failed to validate in " + xmlFileInput, e);
        } catch (ParsingException e) {
            throw new RuntimeException("The document XML is malformed in " + xmlFileInput, e);
        } catch (IOException e) {
            throw new RuntimeException("Problem reading file " + xmlFileInput, e);
        } catch (SAXException | ParserConfigurationException e) {
            throw new RuntimeException(
                    "Problem creating the XML parser, may be Apache Xerces is not installed", e);
        }
    }

    public Document load(InputStream input) {
        try {
            Builder builder = LocalEntityResolver.createXomBuilder(false);
            return builder.build(input);
        } catch (ValidityException e) {
            throw new RuntimeException("The document failed to validate", e);
        } catch (ParsingException e) {
            throw new RuntimeException("The document XML is malformed", e);
        } catch (IOException e) {
            throw new RuntimeException("Problem reading file", e);
        } catch (SAXException | ParserConfigurationException e) {
            throw new RuntimeException(
                    "Problem creating the XML parser, may be Apache Xerces is not installed", e);
        }
    }

    public Document load(Reader input) {
        try {
            Builder builder = LocalEntityResolver.createXomBuilder(false);
            return builder.build(input);
        } catch (ValidityException e) {
            throw new RuntimeException("The document failed to validate", e);
        } catch (ParsingException e) {
            throw new RuntimeException("The document XML is malformed", e);
        } catch (IOException e) {
            throw new RuntimeException("Problem reading file", e);
        } catch (SAXException | ParserConfigurationException e) {
            throw new RuntimeException(
                    "Problem creating the XML parser, may be Apache Xerces is not installed", e);
        }
    }

    public void save(Document doc, File xmlFileOutput) {
        File xmlFile = xmlFileOutput.getAbsoluteFile();
        boolean overwriting = xmlFile.exists();
        try (FileOutputStream output = new FileOutputStream(xmlFileOutput)) {
            save(doc, output);
            log.debug(
                    "Wrote UTD output to {}, overwriting {}", xmlFileOutput.getCanonicalPath(), overwriting);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file to " + xmlFileOutput.getAbsolutePath(), e);
        }
    }

    public void save(Document doc, OutputStream output) throws IOException {
        if (doc == null) throw new NullPointerException("doc");
        Serializer serializer = newSerializer(output);
        serializer.write(doc);
    }

    protected Serializer newSerializer(OutputStream output) throws IOException {
        return new Serializer(output, "UTF-8");
    }

    /**
     * Provides a preformatted xml file for debugging
     */
    public static class Formatted extends XMLHandler {
        @Override
        protected Serializer newSerializer(OutputStream output) throws IOException {
            Serializer serializer = super.newSerializer(output);
            serializer.setIndent(2);
            return serializer;
        }
    }

    @Nullable
    public static Element ancestorVisitorElement(Node node, Predicate<@NotNull Element> onAncestor) {
        return (Element)
                ancestorVisitor(
                        node,
                        (Node n) -> n instanceof Element && onAncestor.test((Element) n));
    }

    /**
     *
     */
    @Nullable
    public static Node ancestorVisitor(Node node, Predicate<Node> onAncestor) {
        if (node == null) throw new NullPointerException("Element");
        Document document = node.getDocument();
        if (document == null) {
            throw new RuntimeException("Element is detached from a document element " + node.toXML());
        }
        if (node == document) return null;

        if (onAncestor.test(node)) return node;

        // Can be given text nodes which should just go up
        ParentNode parentNode = node.getParent();
        if (parentNode == document) return null;
        if (parentNode == null)
            throw new RuntimeException("Has document but parent is null? " + node.toXML());
        return ancestorVisitor(parentNode, onAncestor);
    }

    public static Element ancestorVisitorFatal(Node node, Predicate<Element> onAncestor) {
        Element result = ancestorVisitorElement(node, onAncestor);
        if (result == null) throw new NodeException("Unable to match node ", node);
        return result;
    }

    public static boolean ancestorElementNot(Node node, Predicate<Element> matcher) {
        return ancestorVisitorElement(node, matcher) == null;
    }

    public static boolean ancestorElementIs(Node node, Predicate<Element> matcher) {
        return ancestorVisitorElement(node, matcher) != null;
    }

    /**
     * Utility to linearly get ancestors
     */
    public static List<Element> ancestor(Element element) {
        List<Element> ancestors = ancestorOrSelf(element);
        ancestors.remove(0);
        return ancestors;
    }

    /**
     * Utility to linerally get ancestors
     */
    public static List<Element> ancestorOrSelf(Element element) {
        List<Element> ancestors = new ArrayList<>();
        Element curAncestor = element;
        while (curAncestor != null) {
            ancestors.add(curAncestor);

            ParentNode parent = curAncestor.getParent();
            if (parent instanceof Document) {
                break;
            }
            curAncestor = (Element) parent;
        }
        return ancestors;
    }

    /**
     * Lowest Common Ancestor Problem
     */
    public static Element findCommonParent(List<Element> elements) {
        if (elements.isEmpty()) throw new IllegalArgumentException("elements list cannot be empty");
        for (Element element : elements) {
            log.debug("in list {}", element.toXML());
            if (element.getDocument() == null) {
                throw new NodeException(
                        "Element #" + elements.indexOf(element) + " isn't attached to document", element);
            }
        }

        ParentNode curParent = elements.get(0);
        if (elements.size() == 1) return (Element) curParent;

        int counter = 1;
        while (true) {
            Set<Element> remainingElements = new HashSet<>(elements);
            final int curCounter = counter;
            childrenRecursiveVisitor(
                    (Element) curParent,
                    (Element curElem) -> {
                        if (remainingElements.contains(curElem)) {
                            log.debug("Found at iteration {} element {}", curCounter, curElem.toXML());
                            remainingElements.remove(curElem);
                        }

                        // Go through all children
                        return false;
                    });
            if (remainingElements.isEmpty()) break;
            curParent = curParent.getParent();
            if (curParent == curParent.getDocument())
                throw new RuntimeException("Unable to find common parent at iteration " + counter);
            counter++;
        }
        return (Element) curParent;
    }

    /**
     * Call given callback on every recursive child element until it returns true
     */
    @Nullable
    public static Element childrenRecursiveVisitor(Element curElem, Predicate<Element> onElement) {
        if (onElement.test(curElem)) {
            return curElem;
        }
        for (Element iterableXOMChildren : curElem.getChildElements()) {
            Element result = childrenRecursiveVisitor(iterableXOMChildren, onElement);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static @Nullable Node childrenRecursiveNodeVisitor(Node curNode, Predicate<Node> onElement) {
        if (onElement.test(curNode)) {
            return curNode;
        }
        for (Node iterableXOMChildren : NodeUtilsKt.getChildNodes(curNode)) {
            Node result = childrenRecursiveNodeVisitor(iterableXOMChildren, onElement);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Apply given namespace to every revursive child element
     */
    public static void setNamespaceRecursive(Element root, String namespaceURI) {
        childrenRecursiveVisitor(
                root,
                curElement -> {
                    curElement.setNamespaceURI(namespaceURI);

                    // Keep going through all children
                    return false;
                });
    }

    /**
     * Gets the next sibling node under the given nodes parent
     */
    public static @Nullable Node nextSiblingNode(Node node) {
        ParentNode parent = node.getParent();
        if (parent == null) {
            throw new NodeException("Node doesn't have parent", node);
        }

        int index = parent.indexOf(node);
        if (index == parent.getChildCount() - 1) {
            // Last child node of parent
            return null;
        }

        return parent.getChild(index + 1);
    }

    /**
     * Gets the next sibling node under the given nodes parent
     */
    public static Node previousSiblingNode(Node node) {
        ParentNode parent = node.getParent();
        if (parent == null) {
            throw new NodeException("Node doesn't have parent", node);
        }

        int index = parent.indexOf(node);
        if (index == 0) {
            // First child node of parent
            return null;
        }

        return parent.getChild(index - 1);
    }

    /**
     * Returns the node after the current node. If last node in parent, return the parent's (or
     * ancestors) following sibling
     */
    public static Node followingNode(Node node) {
        if (node instanceof Document) {
            return null;
        }

        if (node.getParent() == null) {
            throw new NodeException("Start node doesn't have parent", node);
        }

        ParentNode parent = node.getParent();
        int index = parent.indexOf(node);
        if (index == parent.getChildCount() - 1) {
            // Last entry in parent, get parents sibling
            return followingNode(parent);
        }

        return parent.getChild(index + 1);
    }

    /**
     * Java impl of following:: xpath query, will start searching after current node
     */
    public static Node followingVisitor(Node startNode, Predicate<Node> onNode) {
        return followingWithSelfVisitor(followingNode(startNode), onNode);
    }

    @Nullable
    public static Node followingWithSelfVisitor(Node startNode, Predicate<Node> onNode) {
        Node curNode = startNode;
        while (curNode != null) {
            //			log.trace("Current node " + XMLHandler.toXMLSimple(curNode));
            if (onNode.test(curNode)) return curNode;

            if (curNode.getChildCount() != 0) {
                curNode = curNode.getChild(0);
            } else {
                curNode = followingNode(curNode);
            }
        }
        return null;
    }

}
