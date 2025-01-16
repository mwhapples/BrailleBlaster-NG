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

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import nu.xom.Node
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.internal.InterfaceAdapter

/**
 * Interface for all node matching objects.
 *
 *
 * Implement this interface if you want to add a new implementation for testing if actions,
 * styles, etc, should apply to a node. To make it that a matcher implementation is independent of
 * the configuration persistence format, you should create your matcher implementation using the
 * java beans conventions for any properties which should be persisted. If a particular
 * implementation should not want to expose a no-args constructor, then it is acceptable for the
 * no-args constructor to be private.
 *
 *
 * For more advanced matchers, the current default implementation for persisting configurations
 * is JAXB, so JAXB annotations may be used to customise the persistence of the matcher
 * implementation. However this may not be the case on all systems, alternative persistence
 * implementations may be used and the default may change over time.
 */
@XmlJavaTypeAdapter(InterfaceAdapter::class)
interface INodeMatcher {
    /**
     * Check whether the node matches.
     *
     *
     * This method checks whether the node meets the criteria set out by the matcher.
     *
     * @param node The node to check.
     * @return True if the node matches the criteria and false if not.
     */
    fun isMatch(node: Node, namespaces: NamespaceMap): Boolean
}