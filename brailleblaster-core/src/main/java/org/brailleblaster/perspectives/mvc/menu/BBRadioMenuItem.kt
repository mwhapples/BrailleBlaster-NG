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
package org.brailleblaster.perspectives.mvc.menu

import org.eclipse.swt.SWT
import java.util.function.Consumer

/**
 * An abstraction of SWT's MenuItem with the SWT.RADIO style to be used internally in MenuManager
 */
class BBRadioMenuItem internal constructor(
    menu: TopMenu?,
    text: String?,
    accelerator: Int,
    selected: Boolean,
    onSelect: Consumer<BBSelectionData>
) : BBCheckMenuItem(menu, text!!, accelerator, selected, onSelect) {
    init {
        swtOpts = SWT.RADIO
    }

    override fun copy(): BBRadioMenuItem {
        val copy = BBRadioMenuItem(menu, text, accelerator, isSelected, onSelect)
        copy.swtOpts = swtOpts
        return copy
    }
}
