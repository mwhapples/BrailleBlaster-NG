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

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.parsers.ImportParser.OldDocumentAction
import org.brailleblaster.utd.internal.xml.XMLHandler

/**
 * In production BBX can't be as strict
 */
@Suppress("UNUSED")
class FallbackImportParser : ImportParser {
    override fun parseToBBX(oldNode: Node, bbxCursor: Element): OldDocumentAction {
        val oldElem = failIfNotElement(oldNode)
        val newElem: Element = if (XMLHandler.ancestorElementIs(bbxCursor) { curAncestor: Element ->
                if (curAncestor === curAncestor.document.rootElement) {
                    return@ancestorElementIs false
                }
                val cursorType = BBX.getType(curAncestor)
                cursorType === BBX.BLOCK || cursorType === BBX.SPAN
            }) {
            BBX.SPAN.FALLBACK.create()
        } else {
            BBX.CONTAINER.FALLBACK.create()
        }
        initNewElementAttributes(oldElem, newElem)
        bbxCursor.appendChild(newElem)
        return OldDocumentAction.DESCEND
    }
}