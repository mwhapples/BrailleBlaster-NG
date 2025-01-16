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
package org.brailleblaster.utd.internal.elements

import nu.xom.Element
import nu.xom.Text

/**
 * Specialised XOM element for handling page numbers.
 *
 *
 * This offers an easy way to construct and handle page numbers in UTD. When using this class you
 * are guaranteed that the page number element will remain valid UTD.
 */
abstract class PageNumber protected constructor(proto: Element?, pageNumber: String) : Element(proto) {
    var pageNumber: String
        get() = getChild(0).value
        set(pageNumber) {
            (getChild(0) as Text).value = pageNumber
        }

    init {
        super.appendChild(pageNumber)
    }
}