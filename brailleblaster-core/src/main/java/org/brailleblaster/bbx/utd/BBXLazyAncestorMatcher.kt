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
package org.brailleblaster.bbx.utd

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.BBX
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.matchers.DelegatingMatcher

/**
 * Alternative "Lazy" matcher vs the "greedy" AncestorMatcher. Will stop when
 * matcher level goes down while ascending.
 * Eg Text->Block->Container=good while Text->Container->Block=will stop at Container
 */
@Suppress("UNUSED")
class BBXLazyAncestorMatcher : DelegatingMatcher() {
    override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
        val matcher = matcher ?: return true
        var highestLevel = getLevel(node)
        var curParent = node.parent
        while (curParent != null) {
            val parentLevel = getLevel(curParent)
            if (parentLevel < highestLevel) {
                break
            }
            highestLevel = parentLevel
            if (matcher.isMatch(curParent, namespaces)) {
                return true
            }
            curParent = curParent.parent
        }
        return false
    }

    companion object {
        private fun getLevel(node: Node): Int {
            return if (BBX.isA(node)) {
                // for our purposes, treat inline and span as the same
                val type = BBX.getType(node as Element)
                if (type === BBX.SPAN || type === BBX.INLINE) {
                    0
                } else if (type === BBX.BLOCK) {
                    1
                } else if (type === BBX.CONTAINER) {
                    2
                } else if (type === BBX.SECTION) {
                    3
                } else {
                    // unknown element
                    -1
                }
            } else {
                // eg text, comments, other stuff
                -1
            }
        }
    }
}