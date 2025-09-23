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
package org.brailleblaster.utd.internal.xml

import nu.xom.Node

class NodeIterator(private val startNode: Node?, stayInsideStartNode: Boolean, forward: Boolean) :
    Iterator<Node?> {
    private val stayInsideStartNode: Boolean
    private val forward: Boolean
    private var nextNode: Node?

    init {
        this.nextNode = startNode
        this.stayInsideStartNode = stayInsideStartNode
        this.forward = forward
    }

    override fun hasNext(): Boolean {
        return nextNode != null
    }

    override fun next(): Node? {
        val curNode = nextNode
        doAdvance()
        return curNode
    }

    fun doAdvance() {
        nextNode = if (nextNode!!.childCount != 0) {
            nextNode!!.getChild(if (forward) 0 else nextNode!!.childCount - 1)
        } else {
            itrNextNode(
                nextNode!!,
                if (stayInsideStartNode) startNode else null,
                forward
            )
        }
    }

    companion object {
        /**
         * Safe following node impl that stops once outside of the given start node
         *
         * @param stopNode     Parent we are not escaping from
         * @param inputCurNode Assumed to be some (maybe nested) child of startNode
         */
        @JvmStatic
        fun itrNextNode(inputCurNode: Node, stopNode: Node?, forward: Boolean): Node? {
            var curNode: Node = inputCurNode
            //TODO: This will break if inputCurNode is not descendant from stopNode
            while (stopNode == null || stopNode !== curNode) {
                val parent = curNode.parent ?: break
                val index = parent.indexOf(curNode)
                if (forward && index != parent.getChildCount() - 1) {
                    return parent.getChild(index + 1)
                } else if (!forward && index > 0) {
                    return parent.getChild(index - 1)
                }
                //Last entry in parent, get parents sibling
                curNode = parent
            }
            //Finished getting all childrens parents
            return null
        }
    }
}
