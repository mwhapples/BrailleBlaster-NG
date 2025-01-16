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

import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import nu.xom.Node
import org.brailleblaster.utd.ComparableStyle
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.internal.ComparableStyleAdapter

/**
 * Uses the style of the previous sibling to check if it matches.
 *
 *
 * This is basically a shortcut to constructing a PreviousSiblingMatcher with a ActionAndStyleMatcher delegate.
 */
class PreviousSiblingStyleMatcher : INodeMatcher {
    private val delegateMatcher = PreviousSiblingMatcher()
    private val styleMatcher: ActionAndStyleMatcher = ActionAndStyleMatcher()

    init {
        delegateMatcher.matcher = styleMatcher
    }

    @get:XmlJavaTypeAdapter(ComparableStyleAdapter::class)
    @get:XmlElement(name = "style")
    var styles: List<ComparableStyle>
        get() = styleMatcher.styles
        set(styles) {
            styleMatcher.styles = styles
        }

    @get:XmlElement(name = "ignoreWhiteSpaceNodes")
    var isIgnoreWhiteSpace: Boolean
        get() = delegateMatcher.isIgnoreWhiteSpace
        set(value) {
            delegateMatcher.isIgnoreWhiteSpace = value
        }

    override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
        return delegateMatcher.isMatch(node, namespaces)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (styleMatcher.hashCode())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }
        val o = other as PreviousSiblingStyleMatcher
        return styleMatcher == o.styleMatcher
    }
}
