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
package org.brailleblaster.perspectives.mvc.modules.misc

import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.ModifyEvent
import org.brailleblaster.perspectives.mvc.events.XMLCaretEvent

class PostViewUpdateModule : SimpleListener {
    private var onModifyEvent: Procedure? = null
    private val onXMLCaretEvent: MutableList<Procedure> = ArrayList()

    override fun onEvent(event: SimpleEvent) {
        if (event is ModifyEvent && onModifyEvent != null) {
            onModifyEvent!!.execute()
            onModifyEvent = null
        } else if (event is XMLCaretEvent) {
            for (procedure in onXMLCaretEvent) {
                procedure.execute()
            }
            onXMLCaretEvent.clear()
        }
    }

    fun onModifyEvent(modify: Procedure?) {
        onModifyEvent = modify
    }

    fun onXMLCaretEvent(caret: Procedure) {
        onXMLCaretEvent.add(caret)
    }

    fun interface Procedure {
        fun execute()
    }
}