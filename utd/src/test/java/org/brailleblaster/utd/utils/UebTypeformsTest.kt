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

import org.brailleblaster.utd.BrailleSettings
import org.brailleblaster.utd.UTDTranslationEngine
import org.brailleblaster.utd.properties.BrailleTableType
import org.mwhapples.jlouis.Louis.TypeForms
import org.testng.Assert.assertEquals
import org.testng.annotations.Test

/**
 * UEB typeform changes made before liblouis translates, with sentences from
 * ASM's statements.
 */
class UebTypeformsTest {
    private val engine = UTDTranslationEngine().apply {
        brailleSettings = BrailleSettings(mainTranslationTable = "en-us-brf.dis,en-ueb-g2.ctb", isUseAsciiBraille = true)
    }

    @Test
    fun noTerminatorBeforeClosingPunctuationAfterWordIndicators() {
        // Rules of UEB 9.7.3: the full stop isn't underlined in print, but needs no terminator
        val text = "any potential errors in writing. You may call us"

        assertEquals(translate(text, underline(text, "in writing")), "ANY POT5TIAL ]RORS _19 _1WRIT+4 ,Y MAY CALL U")
    }

    @Test
    fun passageKeepsTerminatorBeforePunctuation() {
        // Rules of UEB 9.7.2: a passage needs its terminator, placed where the typeform changes
        val text = "any potential errors in writing. You may"

        assertEquals(translate(text, underline(text, "potential errors in writing")), "ANY _7POT5TIAL ]RORS 9 WRIT+_'4 ,Y MAY")
    }

    @Test
    fun noContractionInLetterAbbreviation() {
        // Rules of UEB 10.12.1: EST (Eastern Standard Time) is said letter by letter
        val text = "received by 5 p.m. EST on a normal business day"

        assertEquals(translate(text, null), "RCVD BY #E P4M4 ,,EST ON A NORMAL BUSI;S \"D")
    }

    @Test
    fun lowerCaseWordKeepsContraction() {
        assertEquals(translate("id est", null), "ID E/")
    }

    private fun translate(text: String, typeforms: ShortArray?): String =
        TextTranslator.translateTextWithEmpArray(text, engine, typeforms, BrailleTableType.LITERARY).translation.uppercase()

    private fun underline(text: String, words: String): ShortArray {
        val start = text.indexOf(words)
        return ShortArray(text.length) { if (it in start until start + words.length) TypeForms.UNDERLINE else TypeForms.PLAIN_TEXT }
    }
}
