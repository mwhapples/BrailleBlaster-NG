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
package org.brailleblaster.pandoc

import org.brailleblaster.archiver2.ArchiverFactory
import org.brailleblaster.bbx.BookToBBXConverter
import org.brailleblaster.settings.UTDManager
import org.brailleblaster.utd.BRFWriter
import org.testng.Assert.assertEquals
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.StringWriter

/**
 * A Word paragraph style named for a braille indent, like ASM's "1-3" in the
 * STM statements, imports as the BrailleBlaster style of that name.
 */
class DocxIndentStyleTest {
    @BeforeClass
    fun init() {
        BookToBBXConverter.devSetup(arrayOf())
    }

    @Test
    fun oneThreeRunsOverToCellThree() {
        val lines = brfLines(TestDocx.styledPara("1-3", LONG_TEXT))

        val first = lines.indexOfFirst { it.trimStart().startsWith("zebra") }
        assertEquals(indent(lines[first]), 0, "First line at cell 1:\n${lines.joinToString("\n")}")
        assertEquals(indent(lines[first + 1]), 2, "Runover at cell 3:\n${lines.joinToString("\n")}")
    }

    @Test
    fun otherStyleStaysParagraph() {
        val lines = brfLines(TestDocx.styledPara("Zq", LONG_TEXT))

        val first = lines.indexOfFirst { it.trimStart().startsWith("zebra") }
        assertEquals(indent(lines[first]), 2, "First line at cell 3:\n${lines.joinToString("\n")}")
        assertEquals(indent(lines[first + 1]), 0, "Runover at cell 1:\n${lines.joinToString("\n")}")
    }

    private fun indent(line: String) = line.length - line.trimStart().length

    private fun brfLines(body: String): List<String> {
        val docx = TestDocx.docx(body, styles = STYLES)
        val doc = ArchiverFactory.load(docx.toPath()).bbxDocument
        val utdManager = UTDManager()
        utdManager.loadEngineFromDoc(doc, "bbx")
        utdManager.engine.brailleSettings.isUseAsciiBraille = true
        val translated = utdManager.engine.translateAndFormatDocument(doc)
        val brf = StringWriter().also {
            utdManager.engine.toBRF(translated, it, 0, BRFWriter.EMPTY_PAGE_LISTENER)
        }.toString()
        return brf.lines().map { it.trimEnd().lowercase() }.filter { it.isNotBlank() }
    }

    companion object {
        // Long enough to run onto a second line
        private const val LONG_TEXT = "zebra quiz yak zebra quiz yak zebra quiz yak zebra quiz yak"

        private val STYLES = """
            <w:style w:type="paragraph" w:customStyle="1" w:styleId="1-3"><w:name w:val="1-3"/>
              <w:pPr><w:ind w:left="360" w:hanging="360"/></w:pPr></w:style>
            <w:style w:type="paragraph" w:customStyle="1" w:styleId="Zq"><w:name w:val="Zq"/></w:style>"""
    }
}
