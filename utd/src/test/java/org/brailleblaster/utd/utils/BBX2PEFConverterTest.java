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

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nu.xom.Document;
import org.brailleblaster.utd.internal.elements.Brl;
import org.brailleblaster.utd.internal.elements.BrlOnly;
import org.brailleblaster.utd.internal.elements.BrlPageNumber;
import org.brailleblaster.utd.internal.elements.MoveTo;
import org.brailleblaster.utd.internal.elements.NewPage;
import org.brailleblaster.utd.internal.elements.PrintPageNumber;
import org.brailleblaster.utd.properties.UTDElements;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import nu.xom.Attribute;
import nu.xom.Element;

public class BBX2PEFConverterTest {
    public static final Document EMPTY_DOC = new Document(new Element("root"));
    private XPathExpression findVolumes;
    private XPathExpression findRelativePages;
    private XPathExpression findRelativeSections;
    private XPathExpression findTitle;
    private XPathExpression findSubjects;
    private XPath xpath;
    private XPathExpression findRelativeRows;
    private XPathExpression findIdentifier;

    @BeforeClass
    public void createXPathExpressions() throws XPathExpressionException {
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new PEFNamespaceContext());
        findVolumes = xpath.compile("/pef:pef/pef:body/pef:volume");
        findRelativeSections = xpath.compile("./pef:section");
        findRelativePages = xpath.compile("./pef:page");
        findRelativeRows = xpath.compile(".//pef:row");
        findIdentifier = xpath.compile("/pef:pef/pef:head/pef:meta/dc:identifier");
        findTitle = xpath.compile("/pef:pef/pef:head/pef:meta/dc:title");
        findSubjects = xpath.compile("/pef:pef/pef:head/pef:meta/dc:subject");
    }

    public void assertPageEquals(org.w3c.dom.Node actual, String[] expectedLines) throws XPathExpressionException {
        org.w3c.dom.NodeList rows = (org.w3c.dom.NodeList) findRelativeRows.evaluate(actual, XPathConstants.NODESET);
        assertEquals(rows.getLength(), expectedLines.length, "Incorrect number of lines");
        for (int i = 0; i < expectedLines.length; i++) {
            assertEquals(rows.item(i).getTextContent(), expectedLines[i], String.format("Row %d does not match", i));
        }
    }

    @Test
    public void testSetPageSize() throws XPathExpressionException {
        BBX2PEFConverter c = new BBX2PEFConverter();
        assertEquals(c.getRows(), 25, "Number of initial rows is incorrect");
        assertEquals(c.getCols(), 40, "Number of initial columns is not correct");
        int[] rowArray = new int[]{25, 20, 35, 10};
        int[] colArray = new int[]{40, 30, 35, 40};
        for (int i = 0; i < rowArray.length; i++) {
            c.setPageSize(rowArray[i], colArray[i]);
            assertEquals(c.getRows(), rowArray[i], "Number of rows incorrect");
            assertEquals(c.getCols(), colArray[i], "Number of columns incorrect");
            c.onStartDocument(EMPTY_DOC);
            c.onEndDocument(EMPTY_DOC);
            org.w3c.dom.Document pef = c.getPefDoc();
            org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
            assertTrue(vols.getLength() > 0, "Resulting doc has no volumes");
            for (int volIndex = 0; volIndex < vols.getLength(); ++volIndex) {
                assertTrue((Boolean) xpath.evaluate(
                                String.format("count(self::node()[@cols='%d' and @rows='%d']) = 1",
                                        colArray[i], rowArray[i]),
                                vols.item(volIndex), XPathConstants.BOOLEAN),
                        "Volume does not have correct number of cols and rows");
            }
        }
    }

    @Test
    public void createMinimalDocument() throws XPathExpressionException {
        BBX2PEFConverter c = new BBX2PEFConverter();
        c.onStartDocument(EMPTY_DOC);
        c.onEndDocument(EMPTY_DOC);
        XPathExpression checkVolAttributes = xpath.compile("count(self::node()[@cols='40' and @duplex='false' and @rowgap='0' and @rows='25']) = 1");
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertTrue(vols.getLength() > 0, "Minimal document requires 1 volume");
        for (int volIndex = 0; volIndex < vols.getLength(); ++volIndex) {
            org.w3c.dom.Node vol = vols.item(volIndex);
            assertTrue((Boolean) checkVolAttributes.evaluate(vol, XPathConstants.BOOLEAN), "Volume must have cols, duplex, rowgap and rows attributes");
            org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
            assertTrue(sections.getLength() > 0, "Volume must contain at least 1 section");
            for (int sectionIndex = 0; sectionIndex < sections.getLength(); ++sectionIndex) {
                org.w3c.dom.Node section = sections.item(sectionIndex);
                org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(section, XPathConstants.NODESET);
                assertTrue(pages.getLength() > 0, "Section must have at least 1 page");
            }
        }
    }

    @Test
    public void testCreateBasicDocument() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        brl.appendChild("⠠\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList identifiers = (org.w3c.dom.NodeList) findIdentifier.evaluate(pef, XPathConstants.NODESET);
        assertEquals(identifiers.getLength(), 1, "There should only be 1 identifier");
        assertEquals(identifiers.item(0).getTextContent(), "TempID");
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testBasicDocumentWithDefaultIdentifier() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        brl.appendChild("\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.setDefaultIdentifier("TestDoc0001");
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList identifiers = (org.w3c.dom.NodeList) findIdentifier.evaluate(pef, XPathConstants.NODESET);
        assertEquals(identifiers.getLength(), 1, "There should only be 1 identifier");
        assertEquals(identifiers.item(0).getTextContent(), "TestDoc0001");
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node page = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e"};
        assertPageEquals(page, expectedLines);
    }

    @Test
    public void basicDocumentMetaFromDC() throws XPathExpressionException {
        // The test DC elements
        Element metaElement;
        Element headElement = new Element("head", "http://brailleblaster.org/ns/bb");
        final String identifierStr = "Example001";
        metaElement = new Element("dc:identifier", PEFNamespaceContext.DC_NAMESPACE);
        metaElement.appendChild(identifierStr);
        headElement.appendChild(metaElement);
        final String titleStr = "An example document";
        metaElement = new Element("dc:title", PEFNamespaceContext.DC_NAMESPACE);
        metaElement.appendChild(titleStr);
        headElement.appendChild(metaElement);
        final List<String> subjectsList = Lists.newArrayList("Braille", "Transcription");
        for (String subject : subjectsList) {
            metaElement = new Element("dc:subject", PEFNamespaceContext.DC_NAMESPACE);
            metaElement.appendChild(subject);
            headElement.appendChild(metaElement);
        }

        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        brl.appendChild("\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.setDefaultIdentifier("TestDoc0001");
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(headElement));
        c.onEndElement(headElement);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList nodes = (org.w3c.dom.NodeList) findIdentifier.evaluate(pef, XPathConstants.NODESET);
        assertEquals(nodes.getLength(), 1, "There can only be one identifier");
        assertEquals(nodes.item(0).getTextContent(), identifierStr);
        nodes = (org.w3c.dom.NodeList) findTitle.evaluate(pef, XPathConstants.NODESET);
        assertEquals(nodes.getLength(), 1, "There should only be 1 title");
        assertEquals(nodes.item(0).getTextContent(), titleStr);
        nodes = (org.w3c.dom.NodeList) findSubjects.evaluate(pef, XPathConstants.NODESET);
        List<String> actualSubjects = new ArrayList<>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); ++i) {
            actualSubjects.add(nodes.item(i).getTextContent());
        }
        assertThat(actualSubjects).containsExactlyInAnyOrderElementsOf(subjectsList);
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void basicDocumentMetaFromMeta() throws XPathExpressionException {
        // The test DC elements
        Element metaElement;
        Element headElement = new Element("head", "http://brailleblaster.org/ns/bb");
        final String identifierStr = "Example001";
        metaElement = new Element("meta");
        metaElement.addAttribute(new Attribute("name", "dc:identifier"));
        metaElement.appendChild(identifierStr);
        headElement.appendChild(metaElement);
        final String titleStr = "An example document";
        metaElement = new Element("meta");
        metaElement.addAttribute(new Attribute("name", "dc:title"));
        metaElement.appendChild(titleStr);
        headElement.appendChild(metaElement);
        final List<String> subjectsList = Lists.newArrayList("Braille", "Transcription");
        for (String subject : subjectsList) {
            metaElement = new Element("meta");
            metaElement.addAttribute(new Attribute("name", "dc:subject"));
            metaElement.appendChild(subject);
            headElement.appendChild(metaElement);
        }

        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        brl.appendChild("\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.setDefaultIdentifier("TestDoc0001");
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(headElement));
        c.onEndElement(headElement);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList nodes = (org.w3c.dom.NodeList) findIdentifier.evaluate(pef, XPathConstants.NODESET);
        assertEquals(nodes.getLength(), 1, "There should only be 1 identifier");
        assertEquals(nodes.item(0).getTextContent(), identifierStr);
        nodes = (org.w3c.dom.NodeList) findTitle.evaluate(pef, XPathConstants.NODESET);
        assertEquals(nodes.getLength(), 1, "There should only be 1 title");
        assertEquals(nodes.item(0).getTextContent(), titleStr);
        nodes = (org.w3c.dom.NodeList) findSubjects.evaluate(pef, XPathConstants.NODESET);
        List<String> actualSubjects = new ArrayList<>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); ++i) {
            actualSubjects.add(nodes.item(i).getTextContent());
        }
        assertThat(actualSubjects).containsExactlyInAnyOrderElementsOf(subjectsList);
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testInsertAsciiBraille() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild(",te/ text");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        assertFalse(Boolean.parseBoolean(vol.getAttribute("duplex")));
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) sections.item(0);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testDuplexMode() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild(",te/ text");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.setDuplex(true);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        assertTrue(Boolean.parseBoolean(vol.getAttribute("duplex")), "Duplex attribute is incorrect");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testBasicDocWithMultipleLines() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        brl.appendChild("\u2820\u2811\u280c\u2800\u281e\u2811\u282d");
        brl.appendChild(new MoveTo(new BigDecimal("0.0"), new BigDecimal("10.0")));
        brl.appendChild("\u280e\u2811\u2809\u2815\u281d\u2819\u2800\u2807\u2814\u2811");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u2811\u280c\u2800\u281e\u2811\u282d",
                "\u280e\u2811\u2809\u2815\u281d\u2819\u2800\u2807\u2814\u2811"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testBasicDocWithEmptyLines() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        brl.appendChild(new MoveTo(new BigDecimal("0.0"), new BigDecimal("10.0")));
        brl.appendChild("\u2820\u2811\u280c\u2800\u281e\u2811\u282d");
        brl.appendChild(new MoveTo(new BigDecimal("0.0"), new BigDecimal("40.0")));
        brl.appendChild("\u280e\u2811\u2809\u2815\u281d\u2819\u2800\u2807\u2814\u2811");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{
                "",
                "\u2820\u2811\u280c\u2800\u281e\u2811\u282d",
                "", "",
                "\u280e\u2811\u2809\u2815\u281d\u2819\u2800\u2807\u2814\u2811"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testInsertPageNumbers() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild(",te/ text");
        brl.appendChild(new MoveTo(new BigDecimal("235.6"), new BigDecimal("0.0")));
        brl.appendChild(new BrlPageNumber("#a"));
        brl.appendChild(new MoveTo(new BigDecimal("235.6"), new BigDecimal("240.0")));
        brl.appendChild(new PrintPageNumber("#b"));

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u283c\u2801",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u283c\u2803"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testMultiplePages() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild(",te/ text");
        brl.appendChild(new MoveTo(new BigDecimal("235.6"), new BigDecimal("0.0")));
        brl.appendChild(new BrlPageNumber("#a"));
        brl.appendChild(new MoveTo(new BigDecimal("235.6"), new BigDecimal("240.0")));
        brl.appendChild(new PrintPageNumber("#b"));
        brl.appendChild(new NewPage());
        brl.appendChild(new MoveTo(new BigDecimal("0.0"), new BigDecimal("0.0")));
        brl.appendChild(",next page");
        brl.appendChild(new MoveTo(new BigDecimal("235.6"), new BigDecimal("0.0")));
        brl.appendChild(new BrlPageNumber("#b"));
        brl.appendChild(new MoveTo(new BigDecimal("229.4"), new BigDecimal("240.0")));
        brl.appendChild(new PrintPageNumber("a#b"));

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 2, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u283c\u2801",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u283c\u2803"};
        assertPageEquals(p, expectedLines);
        p = pages.item(1);
        expectedLines = new String[]{"\u2820\u281d\u2811\u282d\u281e\u2800\u280f\u2801\u281b\u2811\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u283c\u2803",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2801\u283c\u2803"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testInsertBrlOnly() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(new NewPage());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild(",te/ text");
        brl.appendChild(new MoveTo(new BigDecimal("12.4"), new BigDecimal("10.0")));
        BrlOnly brlOnly = new BrlOnly();
        brlOnly.appendChild("33 33 33");
        brl.appendChild(brlOnly);

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e", "\u2800\u2800\u2812\u2812\u2800\u2812\u2812\u2800\u2812\u2812"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testInsertBrailleWithWhitespaceOverrun() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(new NewPage());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild("3333333333333333333333333333333333333333   	");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{Strings.repeat("\u2812", 40)};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testMultilineInsertBrailleWithWhitespaceOverrun() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(new NewPage());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild("3333333333333333333333333333333333333333   	");
        brl.appendChild(new MoveTo(new BigDecimal("0.0"), new BigDecimal("10.0")));
        brl.appendChild("111");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{Strings.repeat("\u2812", 40),
                "\u2802\u2802\u2802"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testMultipleVolumes() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild(",te/ text");
        Element volumeContainer = new Element("CONTAINER", "http://brailleblaster.org/ns/bb");
        volumeContainer.addAttribute(new Attribute("bb:type", "http://brailleblaster.org/ns/bb", "VOLUME"));
        Brl endVolBrl = new Brl();
        endVolBrl.appendChild(new MoveTo(new BigDecimal("0.0"), new BigDecimal("20.0")));
        endVolBrl.appendChild(",5d volume");
        volumeContainer.appendChild(endVolBrl);
        Brl brl2 = new Brl();
        brl2.appendChild(new NewPage());
        brl2.appendChild(",text 9 vol#b");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        assertTrue(c.onStartElement(volumeContainer));
        assertFalse(c.onStartElement(endVolBrl));
        c.onEndElement(endVolBrl);
        c.onEndElement(volumeContainer);
        assertFalse(c.onStartElement(brl2));
        c.onEndElement(brl2);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 2, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u280c\u2800\u281e\u2811\u282d\u281e",
                "",
                "\u2820\u2822\u2819\u2800\u2827\u2815\u2807\u2825\u280d\u2811"};
        assertPageEquals(p, expectedLines);

        vol = (org.w3c.dom.Element) vols.item(1);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        p = pages.item(0);
        expectedLines = new String[]{"\u2820\u281e\u2811\u282d\u281e\u2800\u2814\u2800\u2827\u2815\u2807\u283c\u2803"};
        assertPageEquals(p, expectedLines);
    }

    @Test
    public void testMultipleVolumesWithFilter() throws XPathExpressionException {
        // The test brl
        Brl brl = new Brl();
        brl.appendChild(UTDElements.NEW_PAGE.create());
        // Use ASCII Braille in brl, BBX2PEF will convert it.
        brl.appendChild(",te/ text");
        Element volumeContainer = new Element("CONTAINER", "http://brailleblaster.org/ns/bb");
        volumeContainer.addAttribute(new Attribute("bb:type", "http://brailleblaster.org/ns/bb", "VOLUME"));
        Brl endVolBrl = new Brl();
        endVolBrl.appendChild(new MoveTo(new BigDecimal("0.0"), new BigDecimal("20.0")));
        endVolBrl.appendChild(",5d volume #a");
        volumeContainer.appendChild(endVolBrl);
        Brl brl2 = new Brl();
        brl2.appendChild(new NewPage());
        brl2.appendChild(",text 9 vol#b");
        Element volumeContainer2 = new Element("CONTAINER", "http://brailleblaster.org/ns/bb");
        volumeContainer2.addAttribute(new Attribute("bb:type", "http://brailleblaster.org/ns/bb", "VOLUME"));
        Brl endVolBrl2 = new Brl();
        endVolBrl2.appendChild(new MoveTo(new BigDecimal("0.0"), new BigDecimal("20.0")));
        endVolBrl2.appendChild(",5d volume #b");
        volumeContainer2.appendChild(endVolBrl2);
        Brl brl3 = new Brl();
        brl3.appendChild(new NewPage());
        brl3.appendChild(",text 9 vol#c");

        BBX2PEFConverter c = new BBX2PEFConverter();
        c.setPageSize(25, 40);
        c.setVolumeFilter(x -> x == 1);
        c.onStartDocument(EMPTY_DOC);
        assertFalse(c.onStartElement(brl));
        c.onEndElement(brl);
        assertTrue(c.onStartElement(volumeContainer));
        assertFalse(c.onStartElement(endVolBrl));
        c.onEndElement(endVolBrl);
        c.onEndElement(volumeContainer);
        assertFalse(c.onStartElement(brl2));
        c.onEndElement(brl2);
        assertTrue(c.onStartElement(volumeContainer2));
        assertFalse(c.onStartElement(endVolBrl2));
        c.onEndElement(endVolBrl2);
        c.onEndElement(volumeContainer2);
        assertFalse(c.onStartElement(brl3));
        c.onEndElement(brl3);
        c.onEndDocument(EMPTY_DOC);
        org.w3c.dom.Document pef = c.getPefDoc();
        org.w3c.dom.NodeList vols = (org.w3c.dom.NodeList) findVolumes.evaluate(pef, XPathConstants.NODESET);
        assertEquals(vols.getLength(), 1, "Incorrect volume count");
        org.w3c.dom.Element vol = (org.w3c.dom.Element) vols.item(0);
        assertEquals(vol.getAttribute("rows"), "25", "Incorrect rows per page");
        assertEquals(vol.getAttribute("cols"), "40", "Incorrect columns per page");
        org.w3c.dom.NodeList sections = (org.w3c.dom.NodeList) findRelativeSections.evaluate(vol, XPathConstants.NODESET);
        assertEquals(sections.getLength(), 1, "Incorrect section count");
        org.w3c.dom.NodeList pages = (org.w3c.dom.NodeList) findRelativePages.evaluate(sections.item(0), XPathConstants.NODESET);
        assertEquals(pages.getLength(), 1, "Incorrect page count");
        org.w3c.dom.Node p = pages.item(0);
        String[] expectedLines = new String[]{"\u2820\u281e\u2811\u282d\u281e\u2800\u2814\u2800\u2827\u2815\u2807\u283c\u2803",
                "",
                "\u2820\u2822\u2819\u2800\u2827\u2815\u2807\u2825\u280d\u2811\u2800\u283c\u2803"};
        assertPageEquals(p, expectedLines);
    }
}
