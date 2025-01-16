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
package org.brailleblaster.utd.utils

import javax.xml.XMLConstants
import javax.xml.namespace.NamespaceContext

class PEFNamespaceContext : NamespaceContext {
    override fun getNamespaceURI(prefix: String): String {
        return when (prefix) {
            XMLConstants.DEFAULT_NS_PREFIX, "pef" -> PEF_NAMESPACE
            "dc" -> DC_NAMESPACE
            XMLConstants.XMLNS_ATTRIBUTE -> XMLConstants.XMLNS_ATTRIBUTE_NS_URI
            XMLConstants.XML_NS_PREFIX -> XMLConstants.XML_NS_URI
            else -> XMLConstants.NULL_NS_URI
        }
    }

    override fun getPrefix(namespaceURI: String): String? {
        return when (namespaceURI) {
            PEF_NAMESPACE -> XMLConstants.DEFAULT_NS_PREFIX
            DC_NAMESPACE -> "dc"
            XMLConstants.XML_NS_PREFIX -> XMLConstants.XML_NS_URI
            XMLConstants.XMLNS_ATTRIBUTE_NS_URI -> XMLConstants.XMLNS_ATTRIBUTE
            else -> null
        }
    }

    override fun getPrefixes(namespaceURI: String): Iterator<String> {
        val prefixes: List<String> = when (namespaceURI) {
            PEF_NAMESPACE -> listOf(XMLConstants.DEFAULT_NS_PREFIX, "pef")
            DC_NAMESPACE -> listOf("dc")
            XMLConstants.XML_NS_URI -> listOf(XMLConstants.XML_NS_PREFIX)
            XMLConstants.XMLNS_ATTRIBUTE_NS_URI -> listOf(XMLConstants.XMLNS_ATTRIBUTE)
            else -> listOf()
        }
        return prefixes.iterator()
    }

    companion object {
        const val DC_NAMESPACE = "http://purl.org/dc/elements/1.1/"
        const val PEF_NAMESPACE = "http://www.daisy.org/ns/2008/pef"
        const val TG_NAMESPACE = "http://www.aph.org/ns/tactile-graphics/1.0"
        const val PAPER_NAMESPACE = "http://www.aph.org/ns/paper-dimensions/1.0"
    }
}