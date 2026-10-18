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
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Pandoc drops Word page breaks, so before pandoc reads a DOCX each break is
 * replaced with marker text. docx-page-break.lua turns the markers into
 * page-break blocks and [FixPageBreak] sets them on the next block.
 */
object DocxPageBreaks {
    /** Must match MARKER in docx-page-break.lua. */
    private const val MARKER = "&#xE000;BBPAGEBREAK&#xE000;"
    private const val DOCUMENT_XML = "word/document.xml"

    /** `<w:br w:type="page"/>`, a manual page break inside a run. */
    private val PAGE_BREAK = Regex("""<w:br\b[^>]*\bw:type="page"[^>]*/>""")

    /** A paragraph whose own properties turn on "Page break before". */
    private val PAGE_BREAK_BEFORE = Regex(
        """(<w:p\b[^>]*>\s*<w:pPr>(?:(?!</w:pPr>).)*?<w:pageBreakBefore\b(?![^>]*w:val="(?:0|false|off)")[^>]*/>.*?</w:pPr>)""",
        RegexOption.DOT_MATCHES_ALL
    )

    fun markPageBreaks(xml: String): String = xml
        .replace(PAGE_BREAK, "<w:t>$MARKER</w:t>")
        .replace(PAGE_BREAK_BEFORE) { "${it.groupValues[1]}<w:r><w:t>$MARKER</w:t></w:r>" }

    /**
     * Returns a temporary copy of [docx] with its page breaks marked, or [docx]
     * itself when it has none.
     */
    fun markPageBreaks(docx: File): File = ZipFile(docx).use { zip ->
        val documentEntry = zip.getEntry(DOCUMENT_XML) ?: return docx
        val xml = zip.getInputStream(documentEntry).use { it.readBytes().toString(Charsets.UTF_8) }
        val marked = markPageBreaks(xml)
        if (marked == xml) return docx

        File.createTempFile("bb-${docx.nameWithoutExtension}-", ".docx").apply {
            deleteOnExit()
            ZipOutputStream(outputStream()).use { out ->
                zip.entries().asSequence().forEach { entry ->
                    out.putNextEntry(ZipEntry(entry.name))
                    if (entry.name == DOCUMENT_XML) {
                        out.write(marked.toByteArray(Charsets.UTF_8))
                    } else {
                        zip.getInputStream(entry).use { it.copyTo(out) }
                    }
                    out.closeEntry()
                }
            }
        }
    }
}
