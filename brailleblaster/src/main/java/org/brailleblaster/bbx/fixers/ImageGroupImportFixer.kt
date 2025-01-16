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
import org.brailleblaster.utd.utils.xom.attributes
import org.brailleblaster.utd.utils.xom.childNodes
import org.slf4j.LoggerFactory

@Suppress("UNUSED")
class ImageGroupImportFixer : AbstractFixer() {
    override fun fix(matchedNode: Node) {
        BBX.CONTAINER.IMAGE.assertIsA(matchedNode)
        val origGroup = matchedNode as Element
        BBX._ATTRIB_FIXER_TODO.assertAndDetach(BBX.FixerTodo.CONVERT_IMAGE_GROUP, origGroup)
        val groupId = imageIdCounter++

        //verify there actually is an image tag
        if (matchedNode.childNodes.none { node: Node -> BBX.SPAN.IMAGE.isA(node) }
        ) {
            log.warn("prodnote without image tag, skipping")
            return
        }

        //split up group into seperate image containers, one image and one description per container
        val groups: MutableList<Element> = ArrayList()
        var addedImage = false
        var curGroup = origGroup
        groups.add(curGroup)
        BBX.CONTAINER.IMAGE.ATTRIB_GROUP_ID[curGroup] = groupId
        for (curChild in matchedNode.childNodes) {
            if (BBX.SPAN.IMAGE.isA(curChild)) {
                if (!addedImage) {
                    addedImage = true
                } else {
                    val newGroup = BBX.CONTAINER.IMAGE.create()
                    BBX.CONTAINER.IMAGE.ATTRIB_GROUP_ID[newGroup] = groupId
                    curGroup.parent.insertChild(
                        newGroup,
                        curGroup.parent.indexOf(curGroup) + 1
                    )
                    groups.add(newGroup)
                    curGroup = newGroup
                }
                for (curOldAttribute in (curChild as Element).attributes) {
                    val newAttribute = Attribute(curOldAttribute)
                    curGroup.addAttribute(newAttribute)
                }
                curChild.detach()
            } else if (curChild !in curGroup.childNodes) {
                curChild.detach()
                curGroup.appendChild(curChild)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ImageGroupImportFixer::class.java)

        /**
         * Counter to generate unique id's. Safe as id isn't added anywhere else
         * and this is only run once
         */
		@JvmField
		var imageIdCounter = 0
    }
}