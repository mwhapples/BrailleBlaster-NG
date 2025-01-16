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
package org.brailleblaster.utd.internal

import jakarta.xml.bind.JAXBElement
import jakarta.xml.bind.annotation.XmlElementDecl
import jakarta.xml.bind.annotation.XmlRegistry
import org.brailleblaster.utd.IStyle
import org.brailleblaster.utd.properties.Align
import org.brailleblaster.utd.properties.NumberLinePosition
import org.brailleblaster.utd.properties.PageNumberType
import org.brailleblaster.utd.properties.PageSide
import javax.xml.namespace.QName

@XmlRegistry
class StyleOptionsFactory {
    @XmlElementDecl(name = BRAILLE_PAGE_NUMBER_FORMAT)
    fun createBraillePageNumberFormat(value: PageNumberType): JAXBElement<PageNumberType> {
        return JAXBElement(QName(BRAILLE_PAGE_NUMBER_FORMAT), PageNumberType::class.java, value)
    }

    @XmlElementDecl(name = ALIGN)
    fun createAlign(value: Align): JAXBElement<Align> {
        return JAXBElement(QName(ALIGN), Align::class.java, value)
    }

    @XmlElementDecl(name = ORPHAN_CONTROL)
    fun createOrphanControl(orphanControl: Int): JAXBElement<Int> {
        return JAXBElement(QName(ORPHAN_CONTROL), Int::class.java, orphanControl)
    }

    @XmlElementDecl(name = FORMAT)
    fun createFormat(format: IStyle.Format): JAXBElement<IStyle.Format> {
        return JAXBElement(QName(FORMAT), IStyle.Format::class.java, format)
    }

    @XmlElementDecl(name = FIRST_LINE_INDENT)
    fun createFirstLineIndent(firstLineIndent: Int): JAXBElement<Int> {
        return JAXBElement(QName(FIRST_LINE_INDENT), Int::class.java, firstLineIndent)
    }

    @XmlElementDecl(name = INDENT)
    fun createIndent(indent: Int): JAXBElement<Int> {
        return JAXBElement(QName(INDENT), Int::class.java, indent)
    }

    @XmlElementDecl(name = LINE_LENGTH)
    fun createLineLength(lineLength: Int): JAXBElement<Int> {
        return JAXBElement(QName(LINE_LENGTH), Int::class.java, lineLength)
    }

    @XmlElementDecl(name = DONT_SPLIT)
    fun createDontSplit(dontSplit: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(DONT_SPLIT), Boolean::class.java, dontSplit)
    }

    @XmlElementDecl(name = KEEP_WITH_NEXT)
    fun createKeepWithNext(keepWithNext: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(KEEP_WITH_NEXT), Boolean::class.java, keepWithNext)
    }

    @XmlElementDecl(name = KEEP_WITH_PREVIOUS)
    fun createKeepWithPrevious(keepWithPrevious: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(KEEP_WITH_PREVIOUS), Boolean::class.java, keepWithPrevious)
    }

    @XmlElementDecl(name = LINES_BEFORE)
    fun createLinesBefore(linesBefore: Int): JAXBElement<Int> {
        return JAXBElement(QName(LINES_BEFORE), Int::class.java, linesBefore)
    }

    @XmlElementDecl(name = LINES_AFTER)
    fun createLinesAfter(linesAfter: Int): JAXBElement<Int> {
        return JAXBElement(QName(LINES_AFTER), Int::class.java, linesAfter)
    }

    @XmlElementDecl(name = NEW_PAGES_BEFORE)
    fun createNewPagesBefore(newPagesBefore: Int): JAXBElement<Int> {
        return JAXBElement(QName(NEW_PAGES_BEFORE), Int::class.java, newPagesBefore)
    }

    @XmlElementDecl(name = NEW_PAGES_AFTER)
    fun createNewPagesAfter(newPagesAfter: Int): JAXBElement<Int> {
        return JAXBElement(QName(NEW_PAGES_AFTER), Int::class.java, newPagesAfter)
    }

    @XmlElementDecl(name = IS_TABLE)
    fun createIsTable(isTable: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(IS_TABLE), Boolean::class.java, isTable)
    }

    @XmlElementDecl(name = IS_TABLE_ROW)
    fun createIsTableRow(isTableRow: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(IS_TABLE_ROW), Boolean::class.java, isTableRow)
    }

    @XmlElementDecl(name = IS_TABLE_CELL)
    fun createIsTableCell(isTableCell: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(IS_TABLE_CELL), Boolean::class.java, isTableCell)
    }

    @XmlElementDecl(name = LINE_SPACING)
    fun createLineSpacing(lineSpacing: Int): JAXBElement<Int> {
        return JAXBElement(QName(LINE_SPACING), Int::class.java, lineSpacing)
    }

    @XmlElementDecl(name = START_SEPARATOR)
    fun createStartSeparator(startSeparator: String): JAXBElement<String> {
        return JAXBElement(QName(START_SEPARATOR), String::class.java, startSeparator)
    }

    @XmlElementDecl(name = COLOR)
    fun createColor(color: String): JAXBElement<String> {
        return JAXBElement(QName(COLOR), String::class.java, color)
    }

    @XmlElementDecl(name = END_SEPARATOR)
    fun createEndSeparator(endSeparator: String): JAXBElement<String> {
        return JAXBElement(QName(END_SEPARATOR), String::class.java, endSeparator)
    }

    @XmlElementDecl(name = GUIDE_WORDS)
    fun createGuideWords(guideWords: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(GUIDE_WORDS), Boolean::class.java, guideWords)
    }

    @XmlElementDecl(name = PAGE_SIDE)
    fun createPageSide(pageSide: PageSide): JAXBElement<PageSide> {
        return JAXBElement(QName(PAGE_SIDE), PageSide::class.java, pageSide)
    }

    @XmlElementDecl(name = PAGE_NUM)
    fun createPageNum(pageNum: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(PAGE_NUM), Boolean::class.java, pageNum)
    }

    @XmlElementDecl(name = SKIP_NUMBER_LINES)
    fun createSkipPageNumberLines(skipNumberLines: NumberLinePosition): JAXBElement<NumberLinePosition> {
        return JAXBElement(QName(SKIP_NUMBER_LINES), NumberLinePosition::class.java, skipNumberLines)
    }

    @XmlElementDecl(name = SKIP_PAGES)
    fun createSkipPages(skipPages: Int): JAXBElement<Int> {
        return JAXBElement(QName(SKIP_PAGES), Int::class.java, skipPages)
    }

    @XmlElementDecl(name = VOLUME_END)
    fun createVolumeEned(volumeEnd: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(VOLUME_END), Boolean::class.java, volumeEnd)
    }

    @XmlElementDecl(name = LINE_NUMBER)
    fun createLineNumber(lineNumber: Boolean): JAXBElement<Boolean> {
        return JAXBElement(QName(LINE_NUMBER), Boolean::class.java, lineNumber)
    }

    @XmlElementDecl(name = LEFT_PADDING)
    fun createLeftPadding(leftPadding: Int): JAXBElement<Int> {
        return JAXBElement(QName(LEFT_PADDING), Int::class.java, leftPadding)
    }

    @XmlElementDecl(name = RIGHT_PADDING)
    fun createRightPadding(rightPadding: Int): JAXBElement<Int> {
        return JAXBElement(QName(RIGHT_PADDING), Int::class.java, rightPadding)
    }

    companion object {
        const val BRAILLE_PAGE_NUMBER_FORMAT: String = "braillePageNumberFormat"
        const val ALIGN: String = "align"
        const val ORPHAN_CONTROL: String = "orphanControl"
        const val FORMAT: String = "format"
        const val FIRST_LINE_INDENT: String = "firstLineIndent"
        const val INDENT: String = "indent"
        const val LINE_LENGTH: String = "lineLength"
        const val DONT_SPLIT: String = "dontSplit"
        const val KEEP_WITH_NEXT: String = "keepWithNext"
        const val KEEP_WITH_PREVIOUS: String = "keepWithPrevious"
        const val LINES_BEFORE: String = "linesBefore"
        const val LINES_AFTER: String = "linesAfter"
        const val NEW_PAGES_BEFORE: String = "newPagesBefore"
        const val NEW_PAGES_AFTER: String = "newPagesAfter"
        const val IS_TABLE: String = "isTable"
        const val IS_TABLE_ROW: String = "isTableRow"
        const val IS_TABLE_CELL: String = "isTableCell"
        const val LINE_SPACING: String = "lineSpacing"
        const val START_SEPARATOR: String = "startSeparator"
        const val COLOR: String = "color"
        const val END_SEPARATOR: String = "endSeparator"
        const val GUIDE_WORDS: String = "guideWords"
        const val PAGE_SIDE: String = "pageSide"
        const val PAGE_NUM: String = "pageNum"
        const val SKIP_NUMBER_LINES: String = "skipNumberLines"
        const val SKIP_PAGES: String = "skipPages"
        const val VOLUME_END: String = "volumeEnd"
        const val LINE_NUMBER: String = "lineNumber"
        const val LEFT_PADDING: String = "leftPadding"
        const val RIGHT_PADDING: String = "rightPadding"
    }
}
