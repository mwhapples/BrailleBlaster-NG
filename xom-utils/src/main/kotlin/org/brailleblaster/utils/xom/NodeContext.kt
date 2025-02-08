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
package org.brailleblaster.utils.xom

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import nu.xom.Node
import nu.xom.ParentNode

class NodeContext(val node: Node, parent: NodeContext?, index: Int) {
    private var privIndex: Int = index
    var parent: NodeContext? = parent
        get() {
            val nc = field
            if (nc == null) {
                val parentNode = node.parent
                if (parentNode != null) {
                    field = NodeContext(parentNode, null, 0)
                }
            } else {
                val parentNode = nc.node
                if (!(privIndex in 0 until parentNode.childCount && node == parentNode.getChild(privIndex))) {
                    val actualParentNode = node.parent
                    field = if (actualParentNode == null) null else NodeContext(actualParentNode, null, -1)
                }
            }
            return field
        }
    var index: Int
        get() {
            val nc = parent
            val i = privIndex
            if (nc == null) {
                privIndex = -1
            } else if (nc.node is ParentNode) {
                if (!(i in 0 until nc.node.childCount && node == nc.node.getChild(i))) {
                    privIndex = nc.node.indexOf(node)
                }
            } else {
                // Something really bad has happened, parent node is not a ParentNode
                // Use index -1 to indicate its not valid.
                privIndex = -1
            }
            return privIndex
        }
        set(value) { privIndex = value }
}

val nodeContextLoader = object : CacheLoader<Node, NodeContext>() {
    override fun load(key: Node): NodeContext {
        val parentNode = key.parent
        val parent: NodeContext?
        val index: Int
        if (parentNode != null) {
            parent = nodeCache.getUnchecked(parentNode)
            index = parentNode.indexOf(key)
        } else {
            parent = null
            index = -1
        }
        return NodeContext(key, parent, index)
    }
}
val nodeCache: LoadingCache<Node, NodeContext> = CacheBuilder.newBuilder().maximumSize(10_000_000).build(nodeContextLoader)