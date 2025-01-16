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

import nu.xom.Element
import nu.xom.Node
import java.util.function.Function

enum class MathTextFinder(private val finderFunction: Function<Element, String?>) {
    NONE(Function<Element, String?> { null }),
    ALTTEXT_ATTRIBUTE(Function { n: Element -> n.getAttributeValue("alttext") }),
    TITLE_ATTRIBUTE(Function { n: Element -> n.getAttributeValue("title") });

    fun findText(n: Node?): String? {
        return if (n !is Element) {
            null
        } else finderFunction.apply(n)
    }
}