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
package org.brailleblaster.utd.utils

import org.slf4j.LoggerFactory
import java.util.*

/**
 * Fast implementation of mapping liblouis braille unicode to ascii and back for UEB
 */
object BrailleUnicodeConverter {
    private val log = LoggerFactory.getLogger(BrailleUnicodeConverter::class.java)
    const val UPPERCASE_ASCII_BRAILLE = " A1B'K2L@CIF/MSP\"E3H9O6R^DJG>NTQ,*5<-U8V.%[$+X!&;:4\\0Z7(_?W]#Y)="

    // Source: https://en.wikipedia.org/wiki/Braille_ASCII#Braille_ASCII_values
    // It's uppercase though, liblouis uses the lowercase equivelant
    @JvmField
    val LOWERCASE_ASCII_BRAILLE = UPPERCASE_ASCII_BRAILLE
        .replace('[', '{')
        .replace(']', '}')
        .replace('\\', '|')
        .replace('^', '~')
        .replace('@', '`')
        .lowercase(Locale.getDefault())

    fun unicodeToAsciiUppercase(unicodeString: String): String {
        return unicodeToAscii(UPPERCASE_ASCII_BRAILLE, unicodeString)
    }

    fun unicodeToAsciiLouis(unicodeString: String): String {
        return unicodeToAscii(LOWERCASE_ASCII_BRAILLE, unicodeString)
    }

    private fun unicodeToAscii(asciiMap: String?, unicodeString: String): String {
        val cbuf = unicodeString.toCharArray()
        unicodeToAscii(asciiMap, cbuf, 0, cbuf.size)
        return String(cbuf)
    }

    fun unicodeToAsciiUppercase(cbuf: CharArray, offset: Int, length: Int) {
        unicodeToAscii(UPPERCASE_ASCII_BRAILLE, cbuf, offset, length)
    }

    fun unicodeToAsciiLouis(cbuf: CharArray, offset: Int, length: Int) {
        unicodeToAscii(LOWERCASE_ASCII_BRAILLE, cbuf, offset, length)
    }

    /**
     * This signature is useful in Reader
     *
     * @param cbuf
     * @param offset
     * @param length
     */
    private fun unicodeToAscii(asciiMap: String?, cbuf: CharArray, offset: Int, length: Int) {
        for (i in offset until offset + length) {
            var orig = cbuf[i]
            if (orig in '\u2800'..'\u287F') {
                if (orig >= '\u2840') {
                    // 8-dot braille for un-needed capitalization, throw away bits 7 and 8
                    val start = orig
                    orig = Char(orig.code and 0xC0.inv())
                    log.trace(
                        "Converted "
                                + Character.getName(start.code)
                                + " to "
                                + Character.getName(orig.code)
                                + " ascii "
                                + asciiMap!![orig - '\u2800']
                    )
                }

                // Convert to ascii
                val asciiBrailleOffset = orig - '\u2800'
                cbuf[i] = asciiMap!![asciiBrailleOffset]
                // hack for translated unicode characters in the format '\x1234'
                if (cbuf[i] == '|'
                    && i >= offset + 1
                    && i <= offset + length - 1
                    && cbuf[i - 1] == '\''
                    && cbuf[i + 1] == '\u282D') {
                        cbuf[i] = '\\'
                }
            }
        }
    }

    fun asciiToUnicodeUppercase(asciiString: String): String {
        return asciiToUnicode(UPPERCASE_ASCII_BRAILLE, asciiString)
    }

    fun asciiToUnicodeLouis(asciiString: String): String {
        return asciiToUnicode(LOWERCASE_ASCII_BRAILLE, asciiString)
    }

    private fun asciiToUnicode(asciiMap: String?, asciiString: String): String {
        val cbuf = asciiString.toCharArray()
        asciiToUnicode(asciiMap, cbuf, 0, cbuf.size)
        return String(cbuf)
    }

    fun asciiToUnicodeUppercase(cbuf: CharArray, offset: Int, length: Int) {
        asciiToUnicode(UPPERCASE_ASCII_BRAILLE, cbuf, offset, length)
    }

    fun asciiToUnicodeLouis(cbuf: CharArray, offset: Int, length: Int) {
        asciiToUnicode(LOWERCASE_ASCII_BRAILLE, cbuf, offset, length)
    }

    /** This signature is useful in Reader  */
    private fun asciiToUnicode(asciiMap: String?, cbuf: CharArray, offset: Int, length: Int) {
        for (i in offset until offset + length) {
            var orig = cbuf[i].code
            log.trace(
                "----- Current char {} {} {}",
                Integer.toHexString(orig),
                cbuf[i],
                Character.getName(orig)
            )
            // hack for translated unicode characters in the format '\x1234'
            if (cbuf[i] == '\\' /*braille backslash/"REVERSE SOLIDUS"*/
                && i >= offset + 1
                && i <= offset + length - 1
                && cbuf[i - 1] == '\u2804'
                /*braille '*/
                && cbuf[i + 1] == 'x') {
                    log.trace("Using special liblouis marker")
                    // special liblouis marker with dot 7?
                    cbuf[i] = '\u2873'
                    continue
            }
            if (orig in 0x41..0x5A && asciiMap == LOWERCASE_ASCII_BRAILLE) {
                // Is ascii upper case, need to convert to lower case as that's what liblouis generates
                orig += 32
                //				log.trace("upper case {} to upper case {}", Character.getName(before),
                // Character.getName(orig));
            }
            val brlOffset = asciiMap!!.indexOf(orig.toChar())
            if (brlOffset != -1) {
                // Shift to braille unicode range which is based on the ascii range
                orig = brlOffset + 0x2800
                cbuf[i] = orig.toChar()
                log.trace(
                    "Converted to brl {} - {} {}",
                    brlOffset,
                    Integer.toHexString(orig),
                    Character.getName(orig)
                )
            } else {
                log.trace("Ignoring " + " " + Integer.toHexString(orig) + " " + Character.getName(orig))
            }
        }
    }

}