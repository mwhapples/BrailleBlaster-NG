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

interface INodeMatcherMap<V> : MutableMap<INodeMatcher, V> {
    /**
     * Find an action which can be applied to the node.
     *
     * @param node The node to match.
     * @return The action which can apply to the node. If no action matches then the default action is returned.
     */
    fun findValueOrDefault(node: Node): V {
        return try {
            findValue(node)
        } catch (e: NoSuchElementException) {
            defaultValue
        }
    }

    /**
     * Find an action which can be applied to the node or the specified default.
     *
     * @param node         The node to match.
     * @param defaultValue The specified default action.
     * @return The action which can be applied to the node. If no action matches then the specified default action will be returned.
     */
    fun findValueWithDefault(node: Node, defaultValue: V?): V? {
        return try {
            findValue(node)
        } catch (e: NoSuchElementException) {
            defaultValue
        }
    }

    @Throws(NoSuchElementException::class)
    fun findValue(node: Node): V

    /**
     * Get the default action.
     * The default action is the action which should be applied if no other action in this map matches.
     *
     * @return The default action.
     */
    val defaultValue: V

    /**
     * Get the namespace definitions.
     *
     * @return The namespace prefix definitions.
     */
    var namespaces: NamespaceMap
}
