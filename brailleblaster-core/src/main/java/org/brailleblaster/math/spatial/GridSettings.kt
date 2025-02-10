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
package org.brailleblaster.math.spatial

import org.brailleblaster.math.spatial.SpatialMathEnum.Passage
import org.brailleblaster.math.spatial.SpatialMathEnum.SpatialMathContainers

class GridSettings : ISpatialMathSettings {
    var defaultType = SpatialMathContainers.CONNECTING
    override var passage = Passage.NONE
    var rows = 1
    var cols = 1
    var rowIndex = 0
    var colIndex = 0
    var isTranslateIdentifierAsMath = false
}