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
package org.brailleblaster.perspectives.braille.mapping.elements

import nu.xom.Element
import nu.xom.ParentNode
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.interfaces.Deletable
import org.brailleblaster.perspectives.braille.messages.AdjustLocalStyleMessage.Companion.adjustPages
import org.brailleblaster.tools.PageBreakTool.changePagesOfTable

/**
 * PageBreakWhiteSpace occurs when the formatter forced a new page to be
 * created, either through a newPagesBefore style option or through
 * DontSplit or KeepWithNext
 */
class PageBreakWhiteSpaceElement : FormattingWhiteSpaceElement, Deletable {
    constructor() : super()

    constructor(start: Int, end: Int) : super(start, end)

    override fun deleteNode(m: Manager): ParentNode? {
        val pbIndex = m.mapList.indexOf(this)
        require(pbIndex != -1) { "PageBreakWhiteSpaceElement not in MapList" }
        val prev = m.mapList.findPreviousNonWhitespace(pbIndex)
        val next = m.mapList.findNextNonWhitespace(pbIndex)


        if (prev != null) {
            val style = m.getStyle(findParent(prev, m)!!)
            if (style != null && style.newPagesAfter > 0) {
                if (prev is TableTextMapElement || prev is ReadOnlyTableTextMapElement) {
                    changePagesOfTable(Manager.getTableParent(prev.node), style.newPagesBefore, style.newPagesAfter - 1, m)
                } else {
                    m.dispatch(adjustPages(findParent(prev, m)!!, style.newPagesBefore, style.newPagesAfter - 1))
                }
            }
        }

        if (next != null) {
            val style = m.getStyle(findParent(next, m)!!)
            if (style != null && style.newPagesBefore > 0) {
                if (next is TableTextMapElement || next is ReadOnlyTableTextMapElement) {
                    changePagesOfTable(Manager.getTableParent(next.node), style.newPagesBefore - 1, style.newPagesAfter, m)
                } else {
                    m.dispatch(adjustPages(findParent(next, m)!!, style.newPagesBefore - 1, style.newPagesAfter))
                }
            }
        }
        return null
    }

    private fun findParent(t: TextMapElement, m: Manager): Element? {
        if (t is ReadOnlyTableTextMapElement) return Manager.getTableParent(t.node)
        return if (t is BoxLineTextMapElement) t.nodeParent else m.document.getParent(t.node)
    }
}
