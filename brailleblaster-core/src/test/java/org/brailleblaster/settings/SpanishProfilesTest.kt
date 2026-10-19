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
package org.brailleblaster.settings

import org.brailleblaster.BBIni
import org.brailleblaster.bbx.BookToBBXConverter
import org.brailleblaster.cli.BrfCommand
import org.brailleblaster.utd.BrailleSettings
import org.brailleblaster.utd.config.UTDConfig
import org.testng.Assert.assertEquals
import org.testng.Assert.assertNotEquals
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import picocli.CommandLine

/**
 * Spanish comes as grade 1 (SPANISH-US) and grade 2 (SPANISH-US-CONTRACTED).
 * Both appear in the Translation settings menu and work with --braille-profile,
 * which find profiles from their brailleSettings files.
 */
class SpanishProfilesTest {
    @BeforeClass
    fun init() {
        BookToBBXConverter.devSetup(arrayOf())
    }

    private fun profile(name: String): BrailleSettings =
        UTDConfig.loadBrailleSettings(BBIni.loadAutoProgramDataFile(UTDManager.UTD_FOLDER, name + UTDManager.BRAILLE_SETTINGS_NAME))

    // UTDManager points liblouis at BrailleBlaster's tables
    private fun translate(settings: BrailleSettings, text: String): String =
        UTDManager().engine.brailleTranslator.translateString(settings.mainTranslationTable, text, 0)

    @Test
    fun gradeOneProfileIsUnchanged() {
        val settings = profile("SPANISH-US")

        assertEquals(settings.mainTranslationTable, "us-table.dis,es-g1.ctb")
        assertEquals(settings.uncontractedTable, "us-table.dis,es-g1.ctb")
    }

    @Test
    fun gradeTwoProfileUsesContractedTable() {
        val settings = profile("SPANISH-US-CONTRACTED")

        assertEquals(settings.mainTranslationTable, "us-table.dis,es-g2.ctb")
        assertEquals(settings.mathTextTable, "us-table.dis,es-g2.ctb")
        // uncontracted and computer braille stay grade 1
        assertEquals(settings.uncontractedTable, "us-table.dis,es-g1.ctb")
        assertEquals(settings.computerBrailleTable, "es-g1.ctb")
        assertEquals(settings.isUseAsciiBraille, true)
    }

    @Test
    fun gradeTwoContracts() {
        val text = "estado de cuenta"
        val gradeOne = translate(profile("SPANISH-US"), text)
        val gradeTwo = translate(profile("SPANISH-US-CONTRACTED"), text)

        assertEquals(gradeOne.lowercase(), "estado de cuenta")
        assertNotEquals(gradeTwo, gradeOne, "Grade 2 should contract '$text'")
        assertEquals(gradeTwo.lowercase(), "\$d d cu5ta")
    }

    @Test
    fun commandLineAcceptsGradeTwoProfile() {
        val command = BrfCommand()

        CommandLine(command).parseArgs("input.docx", "output.brf", "--braille-profile", "SPANISH-US-CONTRACTED")

        assertEquals(command.brailleProfile, "SPANISH-US-CONTRACTED")
        BBIni.loadAutoProgramDataFile(UTDManager.UTD_FOLDER, command.brailleProfile + UTDManager.BRAILLE_SETTINGS_NAME)
    }
}
