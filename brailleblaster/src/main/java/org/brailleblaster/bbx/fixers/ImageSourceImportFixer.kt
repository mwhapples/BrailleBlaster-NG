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

import nu.xom.Attribute
import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.BBX

@Suppress("UNUSED")
class ImageSourceImportFixer : AbstractFixer() {
    override fun fix(matchedNode: Node) {
        BBX.SPAN.IMAGE.assertIsA(matchedNode)
        val image = matchedNode as Element
        process(image, image.getAttribute("src"))
    }

    private fun process(elem: Element, srcAttrib: Attribute?) {
        if (srcAttrib == null) {
            return
        }
        srcAttrib.setNamespace(
            BBX.SPAN.IMAGE.ATTRIB_SOURCE.nsPrefix,
            BBX.SPAN.IMAGE.ATTRIB_SOURCE.nsUrl
        )
        srcAttrib.localName = BBX.SPAN.IMAGE.ATTRIB_SOURCE.name
    }
}