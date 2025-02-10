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
import org.brailleblaster.perspectives.mvc.events.ModularEvent

class QueueManager {
    private val undoQueue: EventQueue = UndoQueue()
    private val redoQueue: EventQueue = RedoQueue()
    private var swapFrame = false

    fun addUndoEvent(f: EventFrame) {
        if (f.peek() is ModularEvent) clearViewEvents(undoQueue)
        if (undoQueue.empty()) {
            f.id = FIRST_ITEM_ID
            if (!redoQueue.empty() && !swapFrame) redoQueue.clear()
        } else {
            val id = undoQueue.peek()!!.id + 1
            if (!redoQueue.empty() && redoQueue.peek()!!.id == id) redoQueue.clear()
            f.id = id
        }
        undoQueue.add(f)
    }

    fun addRedoEvent(f: EventFrame) {
        if (f.peek() is ModularEvent) clearViewEvents(redoQueue)
        val id: Int = if (undoQueue.empty()) FIRST_ITEM_ID else undoQueue.peek()!!.id + 1
        f.id = id
        redoQueue.add(f)
    }

    private fun clearViewEvents(q: EventQueue) {
        while (q.peekLast()?.let { !it.empty() && it[0] is ViewEvent } == true) q.removeLast()
    }

    fun undo(manager: Manager) {
        undoQueue.popEvent(manager.viewInitializer, manager.mapList, manager)
    }

    fun redo(manager: Manager) {
        swapFrame = true
        redoQueue.popEvent(manager.viewInitializer, manager.mapList, manager)
        swapFrame = false
    }

    fun peekUndoEvent(): EventFrame? {
        return undoQueue.peek()
    }

    fun peekRedoEvent(): EventFrame? {
        return redoQueue.peek()
    }

    fun popUndoEvent(): EventFrame {
        return undoQueue.removeLast()
    }

    companion object {
        private const val FIRST_ITEM_ID = 1
    }
}
