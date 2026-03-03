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
package org.brailleblaster.ebraille

import nu.xom.Document
import nu.xom.Element
import org.brailleblaster.bbx.BBX
import org.brailleblaster.libembosser.utils.BrailleMapper
import org.brailleblaster.utd.properties.UTDElements
import org.brailleblaster.utd.utils.getDescendantBrlFast
import org.brailleblaster.utils.xml.BB_NS

object BBX2HTML {
    private val FALLBACK_TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head></head>
            <body></body>
            </html>
        """.trimIndent()
    fun convertBbxToHtml(document: Document): org.jsoup.nodes.Document {
        val bbxRoot = requireNotNull(document.rootElement) { "BBX document must have a root element" }
        require(bbxRoot.namespaceURI == BB_NS && bbxRoot.localName == "bbdoc") { "Document must be a BBX document." }
        val template = javaClass.getResourceAsStream("/org/brailleblaster/ebraille/document_template.html")?.bufferedReader(Charsets.UTF_8)?.readText() ?: FALLBACK_TEMPLATE
        val htmlDoc = org.jsoup.Jsoup.parse(template)
        htmlDoc.head().appendChildren(bbxRoot.getFirstChildElement("head", BB_NS)?.processHead() ?: listOf())
        htmlDoc.body()
            .appendChildren(bbxRoot.childElements.filter { BBX.SECTION.ROOT.isA(it) }.flatMap { it.processChildren() })
        return htmlDoc
    }
}

private fun Element.processHead(): Collection<org.jsoup.nodes.Node> = listOf()

private fun Element.processChildren(): Iterable<org.jsoup.nodes.Node> = childElements.flatMap {
    when(BBX.getTypeOrNull(it)) {
        BBX.SECTION -> it.processSection()
        BBX.CONTAINER -> it.processContainer()
        BBX.BLOCK -> it.processBlock()
        else -> it.processChildren()
    }
}

private fun Element.processSection(): Iterable<org.jsoup.nodes.Node> = processChildren()

private fun Element.processContainer(): Iterable<org.jsoup.nodes.Node> = processChildren()

private fun Element.processBlock(): Iterable<org.jsoup.nodes.Element> = when(BBX.BLOCK.getSubType(this)) {
    BBX.BLOCK.STYLE -> processStyle()
    BBX.BLOCK.DEFAULT -> listOf(processParagraph())
    else -> listOf(processParagraph())
}

private fun Element.processStyle(): Iterable<org.jsoup.nodes.Element> {
    val style = getAttributeValue(UTDElements.UTD_STYLE_ATTRIB)
    return when (style) {
        "Centered Heading" -> listOf(processParagraph(tag = "h1"))
        "Cell 5 Heading" -> listOf(processParagraph(tag = "h2"))
        "Cell 7 Heading" -> listOf(processParagraph(tag = "h3"))
        "Blocked Text" -> listOf(processParagraph(tag = "p", attributes = mapOf("class" to "left-justified")))
        "Centered Text" -> listOf(processParagraph(tag = "p", attributes = mapOf("class" to "centered")))
        else -> listOf(processParagraph())
    }
}

private fun Element.processParagraph(
    tag: String = "p",
    attributes: Map<String, String> = mapOf()
): org.jsoup.nodes.Element = org.jsoup.nodes.Element(tag).apply {
    for ((k, v) in attributes) {
        attr(k, v)
    }
}.appendText(getDescendantBrlFast().joinToString { BrailleMapper.ASCII_TO_UNICODE_FAST.map(it.value) })