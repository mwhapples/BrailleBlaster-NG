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
package org.brailleblaster.util.swt

import org.brailleblaster.util.swt.EasyListeners.selection
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Composite
import java.util.function.Consumer

class ComboBuilder(parent: Composite, style: Int) : AbstractSWTBuilder<Combo, ComboBuilder>(Combo(parent, style)) {
    fun add(text: String?): ComboBuilder {
        widget.add(text)
        return this
    }

    /**
     * Trigger listener when this item is selected
     */
    fun add(text: String?, onSelect: Consumer<SelectionEvent?>): ComboBuilder {
        widget.add(text)
        val index = widget.itemCount - 1
        onSelect { e: SelectionEvent? ->
            if (widget.selectionIndex == index) {
                onSelect.accept(e)
            }
        }
        return this
    }

    fun text(text: String?): ComboBuilder {
        widget.text = text
        return this
    }

    fun onSelect(onSelect: Consumer<SelectionEvent>): ComboBuilder {
        selection(widget, onSelect)
        return this
    }

    fun select(index: Int): ComboBuilder {
        widget.select(index)
        return this
    }
}
