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
package org.brailleblaster.bbx.fixers

import nu.xom.Node
import org.brailleblaster.bbx.BBX
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.internal.xml.XMLHandler
import org.brailleblaster.utd.matchers.INodeMatcher

/**
 * Match nested
 *
 * inside <dd></dd>
 */
@Suppress("UNUSED")
class DefinitionMultipleBlockMatcher : INodeMatcher {
    override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
        if (!BBX.BLOCK.isA(node)) {
            return false
        }
        val previousSiblingNode = XMLHandler.previousSiblingNode(node) ?: return false
        return BBX.SPAN.DEFINITION_TERM.isA(previousSiblingNode)
    }
}