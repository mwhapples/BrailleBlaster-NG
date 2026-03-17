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

private data class ListItem<T>(val element: T, val level: Int)

private fun <T> processListItems(items: List<ListItem<T>>, level: Int, containerFactory: () -> org.jsoup.nodes.Element, itemFactory: (ListItem<T>) -> Collection<org.jsoup.nodes.Element>): org.jsoup.nodes.Element =
    containerFactory().apply {
        val iter = items.listIterator()
        while (iter.hasNext()) {
            val subItems = mutableListOf<ListItem<T>>()
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
                val li = children().lastOrNull() ?: (org.jsoup.nodes.Element("li").appendTo(this))
                li.appendChild(processListItems(subItems, level = level + 1, containerFactory = containerFactory, itemFactory = itemFactory))
            }
            if (appendItem) {
                appendChildren(itemFactory(item))
            }
        }
    }

private fun Element.processList(): org.jsoup.nodes.Element =
    processListItems(childElements.filter { BBX.BLOCK.LIST_ITEM.isA(it) }
        .map { ListItem(it, it.getAttributeValue("itemLevel", BB_NS)?.toIntOrNull() ?: 0) }, level = 0, containerFactory = { org.jsoup.nodes.Element("ul").attr("style", "list-style-type: none") }, itemFactory = { it.element.processBlock() })