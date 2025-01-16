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
package org.brailleblaster.utd.actions

import nu.xom.Node
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan
import org.brailleblaster.utd.properties.UTDElements

/**
 * This action adds an empty <brl></brl> element after the passed node and sets the span corresponding
 * that node to setTranslated(true).
 */
class FlushAction : GenericAction(), IAction {
    override fun applyTo(node: Node, context: ITranslationEngine): List<TextSpan> {
        // Catch on whether or not to insert brl - temp
        if (node.childCount == 0) {
            insertBrl(node)
        }
        val nodeSpan = TextSpan(node, "")
        nodeSpan.isTranslated = true
        val span: MutableList<TextSpan> = ArrayList()
        span.add(nodeSpan)
        return span
    }

    private fun insertBrl(node: Node) {
        // Adjust node to have <brl> after
        val brl = UTDElements.BRL.create()
        val parent = node.parent
        parent.insertChild(brl, parent.indexOf(node) + 1)
    }
}