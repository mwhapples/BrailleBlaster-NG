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
package org.brailleblaster.utd.matchers

import jakarta.xml.bind.annotation.XmlAttribute
import nu.xom.Element
import nu.xom.Node
import nu.xom.Text
import org.brailleblaster.utd.NamespaceMap

/**
 * This class is to match instances of emphasis that should be skipped, such as ** within a
 * heading. If true, it will match to generic block action.
 ** */
@Suppress("UNUSED")
class EmphasisMatcher : INodeMatcher {
    private var nodeNameParts // any emphasis element
            : List<String> = emptyList()
    private var headingsList // any heading element
            : List<String> = emptyList()
    private var headingAncestor = false
    override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
        headingAncestor = false
        return if (node is Element) {
            for (nodeName in nodeNameParts) {
                // we need to check span separately because it might map to
                // something besides underline
                if (nodeName == "span") {
                    if (spanCheck(node)) {
                        return checkHeadings(node)
                    }
                } else if (node.localName != nodeName) {
                    // node doesn't match this possible node name, continue
                    // to check the others.
                    continue
                } else if (node.localName == nodeName) {
                    // node name is one of our possible nodes, send it down
                    // the line of checks
                    return checkHeadings(node)
                }
            }
            // node is an element, but it is not one of our nodeNames that we
            // need to check
            false
        } else {
            // Not an element, don't format as emphasis
            false
        }
    }

    private fun spanCheck(ele: Element): Boolean {
        return ele.localName == "span" && ele.getAttributeValue("underline") != null
    }

    private fun checkHeadings(originalNode: Node): Boolean {
        var node: Node? = originalNode
        // first we need to see if the emphasis has an ancestor of a
        // heading
        while (node != null && !headingAncestor) {
            if (node is Element) {
                val localName = node.localName
                headingAncestor = headingsList.any { localName == it }
            }
            node = node.parent
        }
        return headingAncestor && onlySibling(originalNode)
        // we know that the emphasis has a heading as an ancestor
        // now we need to see if this is the only instance of
        // a text child
    }

    private fun onlySibling(node: Node?): Boolean {
        // get the parent and check all other siblings for instances of text
        val parent: Node = node!!.parent
        var textChildren = 0
        for (i in 0 until parent.childCount) {
            textChildren += countTextChildren(parent.getChild(i), 0)
        }
        return textChildren == 1
    }

    private fun countTextChildren(node: Node, textChildren: Int): Int {
        // recursively count all children of children to account for nested
        // actions and styles
        // that might contain text node children
        return (0 until node.childCount).count { node.getChild(it) is Text } + textChildren
    }

    @set:XmlAttribute
    var nodeName: String
    get() = nodeNameParts.joinToString("|")
    set(value) {
        nodeNameParts = value.split('|')
    }

    @set:XmlAttribute
    var headings: String
    get() = headingsList.joinToString("|")
    set(value) {
        headingsList = value.split('|')
    }
}