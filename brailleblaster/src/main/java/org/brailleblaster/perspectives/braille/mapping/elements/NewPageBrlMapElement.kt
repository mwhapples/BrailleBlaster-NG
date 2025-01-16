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
package org.brailleblaster.perspectives.braille.mapping.elements

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.utd.internal.elements.NewPage
import org.brailleblaster.utd.properties.UTDElements

class NewPageBrlMapElement : BrailleMapElement {
    constructor(start: Int, end: Int, n: Node) : super(start, end, n)

    constructor(n: Node) : super(n) {
        require(UTDElements.NEW_PAGE.isA(n)) { "n" }
    }

    val isPageBreak: Boolean
        get() = NewPage.PAGE_BREAK_ATTR_VALUE == (node as Element).getAttributeValue(
            NewPage.PAGE_BREAK_ATTR_NAME
        )
}
