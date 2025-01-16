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

import kotlin.jvm.JvmOverloads
import org.brailleblaster.utd.config.StyleDefinitions
import java.io.Serializable

class ComparableStyle @JvmOverloads constructor(
    @field:Transient val styleDefs: StyleDefinitions = StyleDefinitions(),
    var styleName: String? = ""
) : Serializable {

    constructor(styleName: String?) : this(StyleDefinitions(), styleName)

    /**
     * Checks that the style of the argument is an exact match for this style.
     *
     * @param matchName The style to be tested.
     * @return True if both styles are exactly the same style, otherwise false is returned.
     */
    fun isExactStyle(matchName: String?): Boolean {
        return (styleName.equals(matchName))
    }

    /**
     * Check if the style is either the same style or a substyle of this style.
     *
     * @param matchName The style to check.
     * @return True if the style to match against is either the same style or if it is a substyle of
     * this one (IE. if one of the styles it is based on match this style). Otherwise false is
     * returned.
     */
    fun isInstanceOfStyle(matchName: String): Boolean {
        var style = styleDefs.getStyleByName(matchName)
        return if (style != null) {
            while (style != null) {
                if (isExactStyle(style.name)) {
                    return true
                }
                style = style.baseStyle
            }
            false
        } else {
            isExactStyle(matchName)
        }
    }
}