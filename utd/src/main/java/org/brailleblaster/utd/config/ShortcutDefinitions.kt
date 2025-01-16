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
package org.brailleblaster.utd.config

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.brailleblaster.utd.Shortcut
import java.util.*

/**
 * Gets the shortcuts definition list
 */
class ShortcutDefinitions {
    private val _shortcuts: MutableList<Shortcut> = ArrayList()

    fun addShortcut(shortcutInput: Iterable<Shortcut?>) {
        for (curShortcut in shortcutInput) addShortcut(curShortcut)
    }

    fun addShortcut(shortcut: Shortcut?) {
        if (shortcut == null) throw NullPointerException("shortcut")
        if (shortcut.id.isBlank()) throw RuntimeException("Shortcut $shortcut cannot have a blank id")
        val existingShortcut = getShortcutById(shortcut.id)
        if (existingShortcut != null) throw RuntimeException("Shortcut $shortcut is a duplicate of existing shortcut $existingShortcut")
        _shortcuts.add(shortcut)
    }

    fun addShortcut(id: String, keyCombination: String): Shortcut {
        if (id.isBlank()) throw RuntimeException("Shortcut id cannot be blank.")
        if (keyCombination.isBlank()) throw RuntimeException("Key combination cannot be blank.")
        val existingShortcut = getShortcutById(id)
        if (existingShortcut != null) throw RuntimeException("Shortcut $id is a duplicate of existing shortcut $existingShortcut")

        val newShortcut = Shortcut(id, keyCombination)
        _shortcuts.add(newShortcut)
        return newShortcut
    }

    fun removeShortcut(shortcut: Shortcut?) {
        if (shortcut == null) throw NullPointerException("shortcut")
        if (!_shortcuts.contains(shortcut)) throw RuntimeException("Shortcut $shortcut is not contained in definitions!")
        _shortcuts.remove(shortcut)
    }

    val shortcuts: List<Shortcut>
        get() = _shortcuts.toList()

    /**
     * Return first shortcut that has the specified id
     *
     * @param id
     * @return
     */
    fun getShortcutById(id: String): Shortcut? = _shortcuts.firstOrNull { it.id == id }

    /**
     * Return first shortcut that has the specified key combination
     *
     * @param keyCombination
     * @return
     */
    fun getShortcutByKeyCombination(keyCombination: String): Shortcut? = _shortcuts.firstOrNull { it.keyCombination == keyCombination }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other)
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}
