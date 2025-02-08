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
import org.slf4j.LoggerFactory

class MatrixSettings : ISpatialMathSettings {
    var rows = DEFAULT_ROWS
        set(value) {
            field = value
            buildModel()
        }
    var cols = DEFAULT_COLS
        set(value) {
            field = value
            buildModel()
        }
    var bracketType: BracketType = DEFAULT_BRACKET
    var wideType: Wide = DEFAULT_WIDE
    var isAddEllipses = false
    var model: MutableList<MutableList<MatrixCell>> = mutableListOf()
    override var passage = Passage.NONE
    var translation: Translation? = Translation.ASCII_MATH

    init {
        buildModel()
    }

    private fun buildModel() {
        for (i in 0 until rows) {
            if (model.size < i + 1) {
                val row = ArrayList<MatrixCell>()
                model.add(row)
            }
        }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (model[i].size < j + 1) {
                    model[i].add(MatrixCell("", false))
                }
            }
        }
    }

    fun setModelFromArray(array: ArrayList<MatrixCell>) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val index = i * cols + j
                if (array.size > index) {
                    model[i][j] = array[index]
                }
            }
        }
    }

    val ellipsesArray: ArrayList<String>
        get() {
            val array = ArrayList<String>()
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    array.add(model[i][j].isEllipsis.toString())
                }
            }
            return array
        }

    companion object {
        private val log = LoggerFactory.getLogger(MatrixSettings::class.java)
        @JvmField
		val DEFAULT_BRACKET = BracketType.BIG_SQUARE
        const val DEFAULT_COLS = 3
        const val DEFAULT_ROWS = 3
        @JvmField
		val DEFAULT_WIDE = Wide.BLOCK_BLANK
        fun enumifyBracket(s: String): BracketType {
            for (b in BracketType.entries) {
                if (b.label == s) {
                    return b
                }
            }
            log.error("Combo box does not match bracket enum options, using default")
            return DEFAULT_BRACKET
        }

        @JvmStatic
		fun enumifyWide(s: String): Wide {
            for (w in Wide.entries) {
                if (w.label == s) {
                    return w
                }
            }
            log.error("Combo box does not match wide enum options, using default")
            return DEFAULT_WIDE
        }

        @JvmStatic
		fun combineCellComponents(text: ArrayList<String>, ellipses: ArrayList<String?>): ArrayList<MatrixCell> {
            val array = ArrayList<MatrixCell>()
            for ((i,t) in text.withIndex()) {
                val hasEllipsis = ellipses[i].toBoolean()
                array.add(MatrixCell(if (hasEllipsis) "" else t, hasEllipsis))
            }
            return array
        }
    }
}