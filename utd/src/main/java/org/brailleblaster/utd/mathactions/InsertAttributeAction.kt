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
package org.brailleblaster.utd.mathactions

import jakarta.xml.bind.annotation.XmlAttribute
import nu.xom.Element
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan

@Suppress("UNUSED")
class InsertAttributeAction private constructor() : InsertAction() {
    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = prime * result + if (close == null) 0 else close.hashCode()
        result = prime * result + if (open == null) 0 else open.hashCode()
        result = prime * result + if (separators == null) 0 else separators.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || !super.equals(other)) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }
        val o = other as InsertAttributeAction
        return close == o.close && open == o.open && separators == o.separators
    }

    @XmlAttribute
    var open: String? = null

    @XmlAttribute
    var preOpen = ""

    @XmlAttribute
    var separators: String? = null

    @XmlAttribute
    var preSeparators = ""

    @XmlAttribute
    var close: String? = null

    @XmlAttribute
    var preClose = ""

    constructor(open: String?, separators: String?, close: String?) : this() {
        this.open = open
        this.separators = separators
        this.close = close
    }

    override fun processElement(node: Element, context: ITranslationEngine): List<TextSpan> {
        var pre = node.getAttributeValue(open)
        if (!pre.isNullOrEmpty() && preOpen.isNotEmpty()) {
            pre = preOpen + pre
        }
        val separatorsValue = node.getAttributeValue(separators)
        val mid: MutableList<String> = ArrayList()
        if (separatorsValue != null) {
            val sepsArr = separatorsValue.split(' ')
            for (s in sepsArr) {
                var sep = s
                if (sep.isNotEmpty() && preSeparators.isNotEmpty()) {
                    sep = preSeparators + sep
                }
                mid.add(sep)
            }
        }
        var post = node.getAttributeValue(close)
        if (!post.isNullOrEmpty() && preClose.isNotEmpty()) {
            post = preClose + post
        }
        return processElementWithInserts(node, pre, mid, post, context)
    }
}