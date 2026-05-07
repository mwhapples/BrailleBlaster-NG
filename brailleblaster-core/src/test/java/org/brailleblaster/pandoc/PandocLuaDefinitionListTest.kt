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
package org.brailleblaster.pandoc

import nu.xom.Document
import nu.xom.Element
import org.brailleblaster.BBIni.programDataPath
import org.brailleblaster.bbx.BookToBBXConverter
import org.brailleblaster.util.PANDOC_CMD
import org.brailleblaster.utils.xml.BB_NS
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets

/**
 * Tests that the bbx.lua Pandoc custom writer correctly converts HTML
 * definition lists (&lt;dl&gt;/&lt;dt&gt;/&lt;dd&gt;) into the BBX DEFINITION list format,
 * including the required bb:listLevel and bb:itemLevel attributes that
 * FixNestedList requires.
 * 
 * Key fix: Pandoc 3.x passes DefinitionList items as \{\[term_string\] = defs_table\}
 * (term is the map key), not as {term_string, defs_table} (numeric indices).
 */
class PandocLuaDefinitionListTest {
    private val luaDir: String by lazy { programDataPath.resolve("pandoc").resolve("lua").toString() }

    @BeforeClass
    fun init() {
        BookToBBXConverter.devSetup(arrayOf())
    }

    /**
     * Runs pandoc with bbx.lua on the given HTML string and returns the parsed
     * BBX Document after the fixers (FixMathML, FixNestedList, FixImage) have run.
     */
    @Throws(Exception::class)
    private fun runPandoc(html: String): Document {
        val htmlFile = File.createTempFile("bb-deflist-test-", ".html")
        htmlFile.deleteOnExit()
        FileWriter(htmlFile).use { fw ->
            fw.write(html)
        }
        val bbxFile = File.createTempFile("bb-deflist-out-", ".bbx")
        bbxFile.deleteOnExit()

        val pb = ProcessBuilder(
            PANDOC_CMD,
            "--from=html+empty_paragraphs",
            "--to=bbx.lua",
            "--output=" + bbxFile.absolutePath,
            htmlFile.absolutePath
        ).directory(File(luaDir))
        pb.environment()["PANDOCCMD"] = PANDOC_CMD
        pb.redirectErrorStream(true)

        val proc = pb.start()
        val status = proc.waitFor()
        Assert.assertEquals(status, 0, "pandoc exited with non-zero status; check bbx.lua for errors")

        val fixer = Fixer(bbxFile.absolutePath)
        fixer.addFixer(FixMathML())
        fixer.addFixer(FixNestedList())
        fixer.addFixer(FixImage())
        fixer.processFixers()

        return fixer.document
    }

    /** Returns the first CONTAINER with bb:listType="DEFINITION", or fails the test.  */
    private fun findFirstDefinitionList(doc: Document): Element {
        val nodes = doc.query(
            "//*[local-name()='CONTAINER' and @*[local-name()='listType']='DEFINITION']"
        )
        Assert.assertNotEquals(nodes.size(), 0, "No DEFINITION list container found in BBX output:\n" + doc.toXML())
        return nodes.get(0) as Element
    }

    /** Asserts that a LIST_ITEM block has the expected term text and definition text.  */
    private fun assertDefinitionItem(item: Element, expectedTerm: String, expectedDef: String) {
        Assert.assertEquals(item.localName, "BLOCK")
        Assert.assertEquals(item.getAttributeValue("type", BB_NS), "LIST_ITEM")

        val termSpan = findDefinitionTermSpan(item)
        Assert.assertTrue(
            termSpan.getValue().contains(expectedTerm),
            "Expected term '" + expectedTerm + "' in DEFINITION_TERM span, got: " + termSpan.getValue()
        )

        Assert.assertTrue(
            item.getValue().contains(expectedDef),
            "Expected definition '" + expectedDef + "' in LIST_ITEM, got: " + item.getValue()
        )
    }

    /** Finds the DEFINITION_TERM span inside a LIST_ITEM block.  */
    private fun findDefinitionTermSpan(listItem: Element): Element {
        val spans = listItem.query(
            "*[local-name()='SPAN' and @*[local-name()='type']='DEFINITION_TERM']"
        )
        Assert.assertTrue(
            spans.size() > 0,
            "No DEFINITION_TERM span found in LIST_ITEM:\n" + listItem.toXML()
        )
        return spans.get(0) as Element
    }

    // -------------------------------------------------------------------------
    // Basic definition list: one <dt> per <dd>
    // -------------------------------------------------------------------------
    @Test
    @Throws(Exception::class)
    fun basicDefinitionList() {
        val html = ("<html><body>"
                + "<dl>"
                + "<dt>Beast of Bodmin</dt><dd>A large feline inhabiting Bodmin Moor.</dd>"
                + "<dt>Morgawr</dt><dd>A sea serpent.</dd>"
                + "<dt>Owlman</dt><dd>A giant owl-like creature.</dd>"
                + "</dl>"
                + "</body></html>")

        val doc = runPandoc(html)
        val list = findFirstDefinitionList(doc)

        // Container must carry bb:listLevel (required by FixNestedList)
        Assert.assertNotNull(
            list.getAttribute("listLevel", BB_NS),
            "bb:listLevel attribute must be present on DEFINITION container"
        )
        Assert.assertEquals(list.getAttributeValue("listType", BB_NS), "DEFINITION")

        // Expect 3 LIST_ITEM blocks
        Assert.assertEquals(
            list.getChildCount(), 3,
            "Expected 3 LIST_ITEM children for 3 dt/dd pairs"
        )

        assertDefinitionItem(
            (list.getChild(0) as Element?)!!,
            "Beast of Bodmin",
            "A large feline inhabiting Bodmin Moor."
        )
        assertDefinitionItem((list.getChild(1) as Element?)!!, "Morgawr", "A sea serpent.")
        assertDefinitionItem((list.getChild(2) as Element?)!!, "Owlman", "A giant owl-like creature.")
    }

    // -------------------------------------------------------------------------
    // Definition list with inline formatting in the term
    // -------------------------------------------------------------------------
    @Test
    @Throws(Exception::class)
    fun definitionListWithBoldTerm() {
        val html = ("<html><body>"
                + "<dl>"
                + "<dt><strong>Term One</strong></dt><dd>Definition of term one.</dd>"
                + "</dl>"
                + "</body></html>")

        val doc = runPandoc(html)
        val list = findFirstDefinitionList(doc)

        Assert.assertEquals(list.getChildCount(), 1)
        val item = list.getChild(0) as Element
        Assert.assertEquals(item.localName, "BLOCK")
        Assert.assertEquals(item.getAttributeValue("type", BB_NS), "LIST_ITEM")

        val termSpan = findDefinitionTermSpan(item)
        Assert.assertTrue(
            termSpan.getValue().contains("Term One"),
            "Term text should be present even when wrapped in <strong>"
        )
    }

    // -------------------------------------------------------------------------
    // Multiple <dd> per <dt>
    // -------------------------------------------------------------------------
    @Test
    @Throws(Exception::class)
    fun definitionListMultipleDefinitions() {
        val html = ("<html><body>"
                + "<dl>"
                + "<dt>Sphinx</dt>"
                + "<dd>A mythical creature with a human head.</dd>"
                + "<dd>Also a riddle-poser in Greek mythology.</dd>"
                + "</dl>"
                + "</body></html>")

        val doc = runPandoc(html)
        val list = findFirstDefinitionList(doc)

        // 2 LIST_ITEM blocks: first has the term span, second is a continuation
        Assert.assertEquals(
            list.getChildCount(), 2,
            "Expected 2 LIST_ITEM blocks for 1 dt + 2 dd"
        )

        val first = list.getChild(0) as Element
        assertDefinitionItem(first, "Sphinx", "A mythical creature with a human head.")

        val second = list.getChild(1) as Element
        Assert.assertEquals(second.getAttributeValue("type", BB_NS), "LIST_ITEM")
        Assert.assertTrue(
            second.getValue().contains("Also a riddle-poser"),
            "Second dd should appear as a continuation LIST_ITEM"
        )
    }

    // -------------------------------------------------------------------------
    // Regression: FixNestedList must not throw NPE on a DEFINITION list
    // -------------------------------------------------------------------------
    @Test
    @Throws(Exception::class)
    fun fixNestedListDoesNotCrashOnDefinitionList() {
        val html = ("<html><body>"
                + "<dl><dt>Alpha</dt><dd>First letter.</dd></dl>"
                + "</body></html>")

        // If FixNestedList crashes, runPandoc throws — this test catches that
        val doc = runPandoc(html)
        Assert.assertNotNull(doc, "Document should not be null after fixers run")
        Assert.assertFalse(doc.toXML().isEmpty(), "Document XML should not be empty")
    }

    // -------------------------------------------------------------------------
    // End-to-end: Cryptids of Cornwall sample file
    // -------------------------------------------------------------------------
    @Test
    @Throws(Exception::class)
    fun cryptidsOfCornwallTestFile() {
        // Load the HTML from test resources (not from the personal ken-work scratch folder)
        PandocLuaDefinitionListTest::class.java
            .getResourceAsStream("definition-list-cryptids.html")?.use { inStream ->
                Assert.assertNotNull(inStream, "Test resource definition-list-cryptids.html not found on classpath")
                val html = String(inStream.readAllBytes(), StandardCharsets.UTF_8)

                val doc = runPandoc(html)

                // The intro paragraph must be present
                val paras = doc.query("//*[local-name()='BLOCK' and contains(.,'Cryptids of Cornwall')]")
                Assert.assertTrue(paras.size() > 0, "Intro paragraph 'Cryptids of Cornwall' should be present")

                // The definition list itself must be present with all three entries
                val list = findFirstDefinitionList(doc)
                Assert.assertEquals(
                    list.getChildCount(), 3,
                    "Expected 3 LIST_ITEM blocks for Beast of Bodmin, Morgawr, Owlman"
                )

                assertDefinitionItem((list.getChild(0) as Element?)!!, "Beast of Bodmin", "A large feline")
                assertDefinitionItem((list.getChild(1) as Element?)!!, "Morgawr", "sea serpent")
                assertDefinitionItem((list.getChild(2) as Element?)!!, "Owlman", "owl-like creature")
            }
    }
}
