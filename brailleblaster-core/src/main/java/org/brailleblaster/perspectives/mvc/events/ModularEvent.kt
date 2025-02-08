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
package org.brailleblaster.perspectives.mvc.events

import nu.xom.Element
import org.brailleblaster.perspectives.braille.eventQueue.Event
import org.brailleblaster.perspectives.braille.eventQueue.EventTypes
import org.brailleblaster.utils.xom.XOMSerializer

/**
 * A ModularEvent is an undo frame that originates from a ModifyEvent
 */
class ModularEvent(
    e: Element,
    actionMap: Element?,
    styleMap: Element?,
    indexes: List<Int>,
    sectionIndex: Int,
    textOffset: Int
) : Event(EventTypes.Update, textOffset) {
    private val e: ByteArray
    private val actionMap: ByteArray?
    private val styleMap: ByteArray?
    @JvmField
	var indexes: List<Int>
    @JvmField
	var sectionIndex: Int

    init {
        this.e = compress(e.copy())!!
        this.actionMap = if (actionMap != null) compress(actionMap.copy()) else null
        this.styleMap = if (styleMap != null) compress(styleMap.copy()) else null
        this.indexes = indexes
        this.sectionIndex = sectionIndex
    }

    val element: Element
        get() = decompress(e)!!

    fun getStyleMap(): Element? {
        return decompress(styleMap)
    }

    fun getActionMap(): Element? {
        return decompress(actionMap)
    }

    private fun compress(e: Element): ByteArray? {
        return XOMSerializer.compress(e)
    }

    private fun decompress(arr: ByteArray?): Element? {
        return XOMSerializer.decompressElement(arr)
    }
}