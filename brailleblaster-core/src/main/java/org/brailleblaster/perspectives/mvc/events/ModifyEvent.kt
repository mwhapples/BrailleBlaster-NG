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

import nu.xom.Node
import org.apache.commons.lang3.builder.ToStringBuilder
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.mvc.SimpleEvent
import java.util.*

class ModifyEvent(sender: Sender, changedNodes: List<Node>, @JvmField var translate: Boolean) : SimpleEvent(sender) {
    @JvmField
    val changedNodes = changedNodes.toMutableList()
    @JvmField
	var sectionIndex = 0
    @JvmField
	var textOffset = 0

    constructor(sender: Sender, translate: Boolean, vararg changedNodes: Node) : this(
        sender,
        changedNodes.toMutableList(),
        translate
    )

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    val isQueueEvent: Boolean
        get() = QUEUE_EVENTS.contains(sender)

    fun setQueueData(sectionIndex: Int, textOffset: Int) {
        this.sectionIndex = sectionIndex
        this.textOffset = textOffset
    }

    companion object {
        val QUEUE_EVENTS: EnumSet<Sender> = EnumSet.of(Sender.REDO_QUEUE, Sender.UNDO_QUEUE)
        private var nextEventIsUndoable = true

        /*
	 * After using this method, the next time a ModifyEvent is dispatched it will not create an
	 * undo frame. This only applies to the next ModifyEvent. Used for when code needs to dispatch 
	 * multiple ModifyEvents in a single edit. 
	 */
		@JvmStatic
		fun cannotUndoNextEvent() {
            nextEventIsUndoable = false
        }

        @JvmStatic
		fun resetUndoable() {
            nextEventIsUndoable = true
        }

        @JvmStatic
		fun canUndo(): Boolean {
            return nextEventIsUndoable
        }
    }
}