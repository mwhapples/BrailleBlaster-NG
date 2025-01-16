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

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import java.io.Serializable

/**
 * Class to hold settings of a shortcut
 *
 */
class Shortcut : IShortcut, Serializable {
    override var id = ""
        set(id) {
            require(id.isNotBlank()) { "Shortcut ID cannot be blank" }
            field = id
        }
    override var keyCombination = ""
        set(keyCombination) {
            require(keyCombination.isNotBlank()) { "Key combination cannot be blank" }
            field = keyCombination
        }

    /**
     * Create shortcut with the given id and key combination
     * @param id
     * @param keyCombination
     */
    constructor(id: String, keyCombination: String) {
        this.id = id
        this.keyCombination = keyCombination
    }

    /**
     * Create shortcut with the Java specified defaults
     * @see org.brailleblaster.utd.config.ShortcutDefinitions.addShortcut
     */
    constructor()

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other)
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE)
    }
}
