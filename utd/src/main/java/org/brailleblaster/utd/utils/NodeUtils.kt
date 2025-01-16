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
package org.brailleblaster.utd.utils

import nu.xom.Node
import org.brailleblaster.utd.utils.xom.NodeContext
import org.brailleblaster.utd.utils.xom.nodeCache
import java.io.Serializable
import java.util.*

object NodeUtils {
    private fun getNodePath(startNode: Node, pathCache: MutableMap<Node, IntList>): IntList {
        val nodeParents: Deque<Pair<Node, Int>> = LinkedList()
        // Ensure the break condition here matches what is expected after, see comments after loop.
        var nc: NodeContext? = nodeCache.getUnchecked(startNode)
        while (nc != null && !pathCache.containsKey(nc.node)) {
            if (nc.parent != null) {
                nodeParents.push(nc.node to nc.index)
            }
            nc = nc.parent
        }
        // Check above while loop, we can only get here when node is null or in the pathCache.
        // Thus below line should never throw an exception.
        var path = if (nc == null) IntList.EmptyIntList else pathCache[nc.node]!!
        for ((n, i) in nodeParents) {
            path = IntList.IntListEntry(i, path)
            pathCache[n] = path
        }
        return path
    }

    @JvmStatic fun sortByDocumentOrder(nodes: List<Node>): List<Node> {
        val pathCache: MutableMap<Node, IntList> = HashMap()
        return nodes.associateBy { getNodePath(it, pathCache).toList().reversed() }.toSortedMap(ListComparator()).values.toList()
    }

    class ListComparator<T : Comparable<T>?> : Comparator<List<T>>, Serializable {
        override fun compare(o1: List<T>, o2: List<T>): Int {
            val i1 = o1.iterator()
            val i2 = o2.iterator()
            while (i1.hasNext() && i2.hasNext()) {
                val t1 = i1.next()
                val t2 = i2.next()
                val compared = t1!!.compareTo(t2)
                if (compared != 0) return compared
            }
            if (i1.hasNext()) return 1
            return if (i2.hasNext()) -1 else 0
        }
    }

    sealed class IntList {
        abstract val head: Int
        abstract val tail: IntList
        abstract fun toList(): List<Int>
        data object EmptyIntList : IntList() {
            override val head: Int
                get() {
                    throw NoSuchElementException()
                }
            override val tail: IntList
                get() {
                    throw NoSuchElementException()
                }

            override fun toList(): List<Int> {
                return emptyList()
            }
        }

        class IntListEntry(override val head: Int, override val tail: IntList) : IntList() {
            override fun toList(): List<Int> {
                return generateSequence({ this.head to this.tail }, { (_, t) -> if (t != EmptyIntList) t.head to t.tail else null}).map { (h, _) -> h }.toList()
            }
        }
    }
}