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

import nu.xom.Attribute
import nu.xom.Element
import org.brailleblaster.bbx.BBX
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.elements.WhiteSpaceElement
import org.brailleblaster.perspectives.braille.mapping.maps.MapList
import org.brailleblaster.perspectives.braille.messages.InsertNodeMessage
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.braille.viewInitializer.ViewInitializer
import org.brailleblaster.perspectives.mvc.events.ModifyEvent
import java.util.*

class ImagePlaceholderHandler(manager: Manager?, vi: ViewInitializer?, list: MapList?) : Handler(
    manager!!, vi!!, list!!
) {
    fun adjustImagePlaceholder(lines: Int, imagePath: String?) {
        list.current.nodeParent?.let { e ->
            val parent = e.parent

            if (lines == 0) parent.removeChild(e)
            else BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_SKIP_LINES[e] = lines

            updateImagePlaceholderPath(imagePath, e)
            //		manager.getSimpleManager().dispatchEvent(new ModifyEvent(Sender.TEXT, false, parent));
        }
    }

    fun insertNewImagePlaceholder(lines: Int, imagePath: String?) {
        var atStart = false
        var atEnd = false
        val posList: List<Int>

        if (list.current is WhiteSpaceElement) {
            val newPlaceholder = BBX.BLOCK.IMAGE_PLACEHOLDER.create()
            newPlaceholder.addAttribute(BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_SKIP_LINES.newAttribute(lines))
            if (imagePath != null) {
                newPlaceholder.addAttribute(BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_IMG_PATH.newAttribute(imagePath))
            }

            val wt = WhitespaceTransformer(manager)
            wt.transformWhiteSpace(list.current as WhiteSpaceElement, newPlaceholder)
        } else {
            //TODO: What is the point of all this? This is the only class that uses InsertNodeMessage
            posList = list.findTextMapElementRange(
                list.currentIndex,
                list.current.nodeParent
            )
            val caretPos = text.view.caretOffset
            if (caretPos < list[posList[0]].getStart(list) && caretPos > list[posList[posList.size - 1]].getEnd(list)) {
                atEnd = true
            } else {
                val startDistance = caretPos - list[posList[0]].getStart(list)
                val endDistance = list[posList[posList.size - 1]].getEnd(list) - caretPos

                if (startDistance <= endDistance) atStart = true
                else atEnd = true
            }
            val attrs = LinkedList<Attribute>()
            attrs.add(BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_SKIP_LINES.newAttribute(lines))
            if (imagePath != null) {
                attrs.add(BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_IMG_PATH.newAttribute(imagePath))
            }

            val m = InsertNodeMessage(atStart, atEnd, BBX.BLOCK.IMAGE_PLACEHOLDER, null, attrs)
            val ieh = InsertElementHandler(manager, vi, list)
            ieh.insertElement(m)
        }
    }

    fun updateImagePlaceholderPath(imagePath: String?, e: Element? = list.current.nodeParent) {
        if (imagePath == null || e == null) {
            return
        }
        //You have to check that you have an attribute skip lines or else don't add a source
        if (BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_SKIP_LINES.getAttribute(e) != null) {
            //If you don't have a path yet, set
            if (BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_IMG_PATH.getAttribute(e) != null) {
                BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_IMG_PATH[e] = imagePath
            } else {
                BBX.BLOCK.IMAGE_PLACEHOLDER.ATTRIB_IMG_PATH.newAttribute(imagePath)
            }
        }

        manager.simpleManager.dispatchEvent(ModifyEvent(Sender.TEXT, false, e.parent))
    }
}
