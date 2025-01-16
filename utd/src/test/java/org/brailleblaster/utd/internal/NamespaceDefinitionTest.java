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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.testng.Assert.assertEquals;

public class NamespaceDefinitionTest {
    private DocumentBuilder docBuilder;

    public DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if (docBuilder == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            docBuilder = dbf.newDocumentBuilder();
        }
        return docBuilder;
    }

    @DataProvider(name = "namespaceDefinitions")
    private Object[][] provideNamespaces() {
        return new Object[][]{
                {"xhtml", "http://www.w3.org/1999/xhtml"},
                {"m", "http://www.w3.org/1998/Math/MathML"}
        };
    }

    @Test(dataProvider = "namespaceDefinitions")
    public void constructor(String prefix, String uri) {
        NamespaceDefinition nsDef = new NamespaceDefinition(prefix, uri);
        assertEquals(nsDef.getPrefix(), prefix);
        assertEquals(nsDef.getUri(), uri);
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(NamespaceDefinition.class).verify();
    }

    @Test(dataProvider = "namespaceDefinitions")
    public void jaxbMarshal(String prefix, String uri) throws Exception {
        Document doc = getDocumentBuilder().newDocument();
        NamespaceDefinition nsDef = new NamespaceDefinition(prefix, uri);
        JAXBElement<NamespaceDefinition> jaxbElement = new JAXBElement<>(new QName("namespace"), NamespaceDefinition.class, nsDef);
        JAXBContext jc = JAXBContext.newInstance(NamespaceDefinition.class);
        Marshaller m = jc.createMarshaller();
        m.setEventHandler(JAXBUtils.FAIL_ON_EXCEPTIONS_HANDLER);
        m.marshal(jaxbElement, doc);
        Element result = doc.getDocumentElement();
        assertEquals(result.getLocalName(), "namespace");
        assertEquals(result.getAttribute("prefix"), prefix);
        assertEquals(result.getAttribute("uri"), uri);
        assertEquals(result.getAttributes().getLength(), 2);
    }
}
