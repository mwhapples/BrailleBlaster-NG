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

import org.brailleblaster.util.swt.EasyListeners.modify
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Text
import java.util.function.Consumer

class TextBuilder(parent: Composite, style: Int) : AbstractSWTBuilder<Text, TextBuilder>(Text(parent, style)) {
    fun text(text: String?): TextBuilder {
        widget.text = text
        return this
    }

    fun onModify(onModify: Consumer<ModifyEvent>): TextBuilder {
        modify(widget, onModify)
        return this
    }
}
