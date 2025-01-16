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
import org.brailleblaster.bbx.BBX.ListType
import org.brailleblaster.bbx.BBXUtils
import org.brailleblaster.bbx.BBXUtils.ListStyleData
import org.brailleblaster.bbx.fixers.AbstractFixer
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.exceptions.NodeException
import org.brailleblaster.utd.internal.xml.XMLHandler
import org.brailleblaster.utd.matchers.INodeMatcher

@Suppress("UNUSED")
class MarginImportFixer : AbstractFixer() {
    override fun fix(matchedNode: Node) {
        BBX.BLOCK.assertIsA(matchedNode)
        val element = matchedNode as Element
        val styleData = BBXUtils.parseListStyle(BBX._ATTRIB_OVERRIDE_STYLE[element])
            ?: throw NodeException("Node matches but styleData is null?", matchedNode)
        BBXUtils.stripStyleExceptOverrideStyle(element)
        BBX._ATTRIB_OVERRIDE_STYLE.detach(element)
        if (styleData.listType != null) {
            //parent list matches, just need to mutate into a list item
            BBXUtils.stripStyleExceptOverrideStyle(element)
            BBX.transform(element, BBX.BLOCK.LIST_ITEM)
            BBX.BLOCK.LIST_ITEM.ATTRIB_ITEM_LEVEL[element] = styleData.indentLevel
        } else if (styleData.marginType != null) {
            BBX.transform(element, BBX.BLOCK.MARGIN)
            BBX.BLOCK.MARGIN.ATTRIB_MARGIN_TYPE[element] = styleData.marginType
            BBX.BLOCK.MARGIN.ATTRIB_INDENT[element] = styleData.indentLevel
            BBX.BLOCK.MARGIN.ATTRIB_RUNOVER[element] = styleData.runoverLevel
        } else {
            throw NodeException("styleData with no marginType or listType? $styleData", matchedNode)
        }
    }

    class Matcher : INodeMatcher {
        override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
            if (!BBX.BLOCK.isA(node)) {
                return false
            }
            val element = node as Element
            if (!BBX._ATTRIB_OVERRIDE_STYLE.has(element)) {
                return false
            }
            val styleData: ListStyleData? = BBXUtils.parseListStyle(BBX._ATTRIB_OVERRIDE_STYLE[element])
            if (styleData == null) {
                return false
            } else if (styleData.listType != null) {
                val parentWithListLevel = XMLHandler.ancestorVisitorElement(
                    element
                ) { BBX.CONTAINER.LIST.ATTRIB_LIST_LEVEL.has(it) }
                    ?: //not found for some reason... do nothing to not break or change the formatting
                    return false
                val listType = BBX.CONTAINER.LIST.ATTRIB_LIST_TYPE[parentWithListLevel]
                if (listType != styleData.listType
                    && !((listType == ListType.POEM || listType == ListType.POEM_LINE_GROUP)
                            && (styleData.listType == ListType.POEM || styleData.listType == ListType.POEM_LINE_GROUP))
                ) {
                    return false
                }
                val origListLevel = BBX.CONTAINER.LIST.ATTRIB_LIST_LEVEL[parentWithListLevel]
                if (origListLevel != styleData.runoverLevel) {
                    //do not break existing list
                    return false
                }
            } else if (styleData.marginType == null) {
                throw NodeException("styleData with no marginType or listType? $styleData", element)
            }
            return true
        }
    }
}