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

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.utd.properties.ContentType
import java.util.EnumSet
import org.brailleblaster.utd.properties.EmphasisType

/**
 * This class will contain all the relevant properties required for translation. It will be used
 * for spannable text translations.
 */
class TextSpan @JvmOverloads constructor(val node: Node?, var text: String = "", var contentType: ContentType = ContentType.StandardText) {

    var emphasis: EnumSet<EmphasisType> = EnumSet.noneOf(EmphasisType::class.java)
    var isTranslated = false
    var brlElement: Element? = null

    fun addEmphasis(add: EmphasisType) {
        emphasis.add(add)
    }

    fun removeEmphasis(rem: EmphasisType) {
        emphasis.remove(rem)
    }
}