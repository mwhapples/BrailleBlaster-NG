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
package org.brailleblaster.utd.asciimath

import nu.xom.Node
import nu.xom.Nodes
import java.util.NoSuchElementException
import java.util.function.Predicate

abstract class AbstractNodeIterator(private val p: Predicate<Node>) : Iterator<Node> {
    private var index = -1
    private var nextIndex = -1
    protected abstract operator fun get(i: Int): Node
    protected abstract fun size(): Int
    override fun hasNext(): Boolean {
        if (index == nextIndex) {
            do {
                nextIndex++
            } while (nextIndex < size() && !p.test(get(nextIndex)))
        }
        return nextIndex < size()
    }

    override fun next(): Node {
        if (hasNext()) {
            index = nextIndex
            return get(index)
        }
        throw NoSuchElementException()
    }
}

class ChildNodeIterator @JvmOverloads constructor(
    val parent: Node,
    p: Predicate<Node> = Predicate { true }
) : AbstractNodeIterator(p) {
    override fun get(i: Int): Node {
        return parent.getChild(i)
    }

    override fun size(): Int {
        return parent.childCount
    }
}

class NodesIterator @JvmOverloads constructor(
    private val nodes: Nodes,
    p: Predicate<Node> = Predicate { true }
) : AbstractNodeIterator(p) {
    override fun get(i: Int): Node {
        return nodes[i]
    }

    override fun size(): Int {
        return nodes.size()
    }
}