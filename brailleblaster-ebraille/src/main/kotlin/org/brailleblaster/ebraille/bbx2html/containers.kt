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
package org.brailleblaster.ebraille.bbx2html

import nu.xom.Element
import org.brailleblaster.bbx.BBX
import org.brailleblaster.utils.xml.BB_NS
import org.jsoup.nodes.Node

internal fun Element.processContainer(): Collection<Node> = when (BBX.CONTAINER.getSubType(this)) {
    BBX.CONTAINER.BOX -> listOf(processBox())
    BBX.CONTAINER.LIST -> listOf(processList())
    else -> processChildren()
}

private fun Element.processBox(): org.jsoup.nodes.Element {
    val boxSymbol = when (style) {
        "Full Box" -> "\u283f"
        else -> "\u2836"
    }
    return org.jsoup.nodes.Element("div").attr("type", boxSymbol).appendChildren(processChildren())
}

private data class ListItem(val element: Element, val level: Int)

private fun processListItems(items: List<ListItem>, level: Int = 0): org.jsoup.nodes.Element =
    org.jsoup.nodes.Element("ul").apply {
        val iter = items.listIterator()
        while (iter.hasNext()) {
            val subItems = mutableListOf<ListItem>()
            var appendItem = true
            var item = iter.next()
            while (item.level > level) {
                subItems.add(item)
                if (iter.hasNext()) {
                    item = iter.next()
                } else {
                    appendItem = false
                    break
                }
            }
            if (subItems.isNotEmpty()) {
                appendChild(org.jsoup.nodes.Element("li").appendChild(processListItems(subItems, level + 1)))
            }
            if (appendItem) {
                appendChildren(item.element.processBlock())
            }
        }
    }

private fun Element.processList(): org.jsoup.nodes.Element =
    processListItems(childElements.filter { BBX.BLOCK.LIST_ITEM.isA(it) }
        .map { ListItem(it, it.getAttributeValue("itemLevel", BB_NS)?.toIntOrNull() ?: 0) })