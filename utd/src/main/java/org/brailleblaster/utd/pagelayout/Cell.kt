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
package org.brailleblaster.utd.pagelayout

import nu.xom.Node

/**
 * Used to represent a location within the page grid.
 *
 *
 * This handles mapping from the grid to nodes and the character
 * representation for the location.
 */
class Cell(
    /**
     * The node this cell references.
     */
    var node: Node?,
    /**
     * The index used to locate the actual character in the referenced
     * node.
     */
    var index: Int,
    /**
     * The positioning information for the line segment this cell belongs to.
     */
    var segmentInfo: SegmentInfo? = null
) {

    val char: Char
        /**
         * Get the character representation of this cell.
         *
         * @return The Braille character in this cell.
         */
        get() {
            val reservedSpace = ' '
            if (node == null) {
                return reservedSpace
            }
            val nodeValue = node!!.value
            if (this.index < 0 || index >= nodeValue.length) {
                return reservedSpace
            }
            return nodeValue[index]
        }

    override fun toString(): String {
        return char.toString()
    }

    fun copy(): Cell {
        return Cell(node, index)
    }

    fun stripNode() {
        if (node != null) {
            node = null
        }
    }
}
