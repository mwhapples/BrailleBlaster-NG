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

import nu.xom.Attribute
import nu.xom.Element
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.utd.BBXDynamicOptionStyleMap
import org.brailleblaster.utd.Style.StyleOption
import org.brailleblaster.utils.xml.BB_NS
import org.brailleblaster.utils.xml.UTD_NS

/**
 * A Word table adds no blank lines of its own, and a list before it adds
 * none toward it, so a heading next to the table is the only spacing left.
 * A list after it adds none only when an empty paragraph (ASM's spacer)
 * came between them; a list straight after a table, like a footnote, keeps
 * its blank line. The table, its UTD table copy and those lists get line
 * style options for that, one line meaning no blank line. Run after
 * FixNestedList so the lists are already merged, and [MarkTableSpacers]
 * before it.
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
                    ?.takeIf { BBX.CONTAINER.LIST.isA(it) && hasSpacerAfter(table) }
                    ?.let { list ->
                        setLines(list, StyleOption.LINES_BEFORE)
                        list.childElements.firstOrNull(BBX.BLOCK.LIST_ITEM::isA)
                            ?.let { setLines(it, StyleOption.LINES_BEFORE) }
                    }
                table.getAttribute(SPACER_AFTER, BB_NS)?.let(table::removeAttribute)
            }
    }

    private fun hasSpacerAfter(table: Element) = table.getAttribute(SPACER_AFTER, BB_NS) != null

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

    companion object {
        internal const val SPACER_AFTER = "spacerAfter"
    }
}

/**
 * Removes each `<bb:emptyParagraph/>` that bbx.lua writes for an empty Word
 * paragraph (see docx-empty-paragraph.lua), first marking a table it follows
 * for [FixTableSpacing]. Run before FixNestedList, so the markers don't keep
 * lists apart.
 */
class MarkTableSpacers : FixerInf {
    private lateinit var rootElem: Element

    override fun setFixer(fixer: Fixer) {
        rootElem = fixer.rootElement
    }

    override fun process() {
        descendants(rootElem)
            .filter(::isSpacer)
            .toList()
            .forEach { spacer ->
                previousTable(spacer)?.addAttribute(
                    Attribute("bb:${FixTableSpacing.SPACER_AFTER}", BB_NS, "true")
                )
                spacer.detach()
            }
    }

    private fun descendants(e: Element): Sequence<Element> =
        sequenceOf(e) + e.childElements.asSequence().flatMap(::descendants)

    private fun isSpacer(e: Element) = e.localName == "emptyParagraph" && e.namespaceURI == BB_NS

    // The table before the spacer, past any other spacers and its UTD table copy
    private fun previousTable(spacer: Element): Element? =
        previousElements(spacer)
            .dropWhile { isSpacer(it) || it.getAttribute("tableCopy", UTD_NS) != null }
            .firstOrNull()
            ?.takeIf(BBX.CONTAINER.TABLE::isA)

    private fun previousElements(e: Element): Sequence<Element> {
        val parent = e.parent ?: return emptySequence()
        return (parent.indexOf(e) - 1 downTo 0).asSequence()
            .map(parent::getChild)
            .filterIsInstance<Element>()
    }
}
