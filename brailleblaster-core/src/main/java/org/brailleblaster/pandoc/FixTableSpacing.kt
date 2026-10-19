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
package org.brailleblaster.pandoc

import nu.xom.Element
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.utd.BBXDynamicOptionStyleMap
import org.brailleblaster.utd.Style.StyleOption
import org.brailleblaster.utils.xml.UTD_NS

/**
 * A Word table adds no blank lines of its own, and a list next to it adds
 * none toward it, so a heading next to the table is the only spacing left.
 * The table, its UTD table copy and the lists on either side get line style
 * options for that, one line meaning no blank line. Run after FixNestedList
 * so the lists are already merged.
 */
class FixTableSpacing : FixerInf {
    private lateinit var rootElem: Element

    override fun setFixer(fixer: Fixer) {
        rootElem = fixer.rootElement
    }

    override fun process() {
        descendants(rootElem)
            .filter(::isOuterTable)
            .toList()
            .forEach { table ->
                val copy = tableCopy(table)
                listOfNotNull(table, copy).forEach {
                    setLines(it, StyleOption.LINES_BEFORE)
                    setLines(it, StyleOption.LINES_AFTER)
                }
                // List items have their own lines before and after, as well as the list
                siblingElement(table, -1)
                    ?.takeIf(BBX.CONTAINER.LIST::isA)
                    ?.let { list ->
                        setLines(list, StyleOption.LINES_AFTER)
                        list.childElements.lastOrNull(BBX.BLOCK.LIST_ITEM::isA)
                            ?.let { setLines(it, StyleOption.LINES_AFTER) }
                    }
                siblingElement(copy ?: table, 1)
                    ?.takeIf(BBX.CONTAINER.LIST::isA)
                    ?.let { list ->
                        setLines(list, StyleOption.LINES_BEFORE)
                        list.childElements.firstOrNull(BBX.BLOCK.LIST_ITEM::isA)
                            ?.let { setLines(it, StyleOption.LINES_BEFORE) }
                    }
            }
    }

    private fun descendants(e: Element): Sequence<Element> =
        sequenceOf(e) + e.childElements.asSequence().flatMap(::descendants)

    private fun isOuterTable(e: Element) =
        BBX.CONTAINER.TABLE.isA(e) && !isTableCopy(e) &&
            generateSequence(e.parent as? Element) { it.parent as? Element }.none(BBX.CONTAINER.TABLE::isA)

    private fun isTableCopy(e: Element) = e.getAttribute("tableCopy", UTD_NS) != null

    private fun tableCopy(table: Element): Element? =
        siblingElement(table, 1)?.takeIf { BBX.CONTAINER.TABLE.isA(it) && isTableCopy(it) }

    private fun siblingElement(e: Element, step: Int): Element? {
        val parent = e.parent ?: return null
        return generateSequence(parent.indexOf(e) + step) { it + step }
            .takeWhile { it in 0 until parent.childCount }
            .map(parent::getChild)
            .filterIsInstance<Element>()
            .firstOrNull()
    }

    private fun setLines(e: Element, option: StyleOption) =
        BBXDynamicOptionStyleMap.setStyleOptionAttrib(e, option, 1)
}
