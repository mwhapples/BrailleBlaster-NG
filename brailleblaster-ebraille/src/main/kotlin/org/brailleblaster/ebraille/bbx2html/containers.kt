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
import org.brailleblaster.ebraille.ListItem
import org.brailleblaster.ebraille.toHtml
import org.brailleblaster.utils.xml.BB_NS
import org.brailleblaster.utils.xml.UTD_NS
import org.brailleblaster.utils.xom.childNodes
import org.jsoup.nodes.Node

internal fun Element.processContainer(): Collection<Node> = when (BBX.CONTAINER.getSubType(this)) {
    BBX.CONTAINER.BOX -> listOf(processBox())
    BBX.CONTAINER.LIST -> listOf(processList())
    BBX.CONTAINER.TABLE -> processTable()
    else -> processChildren()
}

private fun Element.processBox(): org.jsoup.nodes.Element {
    val boxSymbol = when (style) {
        "Full Box" -> "\u283f"
        else -> "\u2836"
    }
    return org.jsoup.nodes.Element("div").attr("type", boxSymbol).appendChildren(processChildren())
}

private fun Element.processList(): org.jsoup.nodes.Element =
    childElements.filter { BBX.BLOCK.LIST_ITEM.isA(it) }
        .map { ListItem(it, it.getAttributeValue("itemLevel", BB_NS)?.toIntOrNull() ?: 0) }.toHtml(
            level = 0,
            containerFactory = { org.jsoup.nodes.Element("ul").attr("style", "list-style-type: none") }
        ) { it.element.processBlock() }

private fun Element.processTable(): List<org.jsoup.nodes.Element> = if (getAttributeValue("tableCopy", UTD_NS) == "true") {
    listOf()
} else {
    listOf(org.jsoup.nodes.Element("table").also {
        val tableFormat = getAttributeValue("format")
        if (tableFormat in listOf("listed", "stairstep", "linear")) {
            it.attr("class", tableFormat)
        }
    }.appendChildren(childElements.filter { BBX.CONTAINER.TABLE_ROW.isA(it) }.map { r -> org.jsoup.nodes.Element("tr").appendChildren(r.childElements.filter { BBX.BLOCK.TABLE_CELL.isA(it) }.map { c -> org.jsoup.nodes.Element("td").appendChildren(c.processContent())}) }))
}