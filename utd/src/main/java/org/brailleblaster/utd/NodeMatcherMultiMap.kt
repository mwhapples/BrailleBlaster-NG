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
package org.brailleblaster.utd

import nu.xom.Node
import org.brailleblaster.utd.matchers.INodeMatcher

abstract class NodeMatcherMultiMap<T, S : INodeMatcherMap<T>?>(override val defaultValue: T) : INodeMatcherMap<T> {
    var maps: List<S> = ArrayList()

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun containsKey(key: INodeMatcher): Boolean {
        var result = false
        var i = 0
        while (i < maps.size && !result) {
            if (maps[i] != null) {
                result = maps[i]!!.containsKey(key)
            }
            i++
        }
        return result
    }

    override fun containsValue(value: T): Boolean {
        var result = false
        var i = 0
        while (!result && i < maps.size) {
            if (maps[i] != null) {
                result = maps[i]!!.containsValue(value)
            }
            i++
        }
        return result
    }

    override val entries: MutableSet<MutableMap.MutableEntry<INodeMatcher, T>>
        get() {
            val result: MutableSet<MutableMap.MutableEntry<INodeMatcher, T>> = HashSet()
            for (map in maps) {
                if (map != null) {
                    result.addAll(map.entries)
                }
            }
            return result
        }

    override fun get(key: INodeMatcher): T? {
        var result: T? = null
        var i = 0
        while (result == null && i < maps.size) {
            if (maps[i] != null) {
                result = maps[i]!![key]
            }
            i++
        }
        return result
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override val keys: MutableSet<INodeMatcher>
        get() {
            val result: MutableSet<INodeMatcher> = HashSet()
            for (map in maps) {
                if (map != null) {
                    result.addAll(map.keys)
                }
            }
            return result
        }

    @Throws(NoSuchElementException::class)
    override fun findValue(node: Node): T {
        var result: T? = null
        var i = 0
        while (i < maps.size && result == null) {
            if (maps[i] != null) {
                result = maps[i]!!.findValueWithDefault(node, null)
            }
            i++
        }
        if (result == null) {
            throw NoSuchElementException("No value found")
        }
        return result
    }

    override var namespaces: NamespaceMap
        get() {
            val nsMap = NamespaceMap()
            for (actionMap in maps) {
                if (actionMap != null) {
                    val namespaces = actionMap.namespaces
                    for (prefix in namespaces.prefixes) {
                        nsMap.addNamespace(prefix, namespaces.getNamespace(prefix))
                    }
                }
            }
            return nsMap
        }
        set(_) { //
            // Do nothing.
        }

    override val size: Int
        get() {
            var size = 0
            for (map in maps) {
                if (map != null) {
                    size += map.size
                }
            }
            return size
        }

    override val values: MutableCollection<T>
        get() {
            val result: MutableList<T> = ArrayList()
            for (map in maps) {
                if (map != null) {
                    result.addAll(map.values)
                }
            }
            return result
        }

    override fun put(key: INodeMatcher, value: T): T? {
        throw UnsupportedOperationException()
    }

    override fun putAll(from: Map<out INodeMatcher, T>) {
        throw UnsupportedOperationException()
    }

    override fun remove(key: INodeMatcher): T? {
        throw UnsupportedOperationException()
    }
}
