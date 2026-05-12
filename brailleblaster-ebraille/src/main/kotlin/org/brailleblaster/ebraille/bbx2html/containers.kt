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
import org.jsoup.nodes.Node

internal fun Element.processContainer(): Collection<Node> = when (BBX.CONTAINER.getSubType(this)) {
    BBX.CONTAINER.BOX -> listOf(processBox())
    BBX.CONTAINER.LIST -> listOf(processList())
    BBX.CONTAINER.TABLETN -> listOf()
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
    val tableFormat = getAttributeValue("format")
    listOf(org.jsoup.nodes.Element("table").also {
        if (tableFormat in listOf("listed", "stairstep", "linear")) {
            it.attr("class", tableFormat)
        }
    }.appendChildren(
        childElements.filter { BBX.CONTAINER.TABLE_ROW.isA(it) }.take(1).let { firstRow ->
            when (tableFormat) {
                "simple" -> {
                    firstRow.processTableRow(if (getAttributeValue("columnHeading") == "false") "td" else "th")
                }
                "listed" -> {
                    firstRow.processTableRow("th")
                }
                else -> {
                    firstRow.processTableRow()
                }
            }
        } + (childElements.filter { BBX.CONTAINER.TABLE_ROW.isA(it) }.drop(1).processTableRow())
    ))
}

private fun Iterable<Element>.processTableRow(cellTag: String = "td"): List<org.jsoup.nodes.Element> = map { r -> org.jsoup.nodes.Element("tr").appendChildren(r.childElements.filter { BBX.BLOCK.TABLE_CELL.isA(it) }.map { c -> org.jsoup.nodes.Element(cellTag).appendChildren(c.processContent())}) }