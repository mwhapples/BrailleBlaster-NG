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

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/** Builds minimal DOCX files for the Pandoc import tests. */
object TestDocx {
    private const val W = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
    private const val REL = "http://schemas.openxmlformats.org/officeDocument/2006/relationships"

    /** Paragraph styles "List Paragraph" and "List 2", which have no numbering of their own. */
    val listStyles = """
        <w:style w:type="paragraph" w:styleId="ListParagraph"><w:name w:val="List Paragraph"/>
        <w:pPr><w:ind w:left="720"/></w:pPr></w:style>
        <w:style w:type="paragraph" w:styleId="List2"><w:name w:val="List 2"/>
        <w:pPr><w:ind w:left="720" w:hanging="360"/></w:pPr></w:style>"""

    /** numId 1 is a bullet list, numId 2 a decimal list. */
    val bulletAndDecimalNumbering = """
        <w:abstractNum w:abstractNumId="0"><w:lvl w:ilvl="0"><w:start w:val="1"/>
        <w:numFmt w:val="bullet"/><w:lvlText w:val="•"/></w:lvl></w:abstractNum>
        <w:abstractNum w:abstractNumId="1"><w:lvl w:ilvl="0"><w:start w:val="1"/>
        <w:numFmt w:val="decimal"/><w:lvlText w:val="%1."/></w:lvl></w:abstractNum>
        <w:num w:numId="1"><w:abstractNumId w:val="0"/></w:num>
        <w:num w:numId="2"><w:abstractNumId w:val="1"/></w:num>"""

    fun para(text: String) = "<w:p><w:r><w:t>$text</w:t></w:r></w:p>"

    /** A paragraph in [styleId], numbered with [numId] when it is given. */
    fun styledPara(styleId: String, text: String, numId: Int? = null): String {
        val numPr = numId?.let { """<w:numPr><w:ilvl w:val="0"/><w:numId w:val="$it"/></w:numPr>""" } ?: ""
        return """<w:p><w:pPr><w:pStyle w:val="$styleId"/>$numPr</w:pPr><w:r><w:t>$text</w:t></w:r></w:p>"""
    }

    /** A DOCX whose body is [body], with optional style and numbering definitions. */
    fun docx(body: String, styles: String? = null, numbering: String? = null): File =
        File.createTempFile("bb-test-gen-", ".docx").apply {
            deleteOnExit()
            val relationships = listOfNotNull(
                styles?.let { """<Relationship Id="rId1" Type="$REL/styles" Target="styles.xml"/>""" },
                numbering?.let { """<Relationship Id="rId2" Type="$REL/numbering" Target="numbering.xml"/>""" }
            ).joinToString("")
            val parts = listOfNotNull(
                "[Content_Types].xml" to """<?xml version="1.0" encoding="UTF-8"?>
                    <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                    <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                    <Default Extension="xml" ContentType="application/xml"/>
                    <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
                    <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
                    <Override PartName="/word/numbering.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml"/>
                    </Types>""",
                "_rels/.rels" to """<?xml version="1.0" encoding="UTF-8"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                    <Relationship Id="rId1" Type="$REL/officeDocument" Target="word/document.xml"/>
                    </Relationships>""",
                "word/_rels/document.xml.rels" to """<?xml version="1.0" encoding="UTF-8"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">$relationships</Relationships>""",
                "word/document.xml" to """<?xml version="1.0" encoding="UTF-8"?>
                    <w:document xmlns:w="$W"><w:body>$body</w:body></w:document>""",
                styles?.let { "word/styles.xml" to """<?xml version="1.0" encoding="UTF-8"?><w:styles xmlns:w="$W">$it</w:styles>""" },
                numbering?.let { "word/numbering.xml" to """<?xml version="1.0" encoding="UTF-8"?><w:numbering xmlns:w="$W">$it</w:numbering>""" }
            )
            ZipOutputStream(outputStream()).use { zip ->
                parts.forEach { (name, xml) ->
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(xml.trimIndent().toByteArray(Charsets.UTF_8))
                    zip.closeEntry()
                }
            }
        }
}
