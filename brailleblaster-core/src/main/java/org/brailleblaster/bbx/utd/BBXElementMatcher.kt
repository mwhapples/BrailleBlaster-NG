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

import jakarta.xml.bind.Unmarshaller
import jakarta.xml.bind.annotation.XmlAttribute
import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.AbstractBBXUtil
import org.brailleblaster.bbx.BBX.*
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.matchers.INodeMatcher
import org.brailleblaster.utd.properties.EmphasisType

class BBXElementMatcher : AbstractBBXUtil, INodeMatcher {
    @XmlAttribute
    private val coreType: CoreType?

    @Suppress("UNUSED")
    constructor() {
        coreType = null
    }

    @Suppress("UNUSED")
    constructor(
        coreType: CoreType?,
        sectionType: SectionSubType?,
        containerType: ContainerSubType?,
        blockType: BlockSubType?,
        inlineType: InlineSubType?,
        spanType: SpanSubType?,
        listType: ListType?,
        marginType: MarginType?,
        tableRowType: TableRowType?,
        emphasisType: EmphasisType?,
        fixerTodo: FixerTodo?
    ) : super(
        coreType,
        sectionType,
        containerType,
        blockType,
        inlineType,
        spanType,
        listType,
        marginType,
        tableRowType,
        emphasisType,
        fixerTodo
    ) {
        this.coreType = coreType
    }

    override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
        if (node !is Element) return false
        if (coreType != null) {
            return coreType.isA(node)
        } else if (sectionType != null) {
            return (SECTION.isA(node)
                    && SECTION.getSubType(node) === sectionType)
        } else if (containerType != null) {
            return (CONTAINER.isA(node)
                    && CONTAINER.getSubType(node) === containerType)
        } else if (listType != null) {
            return (CONTAINER.LIST.ATTRIB_LIST_TYPE.has(node)
                    && CONTAINER.LIST.ATTRIB_LIST_TYPE[node] == listType)
        } else if (tableRowType != null) {
            return (CONTAINER.TABLE_ROW.ATTRIB_ROW_TYPE.has(node)
                    && CONTAINER.TABLE_ROW.ATTRIB_ROW_TYPE[node] == tableRowType)
        } else if (blockType != null) {
            return (BLOCK.isA(node)
                    && BLOCK.getSubType(node) === blockType)
        } else if (inlineType != null) {
            return (INLINE.isA(node)
                    && INLINE.getSubType(node) === inlineType)
        } else if (spanType != null) {
            return (SPAN.isA(node)
                    && SPAN.getSubType(node) === spanType)
        } else if (emphasisType != null) {
            return (INLINE.EMPHASIS.isA(node)
                    && INLINE.EMPHASIS.ATTRIB_EMPHASIS[node].contains(emphasisType))
        } else if (fixerTodo != null) {
            return (_ATTRIB_FIXER_TODO.has(node)
                    && _ATTRIB_FIXER_TODO[node] == fixerTodo)
        } else if (marginType != null) {
            return (BLOCK.MARGIN.ATTRIB_MARGIN_TYPE.has(node)
                    && BLOCK.MARGIN.ATTRIB_MARGIN_TYPE[node] == marginType)
        }
        return false
    }

    @Suppress("UNUSED", "UNUSED_PARAMETER")
    fun afterUnmarshal(unmarshaller: Unmarshaller?, parent: Any?) {
        validateOnlyOneBBXFieldSet(coreType, fixerTodo)
    }
}