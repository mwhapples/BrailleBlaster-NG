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
import org.brailleblaster.utils.xml.BB_NS
import org.brailleblaster.utils.xml.UTD_NS

/**
 * Replaces each `<bb:pageBreak/>` written by bbx.lua with a new page before
 * the block that follows it, the same style option the Page Break tool sets.
 * A table gets the option on its container and its UTD table copy.
 */
class FixPageBreak : FixerInf {
    private lateinit var rootElem: Element

    override fun setFixer(fixer: Fixer) {
        rootElem = fixer.rootElement
    }

    override fun process() {
        val elements = descendants(rootElem).toList()
        elements.withIndex()
            .filter { (_, e) -> isPageBreak(e) }
            .forEach { (i, pageBreak) ->
                elements.drop(i + 1)
                    .firstOrNull { e -> BBX.BLOCK.isA(e) && e.parent != null }
                    ?.let(::breakTarget)
                    ?.forEach(::addPageBefore)
                pageBreak.detach()
            }
    }

    private fun descendants(e: Element): Sequence<Element> =
        sequenceOf(e) + e.childElements.asSequence().flatMap(::descendants)

    private fun isPageBreak(e: Element) = e.localName == "pageBreak" && e.namespaceURI == BB_NS

    private fun breakTarget(block: Element): List<Element> {
        if (!BBX.BLOCK.TABLE_CELL.isA(block)) return listOf(block)
        val table = generateSequence(block.parent as? Element) { it.parent as? Element }
            .lastOrNull { BBX.CONTAINER.TABLE.isA(it) } ?: return listOf(block)
        val parent = table.parent
        val copy = parent.indexOf(table).let { i -> if (i + 1 < parent.childCount) parent.getChild(i + 1) else null }
        return listOfNotNull(
            table,
            (copy as? Element)?.takeIf { it.getAttribute("tableCopy", UTD_NS) != null }
        )
    }

    private fun addPageBefore(e: Element) {
        val pages = e.getAttributeValue(PAGES_BEFORE_ATTRIB, BB_NS)?.toIntOrNull() ?: 0
        BBXDynamicOptionStyleMap.setStyleOptionAttrib(e, StyleOption.NEW_PAGES_BEFORE, pages + 1)
    }

    companion object {
        private val PAGES_BEFORE_ATTRIB =
            BBXDynamicOptionStyleMap.OPTION_ATTRIB_PREFIX + StyleOption.NEW_PAGES_BEFORE.optionName
    }
}
