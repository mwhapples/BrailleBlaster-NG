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
package org.brailleblaster.perspectives.braille.messages

import nu.xom.Element
import org.brailleblaster.perspectives.braille.mapping.elements.TextMapElement

class TabInsertionMessage : Message {
    @JvmField
	var xIndex = 0
    @JvmField
	var tabValue = 0
    @JvmField
	var currentElement: TextMapElement? = null
    @JvmField
	var parent: Element? = null
    var tab: Element? = null

    constructor(tabValue: Int, xIndex: Int, currentElement: TextMapElement?) : super(BBEvent.TAB_INSERTION) {
        this.tabValue = tabValue
        this.xIndex = xIndex
        this.currentElement = currentElement
    }

    constructor(tabValue: Int, parent: Element?) : super(BBEvent.TAB_ADJUSTMENT) {
        this.tabValue = tabValue
        this.parent = parent
    }

    constructor(e: Element?) : super(BBEvent.TAB_DELETION) {
        tab = e
    }
}
