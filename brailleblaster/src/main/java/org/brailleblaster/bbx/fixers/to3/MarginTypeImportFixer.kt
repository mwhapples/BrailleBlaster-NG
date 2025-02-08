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
import org.brailleblaster.utd.toc.TOCAttributes

@Suppress("UNUSED")
class MarginTypeImportFixer : AbstractFixer() {
    override fun fix(matchedNode: Node) {
        BBX.BLOCK.MARGIN.assertIsA(matchedNode)
        val element = matchedNode as Element
        if (TOCAttributes.TYPE.getAttribute(element) != null) {
            BBX.BLOCK.MARGIN.ATTRIB_MARGIN_TYPE[element] = BBX.MarginType.TOC
        } else {
            BBX.BLOCK.MARGIN.ATTRIB_MARGIN_TYPE[element] = BBX.MarginType.NUMERIC
        }
    }

    @Suppress("UNUSED")
    class NoMarginTypeMatcher : INodeMatcher {
        override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
            return !BBX.BLOCK.MARGIN.ATTRIB_MARGIN_TYPE.has(node)
        }
    }
}