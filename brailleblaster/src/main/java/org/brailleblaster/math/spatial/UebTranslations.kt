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

import org.brailleblaster.localization.LocaleHandler.Companion.getDefault

object UebTranslations {
    private val localeHandler = getDefault()
    const val PASSAGE_PADDING: Int = 2
    const val MIN_LINE_CHARS: Int = 1
    const val UEB_DECIMAL: String = "4"
    const val BIG_UEB_LEFT_CURLY: String = ",_<"
    const val BIG_UEB_LEFT_ROUND: String = ",\"<"
    const val BIG_UEB_LEFT_SQUARE: String = ",.<"
    const val BIG_UEB_LEFT_STRAIGHT: String = ",_|"
    const val BIG_UEB_RIGHT_ROUND: String = ",\">"
    const val BIG_UEB_RIGHT_SQUARE: String = ",.>"
    const val BIG_UEB_RIGHT_STRAIGHT: String = ",_|"
    const val BIG_UEB_RIGHT_CURLY: String = ",_>"

    const val UEB_LEFT_CURLY: String = "_<"
    const val UEB_LEFT_ROUND: String = "\"<"
    const val UEB_LEFT_SQUARE: String = ".<"
    const val UEB_LEFT_STRAIGHT: String = "_|"
    const val UEB_RIGHT_ROUND: String = "\">"
    const val UEB_RIGHT_SQUARE: String = ".>"
    const val UEB_RIGHT_STRAIGHT: String = "_|"
    const val UEB_RIGHT_CURLY: String = "_>"
    const val NUMBER_CHAR: String = "#"
    const val INTERVAL_ASCII: String = "w"
    const val FILL_ASCII: String = "="
    const val EMPTY_ASCII: String = "y"
    const val BEGIN_ARROW: String = "|{"
    const val END_ARROW: String = "|o"
    const val FRAC: String = "/"
    const val BEVELED_FRAC: String = "_/"
    const val HORIZONTAL_MODE: String = "\""
    const val LINE_ASCII: String = "3"
    var VERTICAL_LINE_STRAIGHT: String = "_"
    var VERTICAL_LINE_CURLY: String = "o"
    const val ELLIPSIS: String = "..."

    const val MINUS: String = "\"\u2212"

    val PASSAGE_OPTIONS: String = localeHandler["passageOptions"]
    const val OMISSION: String = "+"
    const val PRINT_MINUS: String = "-"
    const val REMAINDER_LOWERCASE: String = "r"
}
