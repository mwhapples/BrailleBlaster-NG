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
package org.brailleblaster.util

import org.brailleblaster.utd.BRFWriter
import org.brailleblaster.wordprocessor.WPManager

/**
 * Class for add margin to String value (used on PrintPreview and EmbossersManager)
 */
class BrfMargin(wpManager: WPManager?) {
    private val numLeftLines = 0
    private val numTopLines = 0
    private val addMargin = false
    fun addMarginToBrf(textBrf: String): String {
        val brfOutput = StringBuilder()
        val output = StringBuilder(textBrf)
        return if (addMargin) {
            setMarginTopLines(brfOutput)
            for (i in output.indices) {
                val curChar = output[i]
                when (curChar) {
                    '\n' -> {
                        brfOutput.append(BRFWriter.NEWLINE)
                        setMarginLeftCells(brfOutput)
                        continue
                    }

                    '\u000c' -> {
                        brfOutput.append(BRFWriter.PAGE_SEPARATOR)
                        if (i < output.length - 1) {
                            setMarginTopLines(brfOutput)
                        }
                        continue
                    }
                }
                brfOutput.append(curChar)
            }
            brfOutput.toString()
        } else textBrf
    }

    fun setMarginTopLines(output: StringBuilder): StringBuilder {
        output.append(BRFWriter.NEWLINE.toString().repeat(numTopLines.coerceAtLeast(0)))
        setMarginLeftCells(output)
        return output
    }

    fun setMarginLeftCells(output: StringBuilder): StringBuilder {
        output.append(" ".repeat(numLeftLines.coerceAtLeast(0)))
        return output
    }
}