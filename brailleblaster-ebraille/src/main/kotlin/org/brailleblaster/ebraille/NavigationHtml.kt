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
import org.jsoup.nodes.Element

private data class HeadingItem(val path: String, val element: Element)

private val HEADINGS_LEVEL_MAP = mapOf("h1" to 0, "h2" to 1, "h3" to 2, "h4" to 3, "h5" to 4, "h6" to 5)

private val ID_RANGE = 0..Long.MAX_VALUE

private class IdGenerator {
    private val generatorMap = mutableMapOf<String, Iterator<Long>>()
    fun nextId(path: String): Long = generatorMap.getOrPut(path) { ID_RANGE.iterator() }.next()
}
object NavigationHtml {
    private val FALLBACK_TEMPLATE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops">
            <head>
              <meta charset="UTF-8"/>
              <link rel="publication" href="package.opf" type="application/oebps-package+xml"/>
            </head>
            <body></body>
            </html>
        """.trimIndent()
    fun createNavigationHtml(docs: Iterable<HtmlItem>): Document {
        val template = javaClass.getResourceAsStream("/org/brailleblaster/ebraille/index_template.html")?.bufferedReader(Charsets.UTF_8)?.readText() ?: FALLBACK_TEMPLATE
        val html = Jsoup.parse(template)
        html.body().appendChild(createHeadingsList(docs))
        return html
    }
    private fun createHeadingsList(docs: Iterable<HtmlItem>): Element = Element("nav").attr("role", "doc-toc").attr("aria-label", "Contents").attr("epub:type", "toc").append("<h2>⠠⠞⠁⠼ ⠷ ⠒⠞⠢⠞⠎</h2>").apply {
        val idGenerator = IdGenerator()
        val headings = docs.flatMap { doc -> doc.document.select(HEADINGS_LEVEL_MAP.keys.joinToString(separator = ", ")).map {
            if (it.id().isEmpty()) {
                it.id("h_${idGenerator.nextId(doc.path)}")
            }
            ListItem(HeadingItem(doc.path, it), HEADINGS_LEVEL_MAP.getOrDefault(it.tagName(), 0))
        } }
        appendChild(headings.toHtml(level = 0, containerFactory = { Element("ol") }, itemFactory = { listOf(Element("li").appendChild(Element("a").attr("href", "${it.element.path}#${it.element.element.id()}").appendText(it.element.element.text()))) }))
    }
}
