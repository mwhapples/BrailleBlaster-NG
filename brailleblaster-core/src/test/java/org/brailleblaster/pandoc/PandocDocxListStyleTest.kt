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
import org.brailleblaster.settings.DefaultNimasMaps
import org.brailleblaster.util.PANDOC_CMD
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.File

/**
 * DOCX paragraphs using Word's unnumbered "List" styles must import as list
 * items (L1-3), while ordinary paragraphs stay Body Text (3-1).
 *
 * The statement fixture uses the "List" style for its addresses and summary
 * lines; before the fix they imported as Body Text and started in cell 3.
 */
class PandocDocxListStyleTest {
    private val luaDir: String by lazy { programDataPath.resolve("pandoc").resolve("lua").toString() }
    private val maps = DefaultNimasMaps()

    @BeforeClass
    fun init() {
        BookToBBXConverter.devSetup(arrayOf())
    }

    /** Mirrors the PandocArchiverLoader command line and fixers. */
    private fun runPandoc(input: File, fromFormat: String): Document {
        val bbxFile = File.createTempFile("bb-liststyle-out-", ".bbx").apply { deleteOnExit() }
        val command = listOf(
            PANDOC_CMD,
            "--from=$fromFormat",
            "--to=bbx.lua",
            "--output=" + bbxFile.absolutePath,
            input.absolutePath
        )
        val proc = ProcessBuilder(command)
            .directory(File(luaDir))
            .redirectErrorStream(true)
            .apply { environment()["PANDOCCMD"] = PANDOC_CMD }
            .start()
        val output = proc.inputStream.bufferedReader().readText()
        Assert.assertEquals(proc.waitFor(), 0, "pandoc failed: $output")

        return Fixer(bbxFile.absolutePath).apply {
            addFixer(FixMathML())
            addFixer(FixNestedList())
            addFixer(FixImage())
            processFixers()
        }.document
    }

    private fun resourceFile(name: String, suffix: String): File =
        File.createTempFile("bb-liststyle-in-", suffix).apply {
            deleteOnExit()
            val bytes = PandocDocxListStyleTest::class.java.getResourceAsStream(name)
                ?.use { it.readAllBytes() }
            Assert.assertNotNull(bytes, "Test resource $name not found on classpath")
            writeBytes(bytes!!)
        }

    private fun blocks(doc: Document): List<Element> =
        doc.query("//*[local-name()='BLOCK']").map { it as Element }

    private fun blockWithText(doc: Document, text: String): Element =
        blocks(doc).firstOrNull { it.value.trim() == text }
            ?: throw AssertionError("No BLOCK with text '$text' in:\n${doc.toXML()}")

    private fun styleName(block: Element): String? =
        maps.styleMultiMap().findValueWithDefault(block, null)?.name

    private fun assertListItem(doc: Document, text: String) {
        val block = blockWithText(doc, text)
        Assert.assertTrue(BBX.BLOCK.LIST_ITEM.isA(block), "'$text' should be a LIST_ITEM:\n${block.toXML()}")
        Assert.assertEquals(styleName(block), "L1-3", "'$text' should use list style 1-3")
    }

    private fun assertParagraph(doc: Document, text: String) {
        val block = blockWithText(doc, text)
        Assert.assertTrue(BBX.BLOCK.DEFAULT.isA(block), "'$text' should be a DEFAULT block:\n${block.toXML()}")
        Assert.assertEquals(styleName(block), "Body Text", "'$text' should use paragraph style 3-1")
    }

    @Test
    fun statementAddressesImportAsListItems() {
        val doc = runPandoc(resourceFile("list-style-statement.docx", ".docx"), "docx+styles+empty_paragraphs")

        listOf(
            "PNC Bank, National Association",
            "P.O. Box 3499",
            "Pittsburgh, PA 15230",
            "John Smith",
            "123 Main St",
            "Townsville, QC 12345",
            "Account number: XXXXXX6789",
            "Annual Percentage Yield Earned 3.123%",
            "Pittsburgh, PA 15230-9991"
        ).forEach { assertListItem(doc, it) }

        // Adjacent "List" paragraphs merge into a single list, without bullets
        val bankAddress = blockWithText(doc, "PNC Bank, National Association").parent as Element
        Assert.assertTrue(BBX.CONTAINER.LIST.isA(bankAddress), "Address should be inside a LIST container")
        Assert.assertEquals(
            bankAddress.childElements.map { it.value.trim() },
            listOf("PNC Bank, National Association", "P.O. Box 3499", "Pittsburgh, PA 15230")
        )
        Assert.assertFalse(doc.toXML().contains("•"), "List style paragraphs must not gain bullets")
    }

    @Test
    fun listItemsKeepLineBreaks() {
        val doc = runPandoc(resourceFile("list-style-statement.docx", ".docx"), "docx+styles+empty_paragraphs")

        val item = blocks(doc).firstOrNull { it.value.startsWith("English 1 Message body") }
            ?: throw AssertionError("Message body list item missing:\n${doc.toXML()}")
        Assert.assertTrue(BBX.BLOCK.LIST_ITEM.isA(item), "Message body should be a LIST_ITEM:\n${item.toXML()}")
        val newLines = item.query("*[local-name()='newLine']").size()
        Assert.assertEquals(newLines, 3, "Line breaks inside the list item were lost:\n${item.toXML()}")
    }

    @Test
    fun bulletListItemsKeepLineBreaks() {
        val doc = runPandoc(resourceFile("list-style-paragraphs.html", ".html"), "html+empty_paragraphs")

        val item = blocks(doc).firstOrNull { it.value.contains("Bullet line one") }
            ?: throw AssertionError("Bullet list item missing:\n${doc.toXML()}")
        Assert.assertTrue(BBX.BLOCK.LIST_ITEM.isA(item), "Bullet should be a LIST_ITEM:\n${item.toXML()}")
        Assert.assertEquals(item.query("*[local-name()='newLine']").size(), 1, "Line break lost:\n${item.toXML()}")
    }

    @Test
    fun nestedListsStillFlatten() {
        val html = File.createTempFile("bb-liststyle-nested-", ".html").apply {
            deleteOnExit()
            writeText("<html><body><ul><li>outer<ul><li>inner</li></ul></li></ul></body></html>")
        }
        val doc = runPandoc(html, "html+empty_paragraphs")

        val lists = doc.query("//*[local-name()='CONTAINER' and @*[local-name()='type']='LIST']")
        Assert.assertEquals(lists.size(), 1, "Nested list should be flattened into one list:\n${doc.toXML()}")
        val inner = blocks(doc).first { it.value.contains("inner") }
        Assert.assertEquals(inner.parent, lists[0], "Inner item should move up into the outer list")
        Assert.assertEquals(BBX.BLOCK.LIST_ITEM.ATTRIB_ITEM_LEVEL.get(inner), 1)
    }

    private fun listParagraphs(body: String): Document = runPandoc(
        TestDocx.docx(body, TestDocx.listStyles, TestDocx.bulletAndDecimalNumbering),
        "docx+styles+empty_paragraphs"
    )

    @Test
    fun unnumberedListParagraphHasNoBullet() {
        val doc = listParagraphs(
            TestDocx.styledPara("ListParagraph", "Portfolio 010") +
                TestDocx.styledPara("ListParagraph", "Due Date 10/31/22")
        )

        assertListItem(doc, "Portfolio 010")
        assertListItem(doc, "Due Date 10/31/22")
        Assert.assertFalse(doc.toXML().contains("•"), "Unnumbered List Paragraph gained a bullet:\n${doc.toXML()}")
    }

    @Test
    fun listParagraphWithoutRealNumberingHasNoBullet() {
        // numId 0 means "no numbering"; numId 7 is not defined in numbering.xml
        val doc = listParagraphs(
            TestDocx.styledPara("ListParagraph", "Numbering off", numId = 0) +
                TestDocx.styledPara("ListParagraph", "Numbering undefined", numId = 7)
        )

        assertListItem(doc, "Numbering off")
        assertListItem(doc, "Numbering undefined")
        Assert.assertFalse(doc.toXML().contains("•"), "List Paragraph without numbering gained a bullet:\n${doc.toXML()}")
    }

    @Test
    fun numberedListParagraphKeepsBulletOrNumber() {
        val doc = listParagraphs(
            TestDocx.styledPara("ListParagraph", "Bulleted", numId = 1) +
                TestDocx.para("Between") +
                TestDocx.styledPara("ListParagraph", "Numbered", numId = 2)
        )

        Assert.assertTrue(blocks(doc).any { it.value.contains("•") }, "Bullet lost:\n${doc.toXML()}")
        Assert.assertTrue(blocks(doc).any { it.value.contains("1.") }, "Number lost:\n${doc.toXML()}")
    }

    @Test
    fun ordinaryParagraphsStayBodyText() {
        val html = resourceFile("list-style-paragraphs.html", ".html")
        val doc = runPandoc(html, "html+empty_paragraphs")

        assertParagraph(doc, "A plain paragraph.")
        assertParagraph(doc, "A Body Text styled paragraph.")
        assertParagraph(doc, "A Listing styled paragraph.")
        assertListItem(doc, "A List styled paragraph.")
        assertListItem(doc, "A List 2 styled paragraph.")
    }
}
