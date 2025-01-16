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

import nu.xom.Element
import org.brailleblaster.BBIni
import org.brailleblaster.bbx.BBX
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.elements.BoxLineTextMapElement
import org.brailleblaster.perspectives.braille.mapping.elements.PageIndicatorTextMapElement
import org.brailleblaster.perspectives.braille.mapping.elements.TextMapElement
import org.brailleblaster.perspectives.braille.mapping.elements.WhiteSpaceElement
import org.brailleblaster.perspectives.braille.mapping.maps.MapList
import org.brailleblaster.perspectives.braille.viewInitializer.ViewInitializer
import org.brailleblaster.utd.actions.SkipAction
import org.brailleblaster.utd.properties.UTDElements

class HideActionHandler(manager: Manager?, vi: ViewInitializer?, list: MapList?) : Handler(
    manager!!, vi!!, list!!
) {
    var boxlineAdded: Boolean = false

    fun hideText() {
        if (!list.isEmpty() && text.view.charCount > 0) {
            if (text.isMultiSelected) hideMultipleElements()
            else if (list.current !is BoxLineTextMapElement) hideSingleElement()
            else invalidSelection()
        }
    }

    private fun hideSingleElement() {
        text.update(false)
        boxlineAdded = false
        var t = manager.mapList.current
        if (t is WhiteSpaceElement) {
            t = manager.mapList.findClosestNonWhitespace(t)!!
            manager.mapList.setCurrent(manager.mapList.indexOf(t))
        }
        hide(list.current)
        reformat(list.current.nodeParent, false)
    }

    private fun hideMultipleElements() {
        val start = text.selectedText[0]
        val end = text.selectedText[0] + text.selectedText[1]

        val itemSet = manager.mapList.getElementInSelectedRange(start, end)
        val invalid = checkSelection(itemSet, start, end)
        var index: Int? = null
        if (!invalid) {
            val itr = itemSet.iterator()
            boxlineAdded = false
            while (itr.hasNext()) {
                val tempElement = itr.next()
                if (index == null) index = list.indexOf(tempElement)

                hide(tempElement)
            }


            //text.clearSelection();	
            reformat(list.current.nodeParent, false)


            //clear selection in a way that cursor remains in same position
            text.setCurrentSelection(text.view.caretOffset, text.view.caretOffset)
        }
    }

    /** Helper method for hideMultipleElemetns method to check whether selection is valid
     * @param itemSet : set containing elements in selection
     * @param start : start of selection
     * @param end : end of selection
     * @return true if valid selection, false if invalid
     */
    private fun checkSelection(itemSet: MutableSet<TextMapElement>, start: Int, end: Int): Boolean {
        var invalid = false
        val addToSet = ArrayList<TextMapElement>()
        for (tempElement in itemSet) {
            if (tempElement is BoxLineTextMapElement) {
                val b = list.findJoiningBoxline(tempElement)
                if (b == null || b.getStart(manager.mapList) > end || b.getEnd(manager.mapList) < start) {
                    invalid = true
                    invalidSelection()
                    break
                } else if (!itemSet.contains(b)) addToSet.add(b)
            }
        }

        if (addToSet.isNotEmpty()) {
            itemSet.addAll(addToSet)
        }

        return invalid
    }

    private fun hide(t: TextMapElement) {
        val block = getParent(t)

        if (isBoxLine(block)) boxlineAdded = true

        removeBraille(block)
        manager.document.changeAction(SkipAction(), block)


        //If node was the only child of a list or similar container, hide that as well
        var parent = block.parent
        while (parent.childCount == 1) {
            if (BBX.SECTION.isA(parent)) {
                break
            }

            manager.document.changeAction(SkipAction(), parent as Element)
            parent = parent.getParent()
        }
    }

    private fun removeBraille(e: Element) {
        val els = e.childElements
        for (i in 0 until els.size()) {
            if (UTDElements.BRL.isA(els[i])) e.removeChild(els[i])
            else removeBraille(els[i])
        }
    }

    private fun getParent(current: TextMapElement): Element {
        val parent = if (current is PageIndicatorTextMapElement || current is BoxLineTextMapElement) current.nodeParent
        else manager.document.getParent(current.node)

        return parent
    }

    private fun invalidSelection() {
        if (!BBIni.debugging) manager.notify("In order to hide a boxline both opening and closing boxlines must be selected")
    }
}
