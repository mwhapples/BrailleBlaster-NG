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
package org.brailleblaster.perspectives.braille.views.wp

import nu.xom.Element
import java.util.*

class Selection {
    /**
     * NOTE: If nothing is selected, selection start is not necessarily the caret position.
     * It's not clear if this should be the case or not
     */
	@JvmField
	var selectionStart = 0
    private var selectionLength = 0
    private val selectionHistory: LinkedList<Element>

    constructor() {
        selectionHistory = LinkedList()
    }

    constructor(selection: IntArray) {
        selectionHistory = LinkedList()
        selectionStart = selection[0]
        setSelectionLength(selection[1])
    }

    fun adjustSelectionStart(`val`: Int) {
        selectionStart += `val`
    }

    fun getSelectionLength(): Int {
        return selectionLength
    }

    fun setSelectionLength(selectionLength: Int) {
        this.selectionLength = selectionLength
        checkSelectionElement()
    }

    fun adjustSelectionLength(`val`: Int) {
        selectionLength += `val`
    }

    val selectionEnd: Int
        get() = selectionStart + selectionLength

    fun previousElement(): Element? {
        if (!selectionHistory.isEmpty()) selectionHistory.removeFirst()
        return if (!selectionHistory.isEmpty()) selectionHistory.first() else null
    }

    var selectionElement: Element?
        get() = if (selectionHistory.isEmpty()) null else selectionHistory.first()
        set(value) {
            if (value != null) {
                selectionHistory.add(0, value)
            } else {
                selectionHistory.clear()
            }
        }

    fun forward(caretPos: Int): Boolean {
        return selectionEnd == caretPos
    }

    private fun checkSelectionElement() {
        if (selectionLength <= 0 && !selectionHistory.isEmpty()) selectionHistory.clear()
    }
}
