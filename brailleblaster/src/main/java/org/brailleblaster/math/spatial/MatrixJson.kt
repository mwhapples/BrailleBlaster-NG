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

import org.brailleblaster.math.spatial.MatrixConstants.BracketType
import org.brailleblaster.math.spatial.MatrixConstants.Wide
import org.brailleblaster.math.spatial.SpatialMathEnum.Passage
import org.brailleblaster.math.spatial.SpatialMathEnum.Translation

class MatrixJson : ISpatialMathContainerJson {
    var rows = 0
    var cols = 0
    private var wideType: Wide = MatrixSettings.DEFAULT_WIDE
    var bracket: BracketType = MatrixSettings.DEFAULT_BRACKET
    var passage: Passage = Passage.NONE
    var translation: Translation? = null
    private var matrixCells: MutableList<MutableList<MatrixCell>> = ArrayList()
    override fun jsonToContainer(): ISpatialMathContainer {
        val matrix = Matrix()
        matrix.settings.rows = rows
        matrix.settings.cols = cols
        matrix.settings.wideType = wideType
        matrix.settings.bracketType = bracket
        matrix.settings.passage = passage
        matrix.settings.translation = translation
        matrix.settings.model = matrixCells
        return matrix
    }

    override fun containerToJson(container: ISpatialMathContainer): ISpatialMathContainerJson {
        val matrix = container as Matrix
        rows = matrix.settings.rows
        cols = matrix.settings.cols
        wideType = matrix.settings.wideType
        bracket = matrix.settings.bracketType
        passage = matrix.settings.passage
        translation = matrix.settings.translation
        matrixCells = matrix.settings.model
        return this
    }
}