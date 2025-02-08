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
package org.brailleblaster.perspectives.braille.views.wp.formatters

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.eventQueue.EventFrame
import org.brailleblaster.perspectives.braille.eventQueue.EventTypes
import org.brailleblaster.perspectives.braille.eventQueue.ViewEvent
import org.brailleblaster.perspectives.braille.views.wp.TextView
import org.eclipse.swt.custom.ExtendedModifyEvent

class EditRecorder(var manager: Manager, var text: TextView) {
    var frame: EventFrame? = null
    private var currentLine: String? = null
    private var currentLineNumber = 0
    fun recordEvent(e: ExtendedModifyEvent) {
        createEvent(e.start, e.length, e.replacedText)
    }

    fun recordLine(currentLine: String?, currentLineNumber: Int) {
        this.currentLine = currentLine
        this.currentLineNumber = currentLineNumber
    }

    fun recordLine(start: Int, end: Int) {
        val firstLine = text.view.getLineAtOffset(start)
        val lastLine = text.view.getLineAtOffset(end)
        val firstOffset = text.view.getOffsetAtLine(firstLine)
        val lastOffset = text.view.getOffsetAtLine(lastLine) + text.view.getLine(lastLine).length
        currentLineNumber = firstLine
        currentLine = text.view.getTextRange(firstOffset, lastOffset - firstOffset)
    }

    private fun createEvent(wordStart: Int, wordEnd: Int, recordedText: String) {
        val eventFrame = manager.peekUndoEvent()
        if (eventFrame?.peek()?.eventType == EventTypes.Edit) {
            val ev = eventFrame.peek() as ViewEvent?
            if (sameWord(ev, wordStart, recordedText)) eventFrame.addEvent(
                ViewEvent(
                    EventTypes.Edit,
                    wordStart,
                    wordEnd,
                    0,
                    0,
                    recordedText
                )
            ) else addEvent(wordStart, wordEnd, recordedText)
        } else {
            addEvent(wordStart, wordEnd, recordedText)
        }
    }

    private fun addEvent(wordStart: Int, wordEnd: Int, recordedText: String) {
        frame = EventFrame()
        frame!!.addEvent(ViewEvent(EventTypes.Edit, wordStart, wordEnd, 0, 0, recordedText))
        manager.addUndoEvent(frame)
    }

    private fun sameWord(e: ViewEvent?, wordStart: Int, recordedText: String): Boolean {
        val line = text.view.getLineAtOffset(wordStart)
        if (line == currentLineNumber) {
            val priorStart = e!!.textOffset
            return wordStart + 1 == priorStart || wordStart - 1 == priorStart
        }
        return false
    }
}
