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
package org.brailleblaster.bbx.parsers

import nu.xom.*
import org.brailleblaster.bbx.parsers.ImportParser.OldDocumentAction
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.matchers.INodeMatcher

@Suppress("UNUSED")
class LeafImportParser : ImportParser {
    override fun parseToBBX(oldNode: Node, bbxCursor: Element): OldDocumentAction {
        bbxCursor.appendChild(oldNode.copy())
        return OldDocumentAction.NEXT_SIBLING
    }

    class Matcher : INodeMatcher {
        override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
            return (node is Text
                    || node is Comment
                    || node is ProcessingInstruction)
        }
    }
}