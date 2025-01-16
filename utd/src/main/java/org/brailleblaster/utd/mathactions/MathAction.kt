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
package org.brailleblaster.utd.mathactions

import nu.xom.*
import nu.xom.xslt.XSLException
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan
import org.brailleblaster.utd.actions.XsltAction
import org.brailleblaster.utd.asciimath.AsciiMathConverter
import org.brailleblaster.utd.exceptions.UTDTranslateException
import org.brailleblaster.utd.properties.BrailleTableType
import org.brailleblaster.utd.properties.ContentType
import org.brailleblaster.utd.properties.UTDElements
import org.brailleblaster.utd.utils.MathMLConverter
import org.brailleblaster.utd.utils.UTDHelper
import java.util.stream.Collectors
import java.util.stream.Stream

class MathAction : XsltAction(BrailleTableType.MATH) {
    override fun translate(
        node: Node,
        tableType: BrailleTableType,
        context: ITranslationEngine
    ): List<TextSpan> {
        // The node parameter is the tme with emphasis=MATH as an attribute.  It has a text child that
        // should be turned
        // into ASCII Math
        if (node.childCount == 0) {
            throw RuntimeException("Translating empty ASCII Math")
        }
        val asciiMathTextNode = node.getChild(0)
        val mathMLNodes = AsciiMathConverter.toMathML(asciiMathTextNode.value,true)
        // Apply the XSLT
        val text = MathMLConverter.convertMathML(mathMLNodes[0])
        val results: Nodes = if (text == null) {
            try {
                transform.transform(mathMLNodes)
            } catch (e: XSLException) {
                throw RuntimeException("Problem applying the stylesheet", e)
            }
        } else {
            Nodes(text)
        }
        val textToTranslate = StringBuilder()
        for (i in 0 until results.size()) {
            val curNode = results[i]
            if (curNode is Text) {
                textToTranslate.append(curNode.getValue())
            } else if (curNode is Element) {
                val action = context.actionMap.findValueOrDefault(curNode)
                val spans = action.applyTo(curNode, context)
                // Gather up all the text for a single TextSpan for the math element
                for (span in spans) {
                    textToTranslate.append(span.text)
                }
            }
        }
        val thisSpan = TextSpan(asciiMathTextNode, textToTranslate.toString(), contentType = ContentType.Math)
        val spans: List<TextSpan> = listOf(thisSpan)
        try {
            translateString(spans, tableType, context)
        } catch (e: UTDTranslateException) {
            // Don't keep wrapping the exception otherwise it will bubble up to the root level
            throw e
        } catch (e: Exception) {
            throw UTDTranslateException("Failed at processing node " + node.toXML(), e)
        }
        thisSpan.isTranslated = true
        return spans
    }

    public override fun assignBrls(
        toTranslate: List<TextSpan>, translated: String, endPos: List<Int>, indexInts: IntArray?
    ) {
        require(toTranslate.size == 1) {
                "Math can only have one brl, ${toTranslate.size} TextSpans were given"
        }
        val toTranslateText = toTranslate[0].text
        // We only assign a single brl to the math element.
        val brl = UTDElements.BRL.create()
        brl.appendChild(translated)
        brl.addAttribute(Attribute("type", "math"))
        // For now set all index values to the start.
        val indexStr = Stream.generate { "0" }.limit(translated.length.toLong()).collect(Collectors.joining(" "))
        brl.addAttribute(Attribute("index", indexStr))
        brl.addAttribute(Attribute("text", toTranslateText))
        // Check that the math element does not already have a brl
        // if it does remove it and replace with the new one.
        val node = toTranslate[0].node
        val parent = node!!.parent
        var index = parent.indexOf(node)
        if (UTDHelper.getAssociatedBrlElement(parent, index) != null) {
            parent.removeChild(index + 1)
        }
        index++
        parent.insertChild(brl, index)
    }


    init {
        xsltResource = "/org/brailleblaster/utd/xslt/mathml.xsl"
    }
}
