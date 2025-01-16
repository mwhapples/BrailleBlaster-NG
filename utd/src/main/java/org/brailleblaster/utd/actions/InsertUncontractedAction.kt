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
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan
import org.brailleblaster.utd.properties.BrailleTableType
import org.brailleblaster.utd.properties.UTDElements
import org.brailleblaster.utd.utils.TextTranslator

class InsertUncontractedAction : IBlockAction {
    var action = GenericBlockAction()
    override fun applyTo(node: Node, context: ITranslationEngine): List<TextSpan> {
        if (node is Element) {
            if (node.getAttribute("translated") == null) {
                return processNode(node, context)
            }
        }
        return emptyList()
    }

    private fun processNode(element: Element, engine: ITranslationEngine): List<TextSpan> {
        val brl = UTDElements.BRL.create()

        //Add the uncontracted word and translate
        val span = Element("span")
        span.addAttribute(Attribute("type", "pronunciation"))
        span.addAttribute(Attribute("translated", "true"))
        val translatedText =
            " " + TextTranslator.translateText(element.value, engine, tableType = BrailleTableType.UNCONTRACTED) + " "

//		//Find DD sibling
//		ParentNode parent = element.getParent();
//		Element sibling = (Element) parent.getChild(parent.indexOf(element) + 1);
//		sibling.insertChild(span, 0);

        //!!Add it as the last child of the <dt> instead
        element.appendChild(span)
        brl.addAttribute(Attribute("type", "pronunciation"))
        brl.addAttribute(Attribute("value", translatedText))
        span.appendChild(brl)

        //Only translates the line itself
        return ArrayList(action.applyTo(element, engine))
    } //	public List<Element> searchForGW(Element element, ITranslationEngine engine) {
    //		List<Element> guideWords = new ArrayList<Element>();
    //		
    //		element.query("descendant-or-self:*");
    //		//Assume you'll search for descendant-or-self with type=guideWord (after Leon's changes)
    //		
    //		
    //		return null;
    //	}
}