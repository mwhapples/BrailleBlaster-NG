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

import jakarta.xml.bind.Unmarshaller
import jakarta.xml.bind.annotation.XmlAttribute
import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.AbstractBBXUtil
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.BookToBBXConverter
import org.brailleblaster.bbx.parsers.ImportParser.OldDocumentAction
import org.brailleblaster.utd.exceptions.NodeException

@Suppress("UNUSED")
class BBXImportParser : AbstractBBXUtil, ImportParser {
    @XmlAttribute
    private val noChildrenValid: Boolean

    @XmlAttribute
    private val styleName: String?

    @Suppress("UNUSED")
    private constructor() {
        //for jaxb
        noChildrenValid = false
        styleName = null
    }

    @Suppress("UNUSED")
    constructor(noChildrenValid: Boolean, styleName: String?) {
        this.noChildrenValid = noChildrenValid
        this.styleName = styleName
    }

    override fun parseToBBX(oldNode: Node, bbxCursor: Element): OldDocumentAction {
        val oldElem = failIfNotElement(oldNode)
        val bbxElem: Element = if (sectionType != null) {
            sectionType.create()
        } else if (containerType != null) {
            if (styleName != null) {
                BBX.CONTAINER.STYLE.create(styleName)
            } else {
                containerType.create()
            }
        } else if (blockType != null) {
            if (styleName != null) {
                BBX.BLOCK.STYLE.create(styleName)
            } else {
                blockType.create()
            }
        } else if (spanType != null) {
            spanType.create()
        } else if (inlineType != null) {
            inlineType.create()
        } //subtype-types
        else if (listType != null) {
            BBX.CONTAINER.LIST.create(listType)
        } else if (tableRowType != null) {
            BBX.CONTAINER.TABLE_ROW.create(tableRowType)
        } else if (emphasisType != null) {
            BBX.INLINE.EMPHASIS.create(emphasisType)
        } else {
            throw RuntimeException(
                "Unhandled: " + oldNode
                        + " | bbxCursor: " + bbxCursor
                        + " | this:" + this
            )
        }
        initNewElementAttributes(oldElem, bbxElem)
        if (fixerTodo != null) {
            BBX._ATTRIB_FIXER_TODO[bbxElem] = fixerTodo
        }
        if (styleName != null) {
            BBX._ATTRIB_OVERRIDE_STYLE[bbxElem] = styleName
        }
        bbxCursor.appendChild(bbxElem)
        return if (oldElem.childCount == 0) {
            if (noChildrenValid) {
                OldDocumentAction.NEXT_SIBLING
            } else if (BookToBBXConverter.STRICT_MODE) {
                throw NodeException("No children", oldNode)
            } else {
                OldDocumentAction.NEXT_SIBLING
            }
        } else {
            OldDocumentAction.DESCEND
        }
    }

    @Suppress("UNUSED", "UNUSED_PARAMETER")
    fun afterUnmarshal(unmarshaller: Unmarshaller?, parent: Any?) {
        validateOnlyOneBBXFieldSet()
        if (styleName != null && (containerType !== BBX.CONTAINER.STYLE
                    && blockType !== BBX.BLOCK.STYLE)
        ) {
            throw RuntimeException("Unexpected styleName for non-style subtype: $this")
        } else if (containerType === BBX.CONTAINER.LIST) {
            throw RuntimeException("containerType==LIST, must set explicit listType field instead")
        } else if (containerType === BBX.CONTAINER.TABLE_ROW) {
            throw RuntimeException("containerType==TABLE_ROW, must set explicit tableRowType field instead")
        }
    }
}