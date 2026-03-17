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
package org.brailleblaster.ebraille

import org.jsoup.nodes.Element

internal data class ListItem<T>(val element: T, val level: Int)

internal fun <T> Iterable<ListItem<T>>.toHtml(
    level: Int,
    containerFactory: () -> Element,
    itemFactory: (ListItem<T>) -> Collection<Element>
): Element =
    containerFactory().also {
        val iter = iterator()
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
                val li = it.children().lastOrNull() ?: (Element("li").appendTo(it))
                li.appendChild(
                    subItems.toHtml(
                        level = level + 1,
                        containerFactory = containerFactory,
                        itemFactory = itemFactory
                    )
                )
            }
            if (appendItem) {
                it.appendChildren(itemFactory(item))
            }
        }
    }