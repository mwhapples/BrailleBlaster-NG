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
package org.brailleblaster.perspectives.braille.viewInitializer

import nu.xom.Element
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.document.BrailleDocument
import org.brailleblaster.perspectives.braille.mapping.elements.SectionElement
import org.brailleblaster.perspectives.braille.mapping.maps.MapList
import org.brailleblaster.perspectives.braille.views.wp.BrailleRenderer
import org.brailleblaster.perspectives.braille.views.wp.BrailleView
import org.brailleblaster.perspectives.braille.views.wp.TextRenderer
import org.brailleblaster.perspectives.braille.views.wp.TextView

class WebInitializer(doc: BrailleDocument?, text: TextView?, braille: BrailleView?) :
    ViewInitializer(doc!!, text!!, braille!!) {
    init {
        sectionList = ArrayList()
    }

    override fun findSections(m: Manager, e: Element) {
        val list = MapList(m)
        sectionList.add(SectionElement(list, 0))
        initializeViews(e, m, 0)
    }

    override fun initializeViews(m: Manager) {
        findSections(m, document.rootElement)

        val tr = TextRenderer(m, text)
        val br = BrailleRenderer(m, braille)
        if (sectionList.size == 1 && sectionList[0].list.isEmpty()) {
            appendToViews(tr, br, sectionList[0].list, 0)
            sectionList[0].setInView(true)
        } else {
            var i = 0
            while (i < sectionList.size && text.view.charCount < CHAR_COUNT) {
                appendToViews(tr, br, sectionList[i].list, 0)
                sectionList[i].setInView(true)
                i++
            }
        }
    }
}
