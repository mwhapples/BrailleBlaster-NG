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
import org.brailleblaster.perspectives.braille.mapping.maps.MapList
import org.brailleblaster.perspectives.braille.messages.RemoveNodeMessage
import org.brailleblaster.perspectives.braille.viewInitializer.ViewInitializer

class RemoveElementHandler(manager: Manager?, vi: ViewInitializer?, list: MapList?) : Handler(
    manager!!, vi!!, list!!
) {
    fun removeNode(m: RemoveNodeMessage) {
        removeElement(m)
    }

    private fun removeElement(message: RemoveNodeMessage) {
        manager.document.removeNode(list, message)
        if (message.index < list.size - 1) {
            var i = 1
            var j = message.index
            while (j < list.size - 1 && list[message.index + i].node == null) {
                if (list[message.index + i].node != null) {
                    break
                }
                i++
                j++
            } // to protect against white space null pointers

            reformat(list[message.index + i].node, false)
        } else if (message.index > 0) reformat(list[message.index - 1].node, false)
        else reformat(manager.document.rootElement.getChild(0), false)
    }
}
