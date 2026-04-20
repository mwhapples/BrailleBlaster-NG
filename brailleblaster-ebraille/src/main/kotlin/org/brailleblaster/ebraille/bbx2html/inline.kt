/*
 * Copyright (C) 2026 American Printing House for the Blind
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
import nu.xom.Node
import org.brailleblaster.bbx.BBX
import org.brailleblaster.ebraille.asciiToEbraille
import org.brailleblaster.utd.properties.UTDElements
import org.brailleblaster.utils.xml.BB_NS
import org.brailleblaster.utils.xom.childNodes

internal fun Element.processContent(): Collection<org.jsoup.nodes.Node> = when {
    UTDElements.BRL.isA(this) -> listOf(org.jsoup.nodes.TextNode(asciiToEbraille(this.value)))
    BBX.SPAN.PAGE_NUM.isA(this) -> listOf(this.processPageNum())
    BBX.INLINE.EMPHASIS.isA(this) -> listOf(this.processEmphasis())
    else -> childNodes.flatMap { it.processContent() }
}

internal fun Node.processContent(): Collection<org.jsoup.nodes.Node> = when(this) {
    is Element -> this.processContent()
    else -> listOf()
}

private fun Element.processEmphasis(): org.jsoup.nodes.Node = when(getAttributeValue("emphasis", BB_NS)) {
    "BOLD" -> org.jsoup.nodes.Element("strong")
    "ITALICS" -> org.jsoup.nodes.Element("em")
    "UNDERLINE" -> org.jsoup.nodes.Element("em").attr("class", "underline")
    "SCRIPT" -> org.jsoup.nodes.Element("em").attr("class", "script")
    "TRANS_1" -> org.jsoup.nodes.Element("em").attr("class", "trans1")
    "TRANS_2" -> org.jsoup.nodes.Element("em").attr("class", "trans2")
    "TRANS_3" -> org.jsoup.nodes.Element("em").attr("class", "trans3")
    "TRANS_4" -> org.jsoup.nodes.Element("em").attr("class", "trans4")
    "TRANS_5" -> org.jsoup.nodes.Element("em").attr("class", "trans5")
    else -> org.jsoup.nodes.Element("em")
}.appendChildren(childNodes.flatMap { it.processContent() })