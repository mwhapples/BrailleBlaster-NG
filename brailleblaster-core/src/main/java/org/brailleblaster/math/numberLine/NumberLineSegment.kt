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
package org.brailleblaster.math.numberLine

import org.brailleblaster.math.spatial.SpatialMathEnum.Fill

class NumberLineSegment(
    var startSegmentCircle: Fill = Fill.NONE,
    var endSegmentCircle: Fill = Fill.NONE,
    var segmentStart: NumberLineComponent = NumberLineComponent(),
    var segmentEnd: NumberLineComponent = NumberLineComponent(),
    var startInterval: Int = 1,
    var endInterval: Int = 1
) : Comparable<NumberLineSegment> {

    val isPoint: Boolean
        get() = endInterval - startInterval == 0

    override fun compareTo(other: NumberLineSegment): Int {
        return this.startInterval.compareTo(other.startInterval)
    }
    companion object {
        fun createDefaultNumberLineSegment(): NumberLineSegment = NumberLineSegment()
    }
}