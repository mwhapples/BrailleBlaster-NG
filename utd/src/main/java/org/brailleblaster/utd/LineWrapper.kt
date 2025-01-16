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
package org.brailleblaster.utd

import org.brailleblaster.utd.exceptions.NoLineBreakPointException

/**
 * Split a string to fit specific line lengths.
 *
 *
 * This class provides a number of methods useful for splitting strings into chunks of a specified length. As where to split a line can depend upon what type of content and the Braille code being used, this class allows customising where a line break is permitted.
 */
interface LineWrapper {
    open class InsertionResult(@JvmField val insertionDots: String?, @JvmField val insertionPosition: Int)

    class LineBreakResult(@JvmField val lineBreakPosition: Int, dots: String?, insertionPosition: Int) :
        InsertionResult(dots, insertionPosition)

    /**
     * Where or whether to insert Braille cells due to starting a new line.
     *
     *
     * This method will check the Braille to find out where or whether a sequence of Braille cells need inserting due to the Braille being at the start of the line. This method does not actually check whether the Braille is the start of a line, that check should be done prior to calling this method.
     *
     * @param brlText The Braille text.
     * @param start   Where to start checking from.
     * @return The index of where to insert the additional Braille cells. Should no additional Braille cells be inserted then this will return -1.
     */
    fun checkStartLineInsertion(brlText: String, start: Int): InsertionResult

    @Throws(NoLineBreakPointException::class)
    fun findNextBreakPoint(brlText: String, startPoint: Int, lineWrapCheck: Int): LineBreakResult
}
