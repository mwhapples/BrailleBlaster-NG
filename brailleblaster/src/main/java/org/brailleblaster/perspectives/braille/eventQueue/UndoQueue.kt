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
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.braille.stylers.TextUpdateHandler
import org.brailleblaster.perspectives.braille.viewInitializer.ViewInitializer
import org.brailleblaster.perspectives.mvc.events.ModularEvent
import org.brailleblaster.perspectives.mvc.events.Queuer
import org.slf4j.LoggerFactory

class UndoQueue : EventQueue() {
    override fun handleEvent(frame: EventFrame, vi: ViewInitializer, list: MapList, manager: Manager) {
        while (!frame.empty()) {
            val event = frame[frame.size() - 1]
            if (event is ModularEvent) {
                val q = Queuer(manager)
                q.handleEvent(frame, Sender.UNDO_QUEUE)
            } else if (event.eventType == EventTypes.Edit) {
                val editHandler = TextUpdateHandler(manager, vi, list)
                editHandler.undoEdit(frame)
            } else {
                logger.warn("Uncaught event for " + event.eventType)
                frame.pop()
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UndoQueue::class.java)
    }
}