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

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import nu.xom.Element
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan
import org.brailleblaster.utd.actions.GenericAction
import org.brailleblaster.utd.properties.ContentType

/**
 * General math action for inserting child nodes.
 *
 * This is the math action used for inserting the child nodes of a node. It also has options for inserting content before, after and in between the child nodes. The inserted text will also be translated using the Braille translator, and so through this mechanism it is possible to define private use characters in unicode for certain Braille indicators such as the Nemeth fraction indicators. This action should not be confused with the non-math InsertAction, this one should only ever be used within a math block, use anywhere else potentially could lead to unpredictable results.
 */
@XmlAccessorType(XmlAccessType.FIELD)
open class InsertAction : GenericAction() {
    @XmlElement
    var preInsert: String? = null

    @XmlElement
    var midInsert: List<String>? = ArrayList()

    @XmlElement
    var postInsert: String? = null
    override fun processElement(node: Element, context: ITranslationEngine): List<TextSpan> {
        return processElementWithInserts(node, preInsert, midInsert, postInsert, context)
    }

    protected fun processElementWithInserts(
        node: Element,
        pre: String?,
        mid: List<String>?,
        post: String?,
        context: ITranslationEngine
    ): List<TextSpan> {
        val spans: MutableList<TextSpan> = ArrayList()
        var insert: TextSpan
        if (pre != null) {
            insert = TextSpan(null, pre)
            insert.contentType = ContentType.Math
            spans.add(insert)
        }
        val midInsertSize = mid!!.size
        val childCount = node.childCount
        val lastButOneChild = childCount - 1
        for (i in 0 until childCount) {
            val child = node.getChild(i)
            val action = context.actionMap.findValueOrDefault(child)
            val childSpans = action.applyTo(child, context)
            spans.addAll(childSpans)
            // Do not place a midInsert after the last child node
            if (i < midInsertSize && i < lastButOneChild) {
                insert = TextSpan(null, mid[i])
                insert.contentType = ContentType.Math
                spans.add(insert)
            }
        }
        if (post != null) {
            insert = TextSpan(null, post)
            insert.contentType = ContentType.Math
            spans.add(insert)
        }
        return spans
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = prime * result + (midInsert?.hashCode() ?: 0)
        result = prime * result + (postInsert?.hashCode() ?: 0)
        result = prime * result + (preInsert?.hashCode() ?: 0)
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
        val o = other as InsertAction
        return midInsert == o.midInsert && postInsert == o.postInsert && preInsert == o.preInsert
    }
}