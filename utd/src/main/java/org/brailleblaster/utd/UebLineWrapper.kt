/*
 * Copyright (C) 2026 American Printing House for the Blind
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

import org.brailleblaster.utd.LineWrapper.LineBreakResult
import org.brailleblaster.utils.braille.BrailleUnicodeConverter

/**
 * Line wrapper for UEB literary text.
 *
 * Breaks lines like [RegexLineWrapper], and divides an unspaced sequence
 * longer than a line (a URL, an email address, a code) with the line
 * continuation indicator, dot 5, at the end of each line.
 */
class UebLineWrapper(private val asciiBraille: Boolean) : LineWrapper by RegexLineWrapper() {
    override fun divideLongWord(word: String, room: Int): LineBreakResult? {
        val text = word.trimEnd(' ', UNICODE_SPACE)
        // a word that fits once its trailing space goes to the next line needs no division
        if (text.length <= room) return null
        // only an unspaced sequence of Braille cells; anything else is cut as before
        if (text.any { it in SPACES }) return null
        if (!asciiBraille && text.any { it !in BRAILLE_CELLS }) return null
        val ascii = (if (asciiBraille) text else BrailleUnicodeConverter.unicodeToAsciiLouis(text)).lowercase()
        // leave a cell for the indicator, and something for the next line
        val last = minOf(room - 1, ascii.length - 1)
        if (last < 1) return null
        val inNumber = numericCells(ascii)
        val allowed = (1..last).filter { canDivide(ascii, inNumber, it) }
        val division = allowed.lastOrNull { isNaturalDivision(ascii, inNumber, it) } ?: allowed.lastOrNull()
            ?: return null
        return LineBreakResult(division, if (asciiBraille) "\"" else UNICODE_DOT_5, division)
    }

    companion object {
        /** Cells that only mean something with the cell after them: prefixes and indicators. */
        private const val PREFIXES = "@^_\".;,`~#"
        private const val NUMERIC = "abcdefghij14"
        private val UNICODE_SPACE = Char(0x2800)
        private val UNICODE_DOT_5 = Char(0x2810).toString()
        private val SPACES = charArrayOf(' ', Char(0xa0), UNICODE_SPACE)
        private val BRAILLE_CELLS = Char(0x2800)..Char(0x28ff)

        /** true for each cell that continues a number started by a number sign */
        private fun numericCells(ascii: String): BooleanArray {
            val result = BooleanArray(ascii.length)
            var numeric = false
            ascii.forEachIndexed { i, c ->
                numeric = if (numeric) c in NUMERIC else false
                result[i] = numeric
                if (c == '#') numeric = true
            }
            return result
        }

        // Dividing before cell p must not split a prefix from its cell or a number from its number sign
        private fun canDivide(ascii: String, inNumber: BooleanArray, p: Int) =
            ascii[p - 1] !in PREFIXES && !inNumber[p]

        // after a slash or hyphen, or before a dot or an at sign
        private fun isNaturalDivision(ascii: String, inNumber: BooleanArray, p: Int) =
            ascii.startsWith("_/", p - 2) || ascii[p - 1] == '-' ||
                (ascii[p] == '4' && !inNumber[p]) || ascii.startsWith("@a", p)
    }
}
