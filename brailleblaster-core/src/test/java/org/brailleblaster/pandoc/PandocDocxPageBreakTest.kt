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

import nu.xom.Document
import nu.xom.Element
import org.brailleblaster.BBIni.programDataPath
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.BookToBBXConverter
import org.brailleblaster.util.PANDOC_CMD
import org.brailleblaster.utils.xml.BB_NS
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Word page breaks must import as a new page before the block that follows
 * the break, the same style option the Page Break tool sets.
 */
class PandocDocxPageBreakTest {
    private val luaDir: String by lazy { programDataPath.resolve("pandoc").resolve("lua").toString() }

    @BeforeClass
    fun init() {
        BookToBBXConverter.devSetup(arrayOf())
    }

    /** Mirrors the PandocArchiverLoader command line and fixers for DOCX. */
    private fun runPandoc(input: File): Document {
        val bbxFile = File.createTempFile("bb-pagebreak-out-", ".bbx").apply { deleteOnExit() }
        val command = listOf(
            PANDOC_CMD,
            "--from=docx+styles+empty_paragraphs",
            "--lua-filter=docx-page-break.lua",
            "--lua-filter=list-paragraph-style.lua",
            "--to=bbx.lua",
            "--output=" + bbxFile.absolutePath,
            DocxPageBreaks.markPageBreaks(input).absolutePath
        )
        val proc = ProcessBuilder(command)
            .directory(File(luaDir))
            .redirectErrorStream(true)
            .apply { environment()["PANDOCCMD"] = PANDOC_CMD }
            .start()
        val output = proc.inputStream.bufferedReader().readText()
        Assert.assertEquals(proc.waitFor(), 0, "pandoc failed: $output")

        return Fixer(bbxFile.absolutePath).apply {
            addFixer(FixPageBreak())
            addFixer(FixMathML())
            addFixer(FixNestedList())
            addFixer(FixImage())
            processFixers()
        }.document
    }

    private fun resourceFile(name: String): File =
        File.createTempFile("bb-pagebreak-in-", ".docx").apply {
            deleteOnExit()
            val bytes = PandocDocxPageBreakTest::class.java.getResourceAsStream(name)
                ?.use { it.readAllBytes() }
            Assert.assertNotNull(bytes, "Test resource $name not found on classpath")
            writeBytes(bytes!!)
        }

    /** A minimal DOCX whose body is [body]. */
    private fun docx(body: String): File =
        File.createTempFile("bb-pagebreak-gen-", ".docx").apply {
            deleteOnExit()
            val parts = mapOf(
                "[Content_Types].xml" to """<?xml version="1.0" encoding="UTF-8"?>
                    <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                    <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                    <Default Extension="xml" ContentType="application/xml"/>
                    <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
                    </Types>""",
                "_rels/.rels" to """<?xml version="1.0" encoding="UTF-8"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
                    </Relationships>""",
                "word/document.xml" to """<?xml version="1.0" encoding="UTF-8"?>
                    <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                    <w:body>$body</w:body></w:document>"""
            )
            ZipOutputStream(outputStream()).use { zip ->
                parts.forEach { (name, xml) ->
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(xml.trimIndent().toByteArray(Charsets.UTF_8))
                    zip.closeEntry()
                }
            }
        }

    private fun para(text: String) = "<w:p><w:r><w:t>$text</w:t></w:r></w:p>"

    private val pageBreakPara = """<w:p><w:r><w:br w:type="page"/></w:r></w:p>"""

    private fun blocks(doc: Document): List<Element> =
        doc.query("//*[local-name()='BLOCK']").map { it as Element }

    private fun blockWithText(doc: Document, text: String): Element =
        blocks(doc).firstOrNull { it.value.trim() == text }
            ?: throw AssertionError("No BLOCK with text '$text' in:\n${doc.toXML()}")

    private fun pagesBefore(e: Element): Int? =
        e.getAttributeValue("overrideOption-newPagesBefore", BB_NS)?.toInt()

    private fun assertPagesBefore(doc: Document, text: String, pages: Int?) {
        val block = blockWithText(doc, text)
        Assert.assertEquals(pagesBefore(block), pages, "New pages before '$text':\n${block.toXML()}")
    }

    private fun assertNoMarkersLeft(doc: Document) {
        Assert.assertEquals(doc.query("//*[local-name()='pageBreak']").size(), 0, "pageBreak left:\n${doc.toXML()}")
        Assert.assertFalse(doc.toXML().contains("BBPAGEBREAK"), "Marker text left:\n${doc.toXML()}")
    }

    @Test
    fun statementPageBreakStartsNewPage() {
        val doc = runPandoc(resourceFile("list-style-statement.docx"))

        assertPagesBefore(doc, "Account Activity", 1)
        assertPagesBefore(doc, "Annual Percentage Yield Earned 3.123%", null)
        Assert.assertEquals(
            doc.query("//*[@*[local-name()='overrideOption-newPagesBefore']]").size(), 1,
            "The statement has a single page break:\n${doc.toXML()}"
        )
        assertNoMarkersLeft(doc)
    }

    @Test
    fun breakInsideParagraphSplitsIt() {
        val doc = runPandoc(
            docx("""<w:p><w:r><w:t>Before</w:t><w:br w:type="page"/><w:t>After</w:t></w:r></w:p>""")
        )

        assertPagesBefore(doc, "Before", null)
        assertPagesBefore(doc, "After", 1)
        assertNoMarkersLeft(doc)
    }

    @Test
    fun consecutiveBreaksAddPages() {
        val doc = runPandoc(docx(para("One") + pageBreakPara + pageBreakPara + para("Two")))

        assertPagesBefore(doc, "One", null)
        assertPagesBefore(doc, "Two", 2)
        assertNoMarkersLeft(doc)
    }

    @Test
    fun pageBreakBeforeParagraphProperty() {
        val doc = runPandoc(
            docx(
                para("One") +
                    "<w:p><w:pPr><w:pageBreakBefore/></w:pPr><w:r><w:t>Two</w:t></w:r></w:p>" +
                    """<w:p><w:pPr><w:pageBreakBefore w:val="0"/></w:pPr><w:r><w:t>Three</w:t></w:r></w:p>"""
            )
        )

        assertPagesBefore(doc, "One", null)
        assertPagesBefore(doc, "Two", 1)
        assertPagesBefore(doc, "Three", null)
        assertNoMarkersLeft(doc)
    }

    @Test
    fun breakBeforeTableMarksTableAndCopy() {
        val cell = "<w:tc><w:p><w:r><w:t>Cell</w:t></w:r></w:p></w:tc>"
        val doc = runPandoc(docx(para("One") + pageBreakPara + "<w:tbl><w:tr>$cell$cell</w:tr></w:tbl>"))

        val tables = doc.query("//*[local-name()='CONTAINER' and @*[local-name()='type']='TABLE']")
            .map { it as Element }
        Assert.assertEquals(tables.size, 2, "Expected a table and its UTD copy:\n${doc.toXML()}")
        tables.forEach { Assert.assertEquals(pagesBefore(it), 1, "Table should start a new page:\n${it.toXML()}") }
        Assert.assertTrue(blocks(doc).filter { BBX.BLOCK.TABLE_CELL.isA(it) }.none { pagesBefore(it) != null })
        assertNoMarkersLeft(doc)
    }

    @Test
    fun breakAtEndIsDropped() {
        val doc = runPandoc(docx(para("Only") + pageBreakPara))

        assertPagesBefore(doc, "Only", null)
        assertNoMarkersLeft(doc)
    }

    @Test
    fun docxWithoutBreaksIsNotCopied() {
        val input = docx(para("Plain"))
        Assert.assertSame(DocxPageBreaks.markPageBreaks(input), input)
    }
}
