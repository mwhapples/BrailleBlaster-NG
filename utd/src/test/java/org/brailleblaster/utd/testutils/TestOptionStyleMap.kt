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
package org.brailleblaster.utd.testutils

import nu.xom.Element
import org.brailleblaster.utd.Style
import org.brailleblaster.utd.internal.DynamicOptionStyleMap

/**
 * Dynamically create styles with attributes
 */
class TestOptionStyleMap : DynamicOptionStyleMap( // use defualt NS and prefix for less typing
    null,
    null,  // no attribte prefix for less typing
    "",  // Style id/name prefix: dos = dynamic option style
    "dos-",
    "dos"
) {
    override fun getBaseStyle(elem: Element): Style {
        return DEFAULT_STYLE
    }

    override fun onNewStyle(newStyle: Style) {
        // Not using StyleDefinitions so do nothing here
    }

    companion object {
        private val DEFAULT_STYLE = Style()
    }
}
