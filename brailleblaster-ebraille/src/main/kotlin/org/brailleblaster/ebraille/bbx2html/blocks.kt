/*
 * Copyright (C) 2025-2026 American Printing House for the Blind
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
package org.brailleblaster.ebraille.bbx2html

import nu.xom.Element
import org.brailleblaster.bbx.BBX
import org.brailleblaster.ebraille.asciiToEbraille
import org.brailleblaster.utils.xml.UTD_NS
import org.brailleblaster.utils.xom.childNodes

internal fun Element.processBlock(): Collection<org.jsoup.nodes.Element> = when(BBX.BLOCK.getSubType(this)) {
    BBX.BLOCK.STYLE -> processStyle()
    BBX.BLOCK.LIST_ITEM -> listOf(processParagraph(tag = "li"))
    BBX.BLOCK.PAGE_NUM -> listOf(processPageNum())
    BBX.BLOCK.DEFAULT -> listOf(processParagraph())
    else -> listOf(processParagraph())
}

internal fun Element.processPageNum(): org.jsoup.nodes.Element = org.jsoup.nodes.Element("span").attr("role", "doc-pagebreak").apply {
    val brl = getFirstChildElement("brl", UTD_NS)
    attr("aria-label", brl.getAttributeValue("printPage") ?: "")
    appendText(asciiToEbraille(brl.getAttributeValue("printPageBrl") ?: ""))
}

private fun Element.processStyle(): Collection<org.jsoup.nodes.Element> = when (style) {
    "Centered Heading" -> listOf(processParagraph(tag = "h1"))
    "Cell 5 Heading" -> listOf(processParagraph(tag = "h2"))
    "Cell 7 Heading" -> listOf(processParagraph(tag = "h3"))
    "Blocked Text" -> listOf(processParagraph(tag = "p", attributes = mapOf("class" to "left-justified")))
    "Centered Text" -> listOf(processParagraph(tag = "p", attributes = mapOf("class" to "centered")))
    else -> listOf(processParagraph())
}

private fun Element.processParagraph(
    tag: String = "p",
    attributes: Map<String, String> = mapOf()
): org.jsoup.nodes.Element = org.jsoup.nodes.Element(tag).apply {
    for ((k, v) in attributes) {
        attr(k, v)
    }
}.appendChildren(childNodes.flatMap { it.processContent() })