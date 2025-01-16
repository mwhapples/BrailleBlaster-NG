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
package org.brailleblaster.utd.utils

import nu.xom.Node
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.Style
import org.brailleblaster.utd.UTDTranslationEngine
import org.slf4j.LoggerFactory

class PageElementFinder(elementName: String) {
    var elementName = ""
    fun findPageNumbers(document: Node, engine: ITranslationEngine): List<String> {
        val pageNumsFound: MutableList<String> = ArrayList()
        val styleMap = engine.styleMap
        var ancestor = document
        val lookUpDesc = "descendant::*[contains(name(), '$elementName')]"
        try {
            if (document.parent != null) {
                ancestor = document.query("ancestor::*")[0]
            }
            val pageNums = ancestor.query(lookUpDesc)
            for (i in 0 until pageNums.size()) {
                //				String iDesc = lookUpDesc + "[" + Integer.toString(i + 1) + "]";
                val style = styleMap.findValueOrDefault(pageNums[i]) as Style
                style.isPageNum = true
                val page = pageNums[i].value
                pageNumsFound.add(page)
            }
        } catch (e: NullPointerException) {
            log.debug("No <$elementName> found.")
        }
        return pageNumsFound
    }

    companion object {
        private val log = LoggerFactory.getLogger(UTDTranslationEngine::class.java)
    }

    init {
        this.elementName = elementName
    }
}