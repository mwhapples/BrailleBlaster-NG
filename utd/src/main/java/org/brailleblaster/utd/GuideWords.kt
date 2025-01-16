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
package org.brailleblaster.utd

import nu.xom.Document
import nu.xom.Node
import org.brailleblaster.utd.matchers.XPathMatcher

class GuideWords {
    fun applyGuide(document: Document, engine: UTDTranslationEngine) {
        //Find DL in the rearmatter
        val lists = document.query("descendant::*[name()='dl'][ancestor::rearmatter]")
        for (i in 0 until lists.size()) {
            val style = engine.getStyle(lists[i])
            if (style != null && style.isGuideWords) {
                findGuideWords(lists[i], engine)
            }
        }
    }

    /**
     * Find possible guide words for each dl's in the rearmatter.
     * The first and last dt in a dl will always be guide words.
     * The dt's before and after a newpage MAY be a guide word.
     *
     * @param node
     * @param engine
     */
    fun findGuideWords(node: Node, engine: ITranslationEngine) {
        /*
         *  Every new dl should start in a new page, as a rule.
         *  Logically, the first and last <dt> should always be a guide word.
         *  The other guide words could potentially be those <dt>'s that
         *  are present before or after a <newpage> element.
         */

        //First and Last

        styleMapper("child::*[name()='dt'][1]", engine, node)
        styleMapper("child::*[name()='dt'][last()]", engine, node)

        //		Nodes firstLast = node.query("child::*[name()='dt'][position() = 1 or position() = last()]");
//		for (int i = 0; i < firstLast.size(); i++){
//			Element incNode = (Element) firstLast.get(i);
//			Style style = (Style) styleMap.findValue(incNode);
//			style.setGuideWords(true);
//			styleMap.put(new NodeNameMatcher(incNode.getLocalName(), incNode.getNamespaceURI()), style);
//		}
//		
        //Preceding
        var xpathMain = ("child::*[name()='dt'][preceding::*[name()='newpage']]"
                + "[preceding-sibling::*[1][name()!='dd']]")
        var foundNodes = node.query(xpathMain)
        var xpath: String
        for (i in 0 until foundNodes.size()) {
            xpath = xpathMain + "[" + (i + 1) + "]"
            styleMapper(xpath, engine, node)
        }
        //		
//		
////		String xpathPre = "child::*[name()='dt'][preceding::*[name()='newpage']]"
////				+ "[preceding-sibling::*[1][name()!='dd']]";
////		Nodes preceding = node.query(xpathPre);
////		for (int i = 0; i < preceding.size(); i++){
////			Element incNode = (Element) preceding.get(i);
////			Style style = (Style) styleMap.findValue(incNode);
////			style.setGuideWords(true);
////			styleMap.put(new NodeNameMatcher(incNode.getLocalName(), incNode.getNamespaceURI()), style);
////		}
//		
//		//Following
        xpathMain = ("child::*[name()='dt'][preceding::*[name()='newpage']]"
                + "[following-sibling::*[2][name()!='dt']]")
        foundNodes = node.query(xpathMain)
        for (i in 0 until foundNodes.size()) {
            xpath = xpathMain + "[" + (i + 1) + "]"
            styleMapper(xpath, engine, node)
        }
        //		
////		String xpathFoll = "child::*[name()='dt'][following::*[name()='newpage']]"
////				+ "[following-sibling::*[2][name()!='dt']]";
////		Nodes following = node.query(xpathFoll);
////		for (int i = 0; i < following.size(); i++){
////			Element incNode = (Element) following.get(i);
////			Style style = (Style) styleMap.findValue(incNode);
////			style.setGuideWords(true);
////			styleMap.put(new NodeNameMatcher(incNode.getLocalName(), incNode.getNamespaceURI()), style);
////		}
//		
//		/*
//		 * 	Descendants of <dt> or <dd>
//		 * 	If the <newpage> is a descendant of a <dt>, set the style of that element
//		 * 	to isGuideWords==true. Find the following <dt> and set the style accordingly.
//		 * 
//		 * 	If the <newpage> is a descendant of a <dd>, set the style of the <dt>s
//		 * 	preceding and following that <dd> to reflect isGuideWords==true.
//		 */
        xpathMain = "child::*[name()='dt'][descendant::*[name()='newpage']]"
        foundNodes = node.query(xpathMain)
        for (i in 0 until foundNodes.size()) {
            xpath = xpathMain + "[" + (i + 1) + "]"
            styleMapper(xpath, engine, node)

            val xpathSib = "following-sibling::*[name()='dt'][1]"
            styleMapper(xpathSib, engine, foundNodes[i])
        }
        //		
//		
////		String xpathDesc = "child::*[name()='dt'][descendant::*[name()='newpage']]";
////		Nodes descendant = node.query(xpathDesc);
////		for (int i = 0; i < descendant.size(); i++) {
////			Element incNode = (Element) descendant.get(i);
////			Style style = (Style) styleMap.findValue(incNode);
////			style.setGuideWords(true);
////			styleMap.put(new NodeNameMatcher(incNode.getLocalName(), incNode.getNamespaceURI()), style);
////			
////			String xpathSib = "following-sibling::*[name()='dt'][1]";
////			Nodes sibling = descendant.get(0).query(xpathSib);
////			for (int j = 0; j < sibling.size(); j++) {
////				incNode = (Element) sibling.get(j);
////				style = (Style) styleMap.findValue(incNode);
////				style.setGuideWords(true);
////				styleMap.put(new NodeNameMatcher(incNode.getLocalName(), incNode.getNamespaceURI()), style);
////			}
////		}
//		
        xpathMain = "child::*[name()='dd'][descendant::*[name()='newpage']]"
        foundNodes = node.query(xpathMain)
        for (i in 0 until foundNodes.size()) {
            xpath = xpathMain + "[" + (i + 1) + "]"
            styleMapper(xpath, engine, node)

            var xpathSib = "following-sibling::*[name()='dt'][1]"
            styleMapper(xpathSib, engine, foundNodes[i])

            xpathSib = "preceding-sibling::*[name()='dt'][1]"
            styleMapper(xpathSib, engine, foundNodes[i])
        }

        //		String xpathDesc2 = "child::*[name()='dd'][descendant::*[name()='newpage']]";
//		Nodes DDdescendant = node.query(xpathDesc2);
//		for (int i = 0; i < DDdescendant.size(); i++) {
//			Element incNode = (Element) DDdescendant.get(i);
//			Style style = (Style) styleMap.findValue(incNode);
//			style.setGuideWords(true);
//			styleMap.put(new NodeNameMatcher(incNode.getLocalName(), incNode.getNamespaceURI()), style);
//			
//			String xpathSib = "following-sibling::*[name()='dt'][1]";
//			Nodes siblingF = DDdescendant.get(i).query(xpathSib);
//			for (int j = 0; j < siblingF.size(); j++) {
//				incNode = (Element) siblingF.get(j);
//				style = (Style) styleMap.findValue(incNode);
//				style.setGuideWords(true);
//				styleMap.put(new NodeNameMatcher(incNode.getLocalName(), incNode.getNamespaceURI()), style);
//			}
//			
//			String xpathSib2 = "preceding-sibling::*[name()='dt'][1]";
//			Nodes siblingD = DDdescendant.get(i).query(xpathSib2);
//			for (int j = 0; j < siblingD.size(); j++) {
//				incNode = (Element) siblingD.get(j);
//				style = (Style) styleMap.findValue(incNode);
//				style.setGuideWords(true);
//				styleMap.put(new NodeNameMatcher(incNode.getLocalName(), incNode.getNamespaceURI()), style);
//			}
//		}
    }

    fun styleMapper(xpath: String?, engine: ITranslationEngine, parentNode: Node) {
        val styleMap = engine.styleMap
        //		IStyle defaultValue = new Style();
        val incNode = parentNode.query(xpath)[0]

        //		Style style = (Style) styleMap.getOrDefault(incNode, defaultValue);
        val style = styleMap.findValueOrDefault(incNode) as Style
        style.setGuideWords(true)
        styleMap[XPathMatcher(xpath)] = style
    }
}
