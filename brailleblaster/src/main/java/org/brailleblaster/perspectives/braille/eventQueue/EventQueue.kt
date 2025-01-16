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
package org.brailleblaster.perspectives.braille.eventQueue

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.maps.MapList
import org.brailleblaster.perspectives.braille.viewInitializer.ViewInitializer
import java.util.concurrent.LinkedBlockingDeque

abstract class EventQueue : LinkedBlockingDeque<EventFrame>(SIZE) {
    fun popEvent(vi: ViewInitializer, list: MapList, manager: Manager): EventFrame? {
        if (size > 0) {
            val f = removeLast()
            handleEvent(f, vi, list, manager)
            return f
        }
        return null
    }

    override fun add(element: EventFrame): Boolean {
        if (size == SIZE) removeFirst()
        return super.add(element)
    }

    override fun peek(): EventFrame? {
        return if (empty()) null else last
    }

    fun empty(): Boolean {
        return size == 0
    }

    protected abstract fun handleEvent(frame: EventFrame, vi: ViewInitializer, list: MapList, manager: Manager)

    companion object {
        private const val SIZE = 40
    }
}
