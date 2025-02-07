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
package org.brailleblaster.bbx.fixers

import jakarta.xml.bind.annotation.XmlAttribute
import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.BBX.*
import org.brailleblaster.utd.exceptions.NodeException

@Suppress("UNUSED")
class TransformingImportFixer(
    @field:XmlAttribute val sectionType: SectionSubType?,
    @field:XmlAttribute val containerType: ContainerSubType?,
    @field:XmlAttribute val blockType: BlockSubType?,
    @field:XmlAttribute val inlineType: InlineSubType?,
    @field:XmlAttribute val spanType: SpanSubType?
) : AbstractFixer() {
    // No-arg constructor for JAXB
    private constructor() : this(null, null, null, null, null)

    override fun fix(matchedNode: Node) {
        if (matchedNode !is Element) {
            throw NodeException("Expected element, got ", matchedNode)
        }
        val configuredFields: MutableSet<Any?> = HashSet()
        configuredFields.add(sectionType)
        configuredFields.add(containerType)
        configuredFields.add(blockType)
        configuredFields.add(spanType)
        configuredFields.add(inlineType)
        //nulls will be condensed into one entry + set entry = 2
        require(configuredFields.size == 2) { "Missing correctly set SubType" }
        configuredFields.remove(null)
        val subType = configuredFields.iterator().next() as SubType
        transform(matchedNode, subType)
    }
}