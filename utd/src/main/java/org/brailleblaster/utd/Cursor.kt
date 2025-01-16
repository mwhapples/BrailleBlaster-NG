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
package org.brailleblaster.utd

import nu.xom.Element
import org.brailleblaster.libembosser.spi.BrlCell
import org.brailleblaster.utd.properties.UTDElements

data class Cursor @JvmOverloads constructor(var x: Double = 0.0, var y: Double = 0.0) {

    constructor(existingCursor: Cursor) : this(existingCursor.x, existingCursor.y)

    fun moveX(moveBy: Double) {
        x += moveBy
    }

    fun moveY(moveBy: Double) {
        y += moveBy
    }

    @JvmOverloads
    fun moveAfter(element: Element, style: IStyle?, cell: BrlCell = BrlCell.NLS) {
        val elementName = element.localName
        when (UTDElements.getByName(elementName)) {
            UTDElements.MOVE_TO -> {
                var moveTo = element.getAttributeValue("hPos").toDouble()
                x = moveTo
                moveTo = element.getAttributeValue("vPos").toDouble()
                y = moveTo
            }
            UTDElements.NEW_PAGE -> {
            }
            UTDElements.BRLONLY -> {
                x = cell.getWidthForCells(element.value.length).toDouble()
            }
            else -> {
            }
        }
    }

    init {
        require(!(x < 0 || y < 0))
    }
}