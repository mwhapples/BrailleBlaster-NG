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

import org.brailleblaster.utd.properties.Align

/** Information about placement of a line segment.  */
class SegmentInfo {
    /**
     * The left indent of the line segment.
     *
     * @return The left indent.
     */
    /**
     * Set the left indent of the line segment.
     */
    var leftIndent = 0
    /**
     * Get the right indent of the line segment.
     *
     * @return The right indent.
     */
    /**
     * Set the right indent of the line segment.
     */
    var rightIndent = 0
    /**
     * Get the alignment of the line segment.
     *
     * @return The alignment of the line segment.
     */
    /**
     * Set the line segment alignment.
     */
    var alignment: Align? = null
    /**
     * Is the segment newly started.
     *
     * @return True if the segment is newly started, false if text already belongs to the segment.
     */
    /**
     * Set whether the segment is newly started.
     * false otherwise.
     */
    var isBeginning = true
}