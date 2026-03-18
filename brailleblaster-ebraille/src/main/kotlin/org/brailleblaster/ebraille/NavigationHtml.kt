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
package org.brailleblaster.ebraille

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object NavigationHtml {
    private val FALLBACK_TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head></head>
            <body></body>
            </html>
        """.trimIndent()
    fun createNavigationHtml(docs: List<HtmlItem>): Document {
        val template = javaClass.getResourceAsStream("/org/brailleblaster/ebraille/index_template.html")?.bufferedReader(Charsets.UTF_8)?.readText() ?: FALLBACK_TEMPLATE
        val html = Jsoup.parse(template)
        return html
    }
}
