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

import nu.xom.Document
import org.brailleblaster.settings.UTDManager
import org.brailleblaster.utd.internal.xml.XMLHandler
import org.testng.Assert.assertEquals
import org.testng.annotations.Test
import java.io.File
import java.io.StringReader
import java.io.StringWriter

/**
 * Guide dot and column layout checks for simple tables, run against the
 * formatted BRF of tables taken from Braille Formats 2016, section 11.
 */
class SimpleTableGuideDotsTest {
    @Test
    fun example11_3NoSingleGuideDots() {
        assertTable(
            formatBrf(loadResource("example-11-3-table.bbx")),
            """
            ,9f9itive  ,pa/    ,pa/ ,"piciple
            "33333333  "33333  "3333333333333
            awake ""${'"'}  awoke   awok5
            2 ""${'"'}""${'"'}"  was1 7  be5
            be> ""${'"'}""  bore    born
            b1t ""${'"'}""  b1t ""  b1t
            2come ""${'"'}  2came   2come
            2g9 ""${'"'}""  2gan    2gun
            b5d ""${'"'}""  b5t ""  b5t
            """
        )
    }

    @Test
    fun guideDotsNeedRoomForAtLeastTwo() {
        // The heading makes the first column 8 cells wide, so each entry leaves
        // room for 0, 1, 2 and 3 guide dots after its one blank cell
        val doc = simpleTableDoc(
            listOf("zzzzzzzz", "z"),
            listOf("zzzzzzz", "z"),
            listOf("zzzzzz", "z"),
            listOf("zzzzz", "z"),
            listOf("zzzz", "z"),
        )
        assertTable(
            formatBrf(doc),
            """
            zzzzzzzz  ;z
            "3333333  "3
            zzzzzzz   ;z
            zzzzzz    ;z
            zzzzz ""  ;z
            zzzz ""${'"'}  ;z
            """
        )
    }

    @Test
    fun sample11_4NoGuideDotsOnRunovers() {
        // "20 ac/ territory" and "1 ac/ territory" run over, so neither line gets guide dots;
        // the single-line "Variable" still leads to the next column
        assertTable(
            formatBrf(loadResource("sample-11-4.bbx")),
            """
                          ,habitat      ,pot5tial
            ,species      ,unit         ,popula;n
            "33333333333  "33333333333  "33333333333
            ,grizzly
              ,be> ""${'"'}""  ,v>iable ""${'"'}  ,undet}m9$
            ,bald ,eagle  #ejj ac_/ne/  #ej pairs
                            site
            ,williamson"
              's
              ,sapsuck}   #bj ac_/      #bc1cej
                            t}ritory      pairs
            ,pika ""${'"'}""${'"'}  #a ac_/       #ec1jjj
                            t}ritory      pairs
            """
        )
    }

    @Test
    fun guideDotsFollowEntryWhenItFitsOnOneLine() {
        assertTable(
            formatBrf(simpleTableDoc(*WRAPPING_ENTRY_ROWS, widths = listOf(4, 17, 2))),
            """
            zz    zz                 zz
            "333  "3333333333333333  "3
            zz    zzzzz zzzzzzzz ""  zz
            zz    zz ""${'"'}""${'"'}""${'"'}""${'"'}""  zz
            """
        )
    }

    @Test
    fun noGuideDotsWhenNarrowerColumnMakesEntryRunOver() {
        assertTable(
            formatBrf(simpleTableDoc(*WRAPPING_ENTRY_ROWS, widths = listOf(4, 10, 2))),
            """
            zz    zz          zz
            "333  "333333333  "3
            zz    zzzzz       zz
                    zzzzzzzz
            zz    zz ""${'"'}""${'"'}"  zz
            """
        )
    }

    companion object {
        /** The middle entry fits on one line in a 17 cell column but runs over in a 10 cell column */
        private val WRAPPING_ENTRY_ROWS = arrayOf(
            listOf("zz", "zz", "zz"),
            listOf("zz", "zzzzz zzzzzzzz", "zz"),
            listOf("zz", "zz", "zz"),
        )

        private fun loadResource(name: String): Document =
            XMLHandler().load(File(SimpleTableGuideDotsTest::class.java.getResource("tables/$name")!!.toURI()))

        private fun simpleTableDoc(vararg rows: List<String>, widths: List<Int>? = null): Document = XMLHandler().load(
            StringReader(
                """<?xml version="1.0" encoding="UTF-8"?>
                <bbdoc xmlns="http://brailleblaster.org/ns/bb" xmlns:bb="http://brailleblaster.org/ns/bb" xmlns:utd="http://brailleblaster.org/ns/utd">
                <head><bb:version>4</bb:version><utd:isNormalised>true</utd:isNormalised></head>
                <SECTION bb:type="ROOT"><SECTION bb:type="OTHER">
                <CONTAINER bb:type="TABLE" format="simple"${
                    widths?.let { """ customWidths="true" widths="${it.joinToString(",")}"""" } ?: ""
                }>${
                    rows.joinToString("") { row ->
                        """<CONTAINER bb:type="TABLE_ROW" bb:rowType="NORMAL">${
                            row.joinToString("") { """<BLOCK bb:type="TABLE_CELL">$it</BLOCK>""" }
                        }</CONTAINER>"""
                    }
                }</CONTAINER>
                </SECTION></SECTION></bbdoc>"""
            )
        )

        /** Translate and format the document, returning the BRF lines with trailing blank lines removed. */
        private fun formatBrf(doc: Document): List<String> {
            val utdManager = UTDManager()
            utdManager.loadEngineFromDoc(doc, "bbx")
            utdManager.engine.brailleSettings.isUseAsciiBraille = true
            val translated = utdManager.engine.translateAndFormatDocument(doc)
            utdManager.engine.format(translated.rootElement)
            val brf = StringWriter().also {
                utdManager.engine.toBRF(translated, it, 0, BRFWriter.EMPTY_PAGE_LISTENER)
            }.toString()
            return brf.replace(BRFWriter.PAGE_SEPARATOR.toString(), "")
                .lines()
                .map { it.trimEnd() }
                .dropLastWhile { it.isEmpty() || it.trim().startsWith("#") }
                .dropLastWhile { it.isEmpty() }
        }

        /** Assert that the BRF contains the expected table lines, in order and contiguous. */
        private fun assertTable(brfLines: List<String>, expected: String) {
            val expectedLines = expected.trimIndent().lines()
            val start = brfLines.indexOfFirst { it == expectedLines.first() }
            val actual = if (start < 0) brfLines else brfLines.subList(start, (start + expectedLines.size).coerceAtMost(brfLines.size))
            assertEquals(actual.joinToString("\n"), expectedLines.joinToString("\n"), "Full BRF:\n" + brfLines.joinToString("\n"))
        }
    }
}
