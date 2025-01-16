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
package org.brailleblaster.utd.matchers

import jakarta.xml.bind.annotation.XmlElement
import nu.xom.Element
import nu.xom.Node
import nu.xom.Text
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.properties.UTDElements

/**
 * Check whether the previous sibling matches certain criteria.
 * 
 * This matcher uses a delegate matcher to test whether the previous sibling matches certain criteria. It will only examine the immediate previous sibling. In the event that there is no immediate previous sibling, due to it being the first child of its parent, then no match will be given.
 */
class PreviousSiblingMatcher : DelegatingMatcher() {
    @get:XmlElement(name = "ignoreWhiteSpaceNodes")
    var isIgnoreWhiteSpace: Boolean = false
    override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
        if (matcher == null) {
            return true
        }
        val parent = node.parent ?: return false
        var index = parent.indexOf(node)
        var previousSibling: Node
        do {
            index--
            if (index < 0) {
                return false
            }
            previousSibling = parent.getChild(index)
        } while (isIgnorableNode(previousSibling))
        return matcher!!.isMatch(previousSibling, namespaces)
    }

    private fun isIgnorableNode(node: Node?): Boolean {
        if (node is Text) {
            return isIgnoreWhiteSpace && node.getValue().isEmpty()
        }
        return node !is Element || UTDElements.BRL.isA(node)
    }
}
