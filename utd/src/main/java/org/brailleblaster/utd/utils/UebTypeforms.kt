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
package org.brailleblaster.utd.utils

import org.mwhapples.jlouis.Louis.TypeForms

/**
 * Typeform changes for UEB that liblouis can't make from its tables, applied
 * to the typeforms passed with the text.
 */
object UebTypeforms {
    /** Typeforms whose short runs liblouis marks with word indicators */
    private val WORD_INDICATOR_TYPEFORMS = listOf(TypeForms.ITALIC, TypeForms.UNDERLINE, TypeForms.BOLD, TypeForms.SCRIPT)

    /** lenemphphrase in en-ueb-g1.ctb: three or more symbols-sequences are a passage */
    private const val PASSAGE_LENGTH = 3

    private const val CLOSING_PUNCTUATION = ".,;:!?)]}\"'”’…"

    /**
     * Abbreviations said letter by letter whose letters would otherwise make
     * up a contraction, written fully capitalised
     */
    private val LETTER_ABBREVIATIONS = Regex("(?<![\\p{L}\\p{N}])(?:EST|EDT|CST|MST|PST|AST|HST|AKST)(?![\\p{L}\\p{N}])")

    /** The typeforms to translate [text] with, from [typeforms] (all plain text when null). */
    fun adjust(text: String, typeforms: ShortArray?): ShortArray =
        (typeforms?.copyOf() ?: ShortArray(text.length)).also { result ->
            WORD_INDICATOR_TYPEFORMS.forEach { includeClosingPunctuation(text, result, it) }
            noContractions(text, result)
        }

    /**
     * Rules of UEB 9.7.3: ignore a change in typeform for closing punctuation
     * when a typeform word indicator is used, so no terminator comes before
     * it. Closing punctuation straight after a short run of [typeform] and
     * before a space joins the run.
     */
    private fun includeClosingPunctuation(text: String, typeforms: ShortArray, typeform: Short) {
        fun has(i: Int) = typeforms[i].toInt() and typeform.toInt() != 0
        runs(text.indices.filter(::has)).forEach { run ->
            val punctuationEnd = (run.last + 1 until text.length).firstOrNull { text[it] !in CLOSING_PUNCTUATION } ?: text.length
            val words = text.substring(run.first, run.last + 1).split(Regex("\\s+")).count { it.isNotEmpty() }
            val endsWord = punctuationEnd == text.length || text[punctuationEnd].isWhitespace()
            if (punctuationEnd > run.last + 1 && endsWord && words < PASSAGE_LENGTH) {
                (run.last + 1 until punctuationEnd).forEach { typeforms[it] = (typeforms[it].toInt() or typeform.toInt()).toShort() }
            }
        }
    }

    /** Rules of UEB 10.12.1: no contractions in an abbreviation whose letters are said separately. */
    private fun noContractions(text: String, typeforms: ShortArray) {
        LETTER_ABBREVIATIONS.findAll(text).flatMap { it.range }.forEach {
            typeforms[it] = (typeforms[it].toInt() or TypeForms.NO_CONTRACT.toInt()).toShort()
        }
    }

    // Consecutive indices as ranges
    private fun runs(indices: List<Int>): List<IntRange> =
        indices.fold(mutableListOf<IntRange>()) { acc, i ->
            if (acc.isNotEmpty() && acc.last().last == i - 1) acc[acc.size - 1] = acc.last().first..i else acc.add(i..i)
            acc
        }
}
