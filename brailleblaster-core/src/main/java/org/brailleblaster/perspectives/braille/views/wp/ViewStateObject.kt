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
package org.brailleblaster.perspectives.braille.views.wp

data class ViewStateObject(
    var currentStart: Int = 0,
    var currentEnd: Int = 0,
    var previousEnd: Int = 0,
    var nextStart: Int = 0,
    var oldCursorPosition: Int = -1,
    var originalStart: Int = 0,
    var originalEnd: Int = 0,
    var currentChar: Int = 0,
    var currentStateMask: Int = 0
) {

    fun adjustStart(`val`: Int) {
        currentStart += `val`
    }

    fun adjustEnd(`val`: Int) {
        currentEnd += `val`
    }

    fun adjustNextStart(`val`: Int) {
        nextStart += `val`
    }

    fun setOriginalPositions(originalStart: Int, originalEnd: Int) {
        this.originalStart = originalStart
        this.originalEnd = originalEnd
    }
}