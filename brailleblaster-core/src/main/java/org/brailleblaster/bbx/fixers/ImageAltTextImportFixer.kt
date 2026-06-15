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
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.matchers.INodeMatcher
import org.brailleblaster.utd.properties.EmphasisType

/**
 * Handles standalone SPAN.IMAGE elements (i.e. images NOT inside an imggroup /
 * CONTAINER.IMAGE with CONVERT_IMAGE_GROUP) that carry a non-empty alt attribute.
 *
 * For each such image, a transcriber note block (BLOCK.STYLE("7-5") containing an
 * INLINE.EMPHASIS(TRANS_NOTE)) is inserted as a sibling immediately after:
 *  - the nearest ancestor BLOCK, when the image is inside a block (inline context), or
 *  - the image span itself, when it is a direct child of a SECTION/CONTAINER.
 *
 * The alt attribute is removed after processing so the fixer matcher does not
 * re-match the same element on a subsequent pass.
 */
@Suppress("UNUSED")
class ImageAltTextImportFixer : AbstractFixer() {
    override fun fix(matchedNode: Node) {
        BBX.SPAN.IMAGE.assertIsA(matchedNode)
        val imgSpan = matchedNode as Element

        val altText = imgSpan.getAttributeValue("alt")?.trim() ?: return
        if (altText.isBlank()) return

        // Remove alt so this element is not matched again on a later pass.
        imgSpan.removeAttribute(imgSpan.getAttribute("alt"))

        val transNoteBlock = BBX.BLOCK.STYLE.create("7-5")
        val transNoteInline = BBX.INLINE.EMPHASIS.create(EmphasisType.TRANS_NOTE)
        transNoteInline.appendChild(altText)
        transNoteBlock.appendChild(transNoteInline)

        // If the image is inside a BLOCK (inline image context), insert the TN after
        // the parent block to avoid creating a BLOCK-inside-BLOCK structure.
        // Otherwise insert it directly after the span itself.
        val imgParent = imgSpan.parent as Element
        val insertParent: Element
        val insertAfter: Element
        if (BBX.BLOCK.isA(imgParent)) {
            insertAfter = imgParent
            insertParent = imgParent.parent as Element
        } else {
            insertAfter = imgSpan
            insertParent = imgParent
        }
        insertParent.insertChild(transNoteBlock, insertParent.indexOf(insertAfter) + 1)
    }

    /** Matches SPAN.IMAGE elements that still carry a non-empty alt attribute. */
    @Suppress("UNUSED")
    class HasNonEmptyAltMatcher : INodeMatcher {
        override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
            if (node !is Element || !BBX.SPAN.IMAGE.isA(node)) return false
            val alt = node.getAttributeValue("alt")?.trim()
            return !alt.isNullOrBlank()
        }
    }
}
