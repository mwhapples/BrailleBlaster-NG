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
package org.brailleblaster.perspectives.braille.messages

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.utd.properties.Align

open class AdjustLocalStyleMessage(e: Element) : Message(BBEvent.ADJUST_LOCAL_STYLE) {
    class AdjustAlignmentMessage(e: Element, val alignment: Align) :
        AdjustLocalStyleMessage(e)

    class AdjustPageMessage(
        e: Element,
        val newPagesBefore: Int,
        val newPagesAfter: Int
    ) : AdjustLocalStyleMessage(e)

    class AdjustLinesMessage(e: Element, val linesBefore: Boolean, val lines: Int) :
        AdjustLocalStyleMessage(e)

    class AdjustMarginMessage(e: Element, val margin: Int) :
        AdjustLocalStyleMessage(e)

    class AdjustIndentMessage(e: Element, val indent: Int, val line: Int) :
        AdjustLocalStyleMessage(e)

    private val indexes: List<Int> = makeIndexes(e)

    fun getElement(manager: Manager): Element? {
        return getParent(manager, indexes)
    }

    private fun getParent(manager: Manager, indexes: List<Int>): Element? {
        var e = if (indexes.isNotEmpty()) manager.document.doc.document.getChild(indexes[0]) as Element else null
        for (i in 1 until indexes.size) e = e!!.getChild(indexes[i]) as Element
        return e
    }

    companion object {
        private fun makeIndexes(n: Node): ArrayList<Int> {
            var child: Node? = n
            val list = ArrayList<Int>()
            var parent = child!!.parent
            do {
                list.add(0, parent!!.indexOf(child))
                child = parent
                parent = parent.parent
            } while (parent != null)
            return list
        }

        @JvmStatic
        fun adjustPages(e: Element, newPageBefore: Int, newPageAfter: Int): AdjustLocalStyleMessage {
            return AdjustPageMessage(e, newPageBefore, newPageAfter)
        }
    }
}
