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
import org.brailleblaster.utd.internal.InterfaceAdapter
import org.brailleblaster.utd.matchers.INodeMatcher
import org.slf4j.LoggerFactory
import java.io.Serializable
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter

class ConditionalValue<T> : Serializable {
    @set:XmlElement
    @set:XmlJavaTypeAdapter(InterfaceAdapter::class)
    var matcher: INodeMatcher? = null
    var value: T? = null
        private set

    fun checkCondition(node: Node, namespaces: NamespaceMap): Boolean {
        val result = matcher!!.isMatch(node, namespaces)
        log.debug("Node {} tested {} for condition", node, result)
        return result
    }

    @XmlJavaTypeAdapter(InterfaceAdapter::class)
    @XmlElement
    fun setValue(value: T) {
        this.value = value
    }

    companion object {
        private val log = LoggerFactory.getLogger(ConditionalValue::class.java)
    }
}