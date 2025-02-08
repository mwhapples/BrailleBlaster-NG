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
package org.brailleblaster.bbx.fixers.to3

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.fixers.AbstractFixer
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.matchers.INodeMatcher

/**
 * Issue #5483
 */
@Suppress("UNUSED")
class NoteSeparationLineImportFixer : AbstractFixer() {
    override fun fix(matchedNode: Node) {
        BBX._ATTRIB_OVERRIDE_STYLE[matchedNode as Element] = "Note Separation Line"
    }

    class Matcher : INodeMatcher {
        override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
            return (node is Element
                    && BBX.BLOCK.STYLE.isA(node)
                    && BBX._ATTRIB_OVERRIDE_STYLE.has(node)) && "noteSep" == BBX._ATTRIB_OVERRIDE_STYLE[node]
        }
    }
}