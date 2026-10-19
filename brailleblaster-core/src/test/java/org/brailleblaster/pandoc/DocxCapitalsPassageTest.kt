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
import org.testng.Assert.assertTrue
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.StringWriter

/**
 * A UEB capitals passage over consecutive Word "List Paragraph" paragraphs
 * takes one indicator and one terminator, as Duxbury gives for ASM's
 * statements. Other paragraphs each close their own passage.
 */
class DocxCapitalsPassageTest {
    @BeforeClass
    fun init() {
        BookToBBXConverter.devSetup(arrayOf())
    }

    @Test
    fun passageOverThreeListParagraphsEndsInBoldWord() {
        // BR-55_lws page 5
        val lines = brf(
            listParagraph("IF YOU HAVE FILED BANKRUPTCY THIS IS BEING SENT AT YOUR REQUEST. YOU HAVE NO PERSONAL ") +
                listParagraph("LIABILITY TO PAY A DISCHARGED DEBT. CALL TO DISCONTINUE STATEMENTS, IF DESIRED. ") +
                listParagraph(bold("QUESTIONS") + run(" Please see the Appendix for additional information."))
        )

        assertLines(
            lines,
            ",,,if y h fil\$ bankruptcy ? is 2+ s5t at",
            "  yr reque/4 y h no p]sonal",
            "liabil;y to pay a 4*>g\$ debt4 call to",
            "  4cont9ue /ate;ts1 if desir\$4",
            "^1\"qs,' ,pl1se see ! ,app5dix = addi;nal"
        )
    }

    @Test
    fun shortListParagraphsMakeOnePassage() {
        // BR-81_acbs page 5: two words each, four in the run
        val lines = brf(listParagraph("CVRXCUSTOMER0049814 NAME1") + listParagraph("CVRXCUSTOMER0049814 NAME2"))

        assertLines(lines, ",,,cvrxcu/om]#jjdihad \"n#a", "cvrxcu/om]#jjdihad \"n#b,'")
    }

    @Test
    fun passageStartsPartWayThroughAddress() {
        // BR-78_lws page 4
        val lines = brf(
            listParagraph("Cleveland, OH 44101-0570") + listParagraph("FRANK E HOLLENBACK ") +
                listParagraph("174 WHITE DEER AVE.") + listParagraph("ALLENWOOD, PA 17810")
        )

        assertLines(lines, ",clevel&1 ,,,oh #ddaja-#jegj", "frank ;e holl5back", "#agd :ite de] ave4", "all5wood1 pa,' #aghaj")
    }

    @Test
    fun normalParagraphsKeepTheirOwnIndicators() {
        // BR-81_acbs page 1
        val lines = brf(normal("CVRXCUSTOMER0049814 NAME1") + normal("CVRXCUSTOMER0049814 NAME2"))

        assertLines(lines, "  ,,cvrxcu/om]#jjdihad ,,\"n#a", "  ,,cvrxcu/om]#jjdihad ,,\"n#b")
    }

    @Test
    fun listStyleParagraphsKeepTheirOwnIndicators() {
        // BR-95_wc: Word's "List" style closes each passage
        val lines = brf(list("PLEASE FORWARD TO") + list("MS FIRST LAST STREET, PA 11111"))

        assertLines(lines, ",,,pl1se =w>d to,'",",,,ms f/ la/ /reet1 pa,' #aaaaa")
    }

    @Test
    fun passageEndingInItsOwnListParagraphIsUnchanged() {
        val lines = brf(listParagraph("CALL US AT ONCE please") + listParagraph("NOW OR NEVER"))

        assertLines(lines, ",,,call u at once,' pl1se", ",,,n[ or n\"e,'")
    }

    @Test
    fun passageCarriesOverPageBreak() {
        val filler = (1..22).joinToString("") { normal("Zebra") }
        val pages = brfPages(
            filler + listParagraph("YOU HAVE NO PERSONAL ") + listParagraph("LIABILITY TO PAY A DEBT.") +
                listParagraph("CALL TO STOP STATEMENTS. ") + listParagraph("Thank you")
        )

        val run = pages.flatten().filter { it.isNotBlank() && !it.trim().startsWith(",zebra") }
        val start = run.indexOfFirst { ",,,y h no p]sonal" in it }
        assertTrue(start >= 0, "No passage start:\n${run.joinToString("\n")}")
        assertTrue(pages.size > 1 && pages[0].none { "/ate;ts4" in it },"Run should cross the page break:\n${show(pages)}")
        val text = run.subList(start, run.size).joinToString("\n")
        assertEquals(Regex(",,,").findAll(text).count(), 1, "One indicator:\n$text")
        assertEquals(Regex(",'").findAll(text).count(), 1, "One terminator:\n$text")
        assertTrue("/ate;ts4,'" in text,"Terminator after the last capitals:\n$text")
    }

    private fun brf(body: String): List<String> = brfPages(body).flatten().filter { it.isNotBlank() }

    private fun brfPages(body: String): List<List<String>> {
        val docx = TestDocx.docx(body, styles = STYLES)
        val doc = ArchiverFactory.load(docx.toPath()).bbxDocument
        val utdManager = UTDManager()
        utdManager.loadEngineFromDoc(doc, "bbx")
        utdManager.engine.brailleSettings.isUseAsciiBraille = true
        val translated = utdManager.engine.translateAndFormatDocument(doc)
        val brf = StringWriter().also {
            utdManager.engine.toBRF(translated, it, 0, BRFWriter.EMPTY_PAGE_LISTENER)
        }.toString()
        return brf.split(BRFWriter.PAGE_SEPARATOR)
            .map { page -> page.lines().map { ascii(it.trimEnd()) } }
            .filter { page -> page.any { it.isNotBlank() } }
    }

    /** [expected] are consecutive lines of [lines], ignoring the page number at the end of a line. */
    private fun assertLines(lines: List<String>, vararg expected: String) {
        val stripped = lines.map { it.replace(Regex("\\s{2,}#\\w+$"), "") }
        val found = stripped.windowed(expected.size).any { it == expected.toList() }
        assertTrue(found, "Expected\n${expected.joinToString("\n")}\nin\n${lines.joinToString("\n")}")
    }

    // The BRF's lowercase forms of ASCII braille as usually written
    private fun ascii(line: String) = line.map {
        when (it) {
            '}' -> ']'
            '~' -> '^'
            '{' -> '['
            '|' -> '\\'
            '`' -> '@'
            else -> it.lowercaseChar()
        }
    }.joinToString("")

    private fun show(pages: List<List<String>>) = pages.joinToString("\n----\n") { it.joinToString("\n") }

    companion object {
        private val STYLES = """
            <w:style w:type="paragraph" w:styleId="List"><w:name w:val="List"/></w:style>
            <w:style w:type="paragraph" w:styleId="ListParagraph"><w:name w:val="List Paragraph"/></w:style>"""

        private fun run(text: String) = """<w:r><w:t xml:space="preserve">$text</w:t></w:r>"""

        private fun bold(text: String) = """<w:r><w:rPr><w:b/></w:rPr><w:t xml:space="preserve">$text</w:t></w:r>"""

        private fun paragraph(style: String?, runs: String) =
            "<w:p>" + (style?.let { """<w:pPr><w:pStyle w:val="$it"/></w:pPr>""" } ?: "") + runs + "</w:p>"

        private fun listParagraph(text: String) = paragraph("ListParagraph", if (text.startsWith("<w:r>")) text else run(text))

        private fun list(text: String) = paragraph("List", run(text))

        private fun normal(text: String) = paragraph(null, run(text))
    }
}
