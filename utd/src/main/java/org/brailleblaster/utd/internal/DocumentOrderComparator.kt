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
package org.brailleblaster.utd.internal

import nu.xom.Node
import nu.xom.Nodes
import nu.xom.ParentNode
import java.io.Serializable
import java.lang.RuntimeException
import java.util.Comparator
import kotlin.math.min

/**
 * Comparator for determining the document order of nodes.
 *
 *
 * This comparator will compare two nodes and determine that a node is less if it appears before
 * the other node and greater if it appears after the other node. In the case that the node is an
 * ancestor of the other node it will determine the ancestor as being the first node of the
 * document, it assesses the position of the opening tag as determining the position. It is an error
 * to pass this comparator two nodes which do not belong to the same document.
 */
class DocumentOrderComparator : Comparator<Node>, Serializable {
    override fun compare(o1: Node, o2: Node): Int {
        if (o1 == o2) {
            return 0
        }
        // Get the ancestors of each node
        // could use xpath but this may be faster.
        val o1Ancestors = Nodes()
        var node: Node? = o1
        while (node != null) {
            o1Ancestors.insert(node, 0)
            node = node.parent
        }
        val o2Ancestors = Nodes()
        node = o2
        while (node != null) {
            o2Ancestors.insert(node, 0)
            node = node.parent
        }
        // Initially based result on number of ancestors
        var result = 0
        if (o1Ancestors.size() < o2Ancestors.size()) {
            result = -1
        } else if (o1Ancestors.size() > o2Ancestors.size()) {
            result = 1
        }
        val searchDepth = min(o1Ancestors.size(), o2Ancestors.size())
        var parent: ParentNode? = null
        for (i in 0 until searchDepth) {
            val node1 = o1Ancestors[i]
            val node2 = o2Ancestors[i]
            if (node1 !== node2) {
                if (parent == null) {
                    // TODO: We probably should do something else as throwing a RuntimeException from a
                    // comparator is not the best idea.
                    // http://www.ibm.com/developerworks/library/j-ce/
                    // As of the time of writing this comparator it should not be that nodes from different
                    // node trees will be compared, so is a low priority to correct.
                    // Possible alternative solutions would be to sort unrelated nodes first or last in a
                    // particular order (eg. determined by the value string) or we may need to create an
                    // alternative implementation.
                    throw RuntimeException("Nodes do not belong to the same tree of nodes")
                }
                val n1Index = parent.indexOf(node1)
                val n2Index = parent.indexOf(node2)
                result = if (n1Index < n2Index) {
                    -1
                } else {
                    1
                }
                break
            } else {
                parent = node1 as ParentNode
            }
        }
        return result
    }
}