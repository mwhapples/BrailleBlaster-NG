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
package org.brailleblaster.utd.actions

import nu.xom.Attribute
import nu.xom.Element
import nu.xom.Node
import nu.xom.Text
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan
import org.brailleblaster.utd.exceptions.UTDTranslateException
import org.brailleblaster.utd.properties.BrailleTableType
import org.brailleblaster.utd.properties.UTDElements
import org.brailleblaster.utils.braille.BrailleUnicodeConverter
import org.brailleblaster.utd.utils.TextTranslator
import org.brailleblaster.utd.utils.TextTranslator.translateTextWithEmpArray
import org.brailleblaster.utd.utils.getAssociatedBrlElement
import org.brailleblaster.utils.xml.UTD_NS
import org.mwhapples.jlouis.Louis
import org.slf4j.LoggerFactory
import java.util.*

/**
 * This class contains the details of the applyTo implementation for block actions.
 * String creation and translation will be done here as well.
 */
open class GenericBlockAction : GenericAction(), IBlockAction {
    override fun applyTo(node: Node, context: ITranslationEngine): List<TextSpan> {
        return translate(node, BrailleTableType.LITERARY, context)
    }

    protected open fun translate(
        node: Node,
        tableType: BrailleTableType,
        context: ITranslationEngine
    ): List<TextSpan> {
        val toTranslate = super.applyTo(node, context)
        var startIndex = 0
        try {
            if (translateCapitalsRun(node, toTranslate, tableType, context)) return emptyList()
            for (i in toTranslate.indices) {
                if (toTranslate[i].isTranslated) {
                    val subList = toTranslate.subList(startIndex, i)
                    translateString(subList, tableType, context)
                    startIndex = i + 1
                }
            }

            //Last list
            if (startIndex < toTranslate.size) {
                val subList = toTranslate.subList(startIndex, toTranslate.size)
                translateString(subList, tableType, context)
            }
        } catch (e: UTDTranslateException) {
            //Don't keep wrapping the exception otherwise it will bubble up to the root level
            throw e
        } catch (e: Exception) {
            throw UTDTranslateException("Failed at processing node " + node.toXML(), e)
        }
        return emptyList()
    }

    /**
     * Checks whether the character `c` is to be considered a word
     * character to normalize emphases after a word.
     *
     * @param c  The character to check
     * @return  true is character is an incidental punctuation, else false
     */
    private fun isPostIncidentalPunctuation(c: Char): Boolean {
        when (c) {
            '.', ',', '?', '!', ';', ':', ')', '}', ']', '>', '\u201D', '\u2019' -> return true
        }
        return false
    }

    fun translateString(toTranslate: List<TextSpan>, tableType: BrailleTableType, context: ITranslationEngine) {
        if (toTranslate.isEmpty()) {
            logger.info("Empty string is passed for translation")
            return
        }
        val prepared = prepare(toTranslate)
        val translationEmpResult = translateTextWithEmpArray(prepared.text, context, prepared.emphasis, tableType)
        assignBrls(toTranslate, translationEmpResult.translation, prepared.endPos, translationEmpResult.dotsToCharsMap)
    }

    /** The text of [toTranslate], its typeforms and where each span ends. */
    private fun prepare(toTranslate: List<TextSpan>): PreparedText {
        val translateText = StringBuilder()
        //add marker for starting position of each segment	
        val emphasisList: MutableList<Short> = ArrayList()

        //first index @ 0;
        val endPos: MutableList<Int> = ArrayList()
        //		boolean usedMathXSLT = false;
        for (input in toTranslate) {
            val inText = input.text
            translateText.append(inText)
            endPos.add(translateText.length)
            var value = Louis.TypeForms.PLAIN_TEXT
            for (type in input.emphasis) {
                value = (value.toInt() or type.jlouisTypeform.toInt()).toShort()
            }

            emphasisList.addAll(generateSequence { value }.take(input.text.length))
        }
        val emphasisArr = (listOf(0) + emphasisList.map {it.toInt() } + 0).windowed(3, step = 1).mapIndexed { index, (prev, cur, next) ->
            var emphVal = cur
            // Unicode's characters not valid for ASCII Braille should never be used with NO_TRANSLATE
            // Assume that these unicode characters are not to have the NO_TRANSLATE typeform applied
            val curCodePoint = translateText.codePointAt(index)
            if (ASCII_BRL_CHARS!!.indexOf(curCodePoint.toChar()) < 0) {
                emphVal =(emphVal - (emphVal and Louis.TypeForms.NO_TRANSLATE.toInt()))
            }
            // TN symbols go outside emphasis when they are at the start or end of emphasis.
            if (curCodePoint == 0xf000 || curCodePoint == 0xf001) {
                emphVal = emphVal and prev and next
            }
            emphVal.toShort()
        }.toShortArray()
        var wordFlag = false
        var puncFlag = false
        var puncStart = 0
        var emphasis: Short = 0
        var preWordEmp: Short = 0
        var wordCount = 0
        for ((i,c,e) in translateText.withIndex().zip(emphasisArr.asIterable()) { (i,c), v -> Triple(i, c, v) }) {
            if (puncFlag) {
                if (!isPostIncidentalPunctuation(c)) {
                    puncFlag = false
                }
            } else if (wordFlag) {
                if (isPostIncidentalPunctuation(c)) {
                    puncStart = i
                    puncFlag = true
                }
            }
            wordFlag = true
            if (preWordEmp == e) wordCount++ else wordCount = 1
            preWordEmp = e
            emphasis = e
        }
        if (puncFlag) {
            if (wordCount > 2) {
                // Direct translate should not be normalised.
                emphasis = (emphasis-(emphasis.toInt() and NON_EMPHASIS_TYPEFORMS.toInt()).toShort()).toShort()
                for (j in puncStart until translateText.length) {
                    emphasisArr[j] = (emphasisArr[j].toInt() or emphasis.toInt()).toShort()
                }
            }
        }
        return PreparedText(translateText.toString(), emphasisArr, endPos)
    }

    private class PreparedText(val text: String, val emphasis: ShortArray, val endPos: List<Int>)

    /**
     * A UEB capitals passage carried over consecutive blocks marked
     * [CAPITALS_RUN_ATTRIB] (Word's "List Paragraph" paragraphs), as Duxbury
     * does: one indicator before the first word and one terminator after the
     * last uppercase letter. liblouis only sees one translation call, so when
     * the fully capitalised words at the end of one marked block carry on at
     * the start of the next, the run is translated in one call and each block
     * keeps its own part of the braille.
     *
     * @return false when [node] is translated on its own as usual
     */
    private fun translateCapitalsRun(
        node: Node,
        toTranslate: List<TextSpan>,
        tableType: BrailleTableType,
        context: ITranslationEngine
    ): Boolean {
        if (node !is Element || !isCapitalsRun(node) || !canJoin(toTranslate) ||
            !TextTranslator.isUebLiterary(tableType, tableType.getTableName(context.brailleSettings))
        ) return false

        // The marked blocks before (step -1) or after (step 1) node that the capitals carry into, nearest first
        fun joined(step: Int): List<List<TextSpan>> =
            generateSequence(node to toTranslate) { (block, spans) ->
                runSibling(block, step)?.let { it to spansOf(it, context) }?.takeIf { (_, next) ->
                    canJoin(next) && if (step < 0) capitalsJoin(next, spans) else capitalsJoin(spans, next)
                }
            }.drop(1).map { it.second }.toList()

        val before = joined(-1).reversed()
        val run = before + listOf(toTranslate) + joined(1)
        if (run.size == 1) return false

        val prepared = run.map(::prepare)
        val text = prepared.joinToString(RUN_SEPARATOR) { it.text }
        val emphasis = prepared.flatMapIndexed { i, p ->
            (if (i == 0) emptyList() else List(RUN_SEPARATOR.length) { Louis.TypeForms.PLAIN_TEXT }) + p.emphasis.asList()
        }
        val result = translateTextWithEmpArray(text, context, emphasis.toShortArray(), tableType)
        val indexInts = result.dotsToCharsMap ?: return false

        // This block's cells; those of the separators and the other blocks go
        val start = prepared.take(before.size).sumOf { it.text.length + RUN_SEPARATOR.length }
        val own = prepared[before.size]
        val cells = result.translation.indices.filter { indexInts[it] in start until start + own.text.length }
        assignBrls(
            toTranslate,
            cells.map { result.translation[it] }.joinToString(""),
            own.endPos,
            cells.map { indexInts[it] - start }.toIntArray()
        )
        return true
    }

    private fun spansOf(block: Element, context: ITranslationEngine): List<TextSpan> = super.applyTo(block, context)

    protected open fun assignBrls(
        toTranslate: List<TextSpan>,
        translated: String,
        endPos: List<Int>,
        indexInts: IntArray?
    ) {
        if (indexInts != null) {
            var beginIndex = 0
            var startSegment = 0
            for (i in endPos.indices) {
                val endSegment = endPos[i]
                var endIndex = beginIndex
                while (endIndex < translated.length && indexInts[endIndex] < endSegment) {
                    endIndex++
                }

                //Get the fragment of the Braille translation which relates to this TextSpan
                val translatedSegment = translated.substring(beginIndex, endIndex)
                // Create index array for segment
                val indexSegments = IntArray(translatedSegment.length)
                for (j in translatedSegment.indices) {
                    indexSegments[j] = indexInts[beginIndex + j] - startSegment
                }
                applyBrlElement(translatedSegment, toTranslate[i], indexSegments)
                beginIndex = endIndex
                startSegment = endSegment
            }
        }
    }

    /*
	 * Apply brl elements on each segment in the list
	 */
    private fun applyBrlElement(input: String, originalSpan: TextSpan, indexPos: IntArray) {
        //Create an Element with name brl.
        val brlElement = UTDElements.BRL.create()
        if (input.isNotEmpty()) {
            //Add the Braille fragment as a text node child of the brl element.
            brlElement.appendChild(input)
        }
        //  Add the index string values to the index attribute of the brl element.
        brlElement.addAttribute(Attribute("index", indexPos.joinToString( " ")))

        //Check whether the node of the input TextSpan has an associated brl element, 
        //delete this existing associated brl element.
        val inputNode = originalSpan.node
        if (inputNode != null) {
            val parentNode = inputNode.parent
            var index = parentNode.indexOf(inputNode)
            if (parentNode.getAssociatedBrlElement(index) != null) {
                parentNode.removeChild(index + 1)
            }

            //Add the new brl element as a next sibling to the node in the input TextSpan.
            index++
            parentNode.insertChild(brlElement, index)
        }
    }

    override fun hashCode(): Int {
        var hash = 7
        //Simple match on class name, gives sane .hashCode() while not breaking subclasses
        hash = 90 * hash + Objects.hashCode(this.javaClass)
        return hash
    }

    override fun equals(other: Any?): Boolean {
        return other != null && javaClass == other.javaClass
        //Simple match on class name, gives sane .hashCode() while not breaking subclasses
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GenericBlockAction::class.java)
        private var ASCII_BRL_CHARS: String? = null
        const val NON_EMPHASIS_TYPEFORMS = Louis.TypeForms.NO_TRANSLATE

        /** utd attribute marking a block whose capitals passage may carry over into the next marked block */
        const val CAPITALS_RUN_ATTRIB = "capitalsRun"

        // Between the blocks of a run; liblouis translates it as a space
        private const val RUN_SEPARATOR = "\n"

        private val WORD = Regex("\\S+")

        private fun isCapitalsRun(node: Node) = node is Element && node.getAttributeValue(CAPITALS_RUN_ATTRIB, UTD_NS) == "true"

        // Translated spans (math, direct braille) split a block into several calls, which a run can't join
        private fun canJoin(spans: List<TextSpan>) = spans.isNotEmpty() && spans.none { it.isTranslated }

        // The next (step 1) or previous (step -1) marked block, skipping whitespace and utd elements
        private fun runSibling(block: Element, step: Int): Element? {
            val parent = block.parent ?: return null
            return generateSequence(parent.indexOf(block) + step) { it + step }
                .takeWhile { it in 0 until parent.childCount }
                .map { parent.getChild(it) }
                .firstOrNull { !(it is Text && it.value.isBlank()) && !(it is Element && it.namespaceURI == UTD_NS) }
                ?.let { it as? Element }
                ?.takeIf { isCapitalsRun(it) }
        }

        /** The last word of [first] and the first word of [second] that have letters are both fully capitalised. */
        private fun capitalsJoin(first: List<TextSpan>, second: List<TextSpan>): Boolean {
            fun letterWords(spans: List<TextSpan>) =
                WORD.findAll(spans.joinToString("") { it.text }).map { it.value }.filter { w -> w.any(Char::isLetter) }
            fun capitalised(word: String?) = word != null && word.none(Char::isLowerCase)
            return capitalised(letterWords(first).lastOrNull()) && capitalised(letterWords(second).firstOrNull())
        }

        init {
            val combinedStr =
                BrailleUnicodeConverter.LOWERCASE_ASCII_BRAILLE + BrailleUnicodeConverter.UPPERCASE_ASCII_BRAILLE
            val abcBuilder = StringBuilder(combinedStr.length)
            for (i in combinedStr.indices) {
                // Only add the first instance of a character to remove duplicates.
                if (combinedStr.indexOf(combinedStr.codePointAt(i).toChar()) == i) {
                    abcBuilder.append(combinedStr[i])
                }
            }
            ASCII_BRL_CHARS = abcBuilder.toString()
        }
    }
}
