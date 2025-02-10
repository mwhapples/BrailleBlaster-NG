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

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.BBX
import org.brailleblaster.utils.xom.childNodes

@Suppress("UNUSED")
class ContainerStyleToBlockImportFixer : AbstractFixer() {
    override fun fix(matchedNode: Node) {
        BBX.CONTAINER.STYLE.assertIsA(matchedNode)
        val matchedContainer = matchedNode as Element
        val style = BBX._ATTRIB_OVERRIDE_STYLE[matchedContainer]
        for (curChild in matchedContainer.childNodes) {
            if (curChild is Element && BBX.BLOCK.STYLE.isA(curChild)) {
                BBX._ATTRIB_OVERRIDE_STYLE[curChild] = style
            }
        }
        BBX._ATTRIB_FIXER_TODO.assertAndDetach(
            BBX.FixerTodo.CONTAINER_STYLE_TO_BLOCK,
            matchedContainer
        )
        BBX.transform(matchedContainer, BBX.CONTAINER.OTHER)
        BBX._ATTRIB_OVERRIDE_STYLE.detach(matchedContainer)
    }
}