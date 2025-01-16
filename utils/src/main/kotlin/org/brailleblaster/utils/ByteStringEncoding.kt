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
package org.brailleblaster.utils

import kotlin.code
import kotlin.collections.joinToString
import kotlin.ranges.contains
import kotlin.ranges.downTo
import kotlin.ranges.step
import kotlin.text.format
import kotlin.text.indexOf
import kotlin.text.iterator

object ByteStringEncoding {
    private const val BACK_SLASH: Byte = 0x5C
    private const val LF: Byte = 0x0A
    private const val CR: Byte = 0x0D
    private const val FF: Byte = 0x0C
    private const val TAB: Byte = 0x09
    private const val HEX_CHARS = "0123456789abcdef"
    fun decode(chars: CharSequence): ByteArray {
        val iter = chars.iterator()
        val buffer = java.io.ByteArrayOutputStream(chars.length * 4)
        while (iter.hasNext()) {
            val b: Int = when(val c = iter.nextChar()) {
                '\\' -> parseEscapeSequence(iter)
                in '\u0000'..'\u00ff' -> c.code
                else -> throw IllegalArgumentException("Input contains invalid byte ${c.code}")
            }
            buffer.write(b)
        }
        return buffer.toByteArray()
    }

    private fun parseEscapeSequence(iter: CharIterator) = if (iter.hasNext()) {
        when (val escapeLetter = iter.nextChar()) {
            '\\' -> BACK_SLASH
            'f' -> FF
            't' -> TAB
            'r' -> CR
            'n' -> LF
            'x' -> parseHexByte(iter)
            else -> throw IllegalArgumentException("Invalid escape sequence found \\${escapeLetter}")
        }.toInt()
    } else {
        throw IllegalArgumentException("Escape char \\ must be followed by a character")
    }

    private fun parseHexByte(iter: CharIterator): Int {
        var hexValue = 0
        for (i in 4 downTo 0 step 4) {
            if (iter.hasNext()) {
                val hc = iter.nextChar()
                val v = HEX_CHARS.indexOf(hc)
                if (v >= 0) {
                    hexValue += v shl i
                } else throw java.lang.IllegalArgumentException("Character $hc is not a hex character.")
            } else throw java.lang.IllegalArgumentException("Not enough characters for hex escape sequence")
        }
        return hexValue
    }

    fun encode(bytes: ByteArray): String {
        return bytes.joinToString("") { b ->
            when(b) {
                BACK_SLASH -> "\\\\"
                LF -> "\\n"
                CR -> "\\r"
                FF -> "\\f"
                TAB -> "\\t"
                in 0x20..0x7E -> Char(b.toInt()).toString()
                else -> "\\x%02x".format(b)
            }
        }
    }
}