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
package org.brailleblaster.perspectives.braille.mapping.elements

import org.brailleblaster.perspectives.braille.mapping.maps.MapList

class SectionElement {
    //Element parent;
	@JvmField
	val list: MapList
    var isVisible: Boolean
    var charCount: Int
    @JvmField
	var pages: Int

    constructor(list: MapList, chars: Int) {
        this.list = list
        this.charCount = chars
        isVisible = false
        this.pages = 0
    }

    constructor(list: MapList) {
        this.list = list
        this.charCount = 0
        this.pages = 0
        isVisible = false
    }

    fun setInView(inView: Boolean) {
        this.isVisible = inView
    }

    fun resetList() {
        isVisible = false
        list.resetList()
    }

    fun setChars(chars: Int) {
        this.charCount = chars
    }

    fun incrementPages() {
        pages++
    }
}
