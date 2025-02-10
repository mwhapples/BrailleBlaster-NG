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
package org.brailleblaster.perspectives.braille.stylers

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.document.BrailleDocument
import org.brailleblaster.perspectives.braille.eventQueue.EventFrame
import org.brailleblaster.perspectives.braille.eventQueue.EventTypes
import org.brailleblaster.perspectives.braille.eventQueue.ViewEvent
import org.brailleblaster.perspectives.braille.mapping.elements.LineNumberTextMapElement
import org.brailleblaster.perspectives.braille.mapping.maps.MapList
import org.brailleblaster.perspectives.braille.messages.UpdateMessage
import org.brailleblaster.perspectives.braille.viewInitializer.ViewInitializer

//import org.brailleblaster.perspectives.braille.eventQueue.Event;
//import org.brailleblaster.perspectives.braille.eventQueue.ModelEvent;
class TextUpdateHandler(manager: Manager, vi: ViewInitializer, list: MapList) : Handler(manager, vi, list) {
    val document: BrailleDocument = manager.document

    fun updateText(message: UpdateMessage) {
        clearEditEvents()
        resetText(message.newText)
        manager.isDocumentEdited = true
    }

    /*
	 * public void undoText(EventFrame f){ while(!f.empty() &&
	 * f.peek().getEventType().equals(EventTypes.Update)){ removeListeners();
	 * ModelEvent ev = (ModelEvent)f.pop();
	 * 
	 * list.setCurrent(ev.getListIndex()); addRedoEvent();
	 * resetText(ev.getNode().getValue());
	 * text.setCurrentElement(manager.getMapList().get(ev.getListIndex()).start)
	 * ;
	 * 
	 * text.view.setCaretOffset(ev.getTextOffset()); initializeListeners(); } }
	 * 
	 * public void redoText(EventFrame f){ while(!f.empty() &&
	 * f.peek().getEventType().equals(EventTypes.Update)){ ModelEvent ev =
	 * (ModelEvent)f.pop(); list.setCurrent(ev.getListIndex()); //
	 * addUndoEvent(); resetText(ev.getNode().getValue());
	 * text.view.setCaretOffset(ev.getTextOffset()); } }
	 */
    private fun resetText(textStr: String) {
        var text = textStr
        if (list.currentIndex < list.size - 1) {
            val t = list[list.currentIndex + 1]
            if (t is LineNumberTextMapElement) text = text.replace("\\s+$".toRegex(), "")
        }
        document.updateNode(manager, list, text)
    }

    private fun clearEditEvents() {
        // EventFrame f = addEvent();
        // if(manager.peekUndoEvent() != null){
        // clears all edit events in a row
        while (isEditEvent(manager.peekUndoEvent())) {
            manager.popUndo()
        }

        // }

        // manager.addUndoEvent(f);
    }

    // private void addRedoEvent(){
    // manager.addRedoEvent(addEvent());
    // }
    /*
	 * private EventFrame addEvent(){ EventFrame f = new EventFrame();
	 * TextMapElement t = list.getCurrent(); Event e = new
	 * ModelEvent(EventTypes.Update, t.n, vi.getStartIndex(),
	 * list.getCurrentIndex(), t.start); f.addEvent(e);
	 * 
	 * return f; }
	 */
    fun undoEdit(f: EventFrame) {
        val frame = recreateEditEvent(f)
        manager.addRedoEvent(frame)
    }

    fun redoEdit(f: EventFrame) {
        val frame = recreateEditEvent(f)
        manager.addUndoEvent(frame)
    }

    private fun recreateEditEvent(f: EventFrame): EventFrame {
        val frame = EventFrame()
        while (!f.empty() && f.peek()!!.eventType == EventTypes.Edit) {
            manager.removeListeners()
            val ev = f.pop() as ViewEvent

            if (ev.textOffset >= text.currentStart && ev.textOffset <= text.currentEnd) text.view.caretOffset =
                ev.textOffset
            else text.setCurrentElement(ev.textOffset)

            val start = ev.textOffset
            val end = ev.textOffset + ev.textEnd
            val replacedText = text.view.getTextRange(ev.textOffset, end - start)

            frame.addEvent(ViewEvent(EventTypes.Edit, start, ev.text.length, 0, 0, replacedText))
            text.undoEdit(ev.textOffset, ev.textEnd, ev.text)
            manager.initializeListeners()
        }

        return frame
    }

    companion object {
        private fun isEditEvent(eventFrame: EventFrame?): Boolean {
            return eventFrame != null && !eventFrame.empty() && eventFrame[0].eventType == EventTypes.Edit
        }
    }
}
