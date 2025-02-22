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

import kotlinx.serialization.Serializable
import org.brailleblaster.math.spatial.MatrixConstants.BracketType
import org.brailleblaster.math.spatial.MatrixConstants.Wide
import org.brailleblaster.math.spatial.SpatialMathEnum.Passage
import org.brailleblaster.math.spatial.SpatialMathEnum.Translation

@Serializable
class MatrixJson @JvmOverloads constructor(
    private var rows: Int = 0,
    private var cols: Int = 0,
    private var wideType: Wide = MatrixSettings.DEFAULT_WIDE,
    private var bracket: BracketType = MatrixSettings.DEFAULT_BRACKET,
    private var passage: Passage = Passage.NONE,
    private var translation: Translation? = null,
    private var matrixCells: MutableList<MutableList<MatrixCell>> = mutableListOf()
) : ISpatialMathContainerJson {

    override fun jsonToContainer(): Matrix {
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
}

fun Matrix.createMatrixJson(): MatrixJson = MatrixJson(
    rows = settings.rows,
    cols = settings.cols,
    wideType = settings.wideType,
    bracket = settings.bracketType,
    passage = settings.passage,
    translation = settings.translation,
    matrixCells = settings.model
)