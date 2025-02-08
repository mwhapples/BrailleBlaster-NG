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

import nu.xom.Node
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.matchers.DelegatingMatcher

@Suppress("UNUSED")
class AncestorMatcher : DelegatingMatcher() {
    override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
        val matcher = matcher
        return if (matcher == null) true else {
            var curParent = node.parent
            while (curParent != null) {
                if (matcher.isMatch(curParent, namespaces)) {
                    return true
                }
                curParent = curParent.parent
            }
            false
        }
    }
}