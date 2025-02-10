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

import nu.xom.Comment
import nu.xom.Element
import nu.xom.Node
import nu.xom.Text
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.parsers.ImportParser.OldDocumentAction
import org.brailleblaster.utd.exceptions.NodeException
import org.brailleblaster.utils.xom.childNodes

/**
 * Handle head element by copying contents to new document. Don't need any special parsing
 * as UTD ignores it anyway
 */
@Suppress("UNUSED")
class HeadImportParser : ImportParser {
    override fun parseToBBX(oldNode: Node, bbxCursor: Element): OldDocumentAction {
        val oldHead = failIfNotElement(oldNode)
        val newhead = BBX.getHead(bbxCursor.document)
        initNewElementAttributes(oldHead, newhead)
        for (curOldHeadChild in oldHead.childNodes) {
            if (curOldHeadChild is Comment) {
                newhead.appendChild(Comment(curOldHeadChild))
                continue
            } else if (curOldHeadChild is Text && curOldHeadChild.value.isBlank()) {
                //ignore whitespace
                continue
            } else if (curOldHeadChild !is Element) {
                throw NodeException("Unexpected non-element in head", curOldHeadChild)
            }
            val newElem = Element(curOldHeadChild)
            newhead.appendChild(newElem)
        }
        return OldDocumentAction.NEXT_SIBLING
    }
}