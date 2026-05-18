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
package org.brailleblaster.bbx.fixers

import nu.xom.Document
import nu.xom.Element
import nu.xom.Text
import org.brailleblaster.bbx.BBX
import org.brailleblaster.settings.UTDManager
import org.brailleblaster.utd.BRFWriter
import org.brailleblaster.utd.internal.xml.XMLHandler
import org.brailleblaster.utd.properties.EmphasisType
import org.brailleblaster.utils.BBData
import org.brailleblaster.utils.braille.singleThreadedMathCAT
import org.brailleblaster.utils.xml.UTD_NS
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.File
import java.io.StringWriter

/**
 * Regression test for the bug where saving a NIMAS file with alt-text
 * converted to a transcriber note (BLOCK.STYLE("7-5") + INLINE.EMPHASIS(TRANS_NOTE))
 * throws: "Braille not permitted outside the page area, cursor at (40,24), ..."
 *
 * The fix should allow a document with such a structure to be translated to BRF
 * without throwing a RuntimeException.
 */
class AltTextTranscriberNoteTest {

    @BeforeClass
    fun setUpMathCat() {
        val rulesDir = File(BBData.brailleblasterPath, "programData/MathCAT/Rules")
        singleThreadedMathCAT { setRulesDir(rulesDir.absolutePath) }
    }

    /**
     * Build a BBX document with enough body text to fill most of a page,
     * followed by a transcriber note block (the structure created by ImageGroupImportFixer
     * when processing an imggroup with alt text).
     */
    private fun buildTestDocument(altText: String, bodyParagraphCount: Int = 23): Document {
        val doc = BBX.newDocument()
        val headElem = BBX.getHead(doc)

        // Mark as normalised so fixers don't re-run
        val isNormalisedElem = Element("isNormalised", UTD_NS)
        isNormalisedElem.appendChild("true")
        headElem.appendChild(isNormalisedElem)

        val rootSection = BBX.SECTION.ROOT.create()
        doc.rootElement.appendChild(rootSection)

        val bodySection = BBX.SECTION.OTHER.create()
        rootSection.appendChild(bodySection)

        // Add enough body paragraphs to fill most of a page (style "Body Text")
        // Each paragraph at default formatting uses ~1 row; 23 paragraphs ≈ 23 rows
        for (i in 1..bodyParagraphCount) {
            val block = BBX.BLOCK.DEFAULT.create()
            block.appendChild(Text("Paragraph $i of the body text content."))
            bodySection.appendChild(block)
        }

        // Add the transcriber note block - this is what ImageGroupImportFixer creates
        // from an imggroup's alt attribute:
        //   <bb:BLOCK bb:type="STYLE" bb:style="7-5">
        //     <bb:INLINE bb:type="EMPHASIS" bb:emphasis="TRANS_NOTE">alt text here</bb:INLINE>
        //   </bb:BLOCK>
        val transNoteBlock = BBX.BLOCK.STYLE.create("7-5")
        val transNoteInline = BBX.INLINE.EMPHASIS.create(EmphasisType.TRANS_NOTE)
        transNoteInline.appendChild(Text(altText))
        transNoteBlock.appendChild(transNoteInline)
        bodySection.appendChild(transNoteBlock)

        return doc
    }

    /**
     * Test that a transcriber note with short alt text does not throw during BRF conversion.
     */
    @Test
    fun shortAltTextTranscriberNoteNoBrfException() {
        val doc = buildTestDocument("A short description.")
        translateAndConvertToBrf(doc)
    }

    /**
     * Test that a transcriber note with long alt text (spanning multiple lines) does not
     * throw during BRF conversion. This is the main regression case.
     */
    @Test
    fun longAltTextTranscriberNoteNoBrfException() {
        // 100+ chars — will span multiple braille rows at indent=4, lineLength=40
        val altText = "Two bars: top bar divided into four equal segments labeled a plus b; " +
                "bottom bar divided into segments showing the total sum result."
        val doc = buildTestDocument(altText)
        translateAndConvertToBrf(doc)
    }

    /**
     * Test with altText that starts exactly at the last row of a page.
     * Fill 24 rows of body text so the transcriber note starts at the page boundary.
     */
    @Test
    fun altTextAtPageBoundaryNoBrfException() {
        val altText = "Two bars: top bar divided into four equal segments labeled a plus b; " +
                "bottom bar divided into segments showing the total sum result."
        // Use 24 body paragraphs to push trans note to the end of page 1 / start of page 2
        val doc = buildTestDocument(altText, bodyParagraphCount = 24)
        translateAndConvertToBrf(doc)
    }

    /**
     * Regression test using the real NIMAS file that originally triggered the bug.
     * Uses the pre-converted BBX document (extracted from the BBZ that caused the
     * original save failure) to test translation + BRF conversion directly,
     * bypassing the NIMAS import pipeline. Should not throw.
     */
    @Test
    fun realNimasFileNoBrfException() {
        val bbxFile = AltTextTranscriberNoteTest::class.java
            .getResource("9781946636171NIMAS.bbx")!!
            .let { java.io.File(it.toURI()) }
        val doc = XMLHandler().load(bbxFile)
        translateAndConvertToBrf(doc)
    }

    private fun translateAndConvertToBrf(doc: Document) {
        val utdManager = UTDManager()
        utdManager.loadEngineFromDoc(doc, "bbx")
        val translated = utdManager.engine.translateAndFormatDocument(doc)
        utdManager.engine.format(translated.rootElement)

        val brfWriter = StringWriter()
        // Should NOT throw RuntimeException("Braille not permitted outside the page area")
        utdManager.engine.toBRF(translated, brfWriter, 0, BRFWriter.EMPTY_PAGE_LISTENER)
    }
}
