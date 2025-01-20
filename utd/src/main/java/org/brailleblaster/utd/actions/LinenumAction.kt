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
import org.brailleblaster.utd.utils.TextTranslator

/**
 * This action translates the text child and appends it
 * as an attribute to the element.
 */
class LinenumAction : IBlockAction {
    private val action = GenericBlockAction()
    override fun applyTo(node: Node, context: ITranslationEngine): List<TextSpan> {
        return if (node is Element) {
            processNode(node, context)
        } else emptyList()
    }

    private fun processNode(line: Element, engine: ITranslationEngine): List<TextSpan> {
        line.addAttribute(Attribute("type", "prose"))
        if (line.getAttribute("translated") == null) {
            if (line.childCount == 0 && line.getAttribute("linenum") != null) {
                line.addAttribute(Attribute("printLineNum", line.getAttributeValue("linenum")))
                line.addAttribute(
                    Attribute(
                        "linenum",
                        TextTranslator.translateText(line.getAttributeValue("linenum"), engine)
                    )
                )
            } else if (line.getAttribute("linenum") == null) {
                line.addAttribute(Attribute("linenum", TextTranslator.translateText(line.value, engine)))
                line.addAttribute(Attribute("printLineNum", line.value))
            }
            line.addAttribute(Attribute("translated", "true"))
        }
        checkForSpace(line)

        //Only translates the line itself
        val processedInput: List<TextSpan> = ArrayList(action.applyTo(line, engine))
        line.removeChildren()
        return processedInput
    }

    private fun checkForSpace(line: Element) {
        val parent = line.parent
        val lineIndex = parent.indexOf(line)
        var spacePresent = 0
        if (lineIndex > 0) {
            //If it's before, check the last character for a space
            val nodeBefore = parent.getChild(lineIndex - 1)
            if (nodeBefore is Text) {
                val text = nodeBefore.value
                if (text[text.length - 1] == ' ') {
                    spacePresent++
                }
            }
        }

//!!!!BRL DOESN'T HAVE THE SPACE IF IT'S THE FIRST CHARACTER OF THE PRINT!!!!!		
//		if (lineIndex < parent.getChildCount() - 1) {
//			//If it's after, check the first character for a space
//			Node nodeAfter = parent.getChild(lineIndex + 1);
//			if (nodeAfter instanceof Text) {
//				String text = nodeAfter.getValue();
//				if (text.charAt(0) == ' ') {
//					spacePresent++;
//				}
//			}
//		}
        line.addAttribute(Attribute("space", spacePresent.toString()))
    }
}