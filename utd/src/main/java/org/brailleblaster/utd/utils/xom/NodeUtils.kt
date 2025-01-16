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
package org.brailleblaster.utd.utils.xom

import nu.xom.Attribute
import nu.xom.Element
import nu.xom.Node
import nu.xom.ParentNode

private class NodeList(private val node: Node) : AbstractList<Node>() {
    override val size: Int
        get() = node.childCount

    override fun get(index: Int): Node {
        return node.getChild(index)
    }
}

val Node.childNodes: List<Node>
    get() = NodeList(this)

private class NodeMutableList(private val parentNode: ParentNode) : AbstractMutableList<Node>() {
    override val size: Int
        get() = parentNode.childCount

    override fun get(index: Int): Node {
        return parentNode.getChild(index)
    }
    override fun add(index: Int, element: Node) {
        parentNode.insertChild(element, index)
    }

    override fun removeAt(index: Int): Node = parentNode.removeChild(index)
    override fun set(index: Int, element: Node): Node {
        val oldNode = parentNode.removeChild(index)
        parentNode.insertChild(element, index)
        return oldNode
    }
}

val ParentNode.childNodes: MutableList<Node>
    get() = NodeMutableList(this)

private class AttributeSet(private val element: Element) : AbstractSet<Attribute>() {
    override val size: Int
        get() = element.attributeCount

    override fun iterator(): Iterator<Attribute> {
        return iterator {
            for (i in 0..<size) {
                yield(element.getAttribute(i))
            }
        }
    }
}

val Element.attributes: Set<Attribute>
    get() = AttributeSet(this)

fun Iterable<Node>.detachAll() = this.reversed().forEach { it.detach() }