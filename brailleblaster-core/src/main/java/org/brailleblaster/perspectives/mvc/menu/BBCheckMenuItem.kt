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
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import java.util.function.Consumer

/**
 * An abstraction of SWT's MenuItem with the SWT.CHECK style to be used internally in MenuManager
 */
open class BBCheckMenuItem @JvmOverloads internal constructor(
    menu: TopMenu?,
    text: String,
    accelerator: Int,
    var isSelected: Boolean,
    onSelect: Consumer<BBSelectionData>,
    sharedItem: SharedItem? = null,
    enabled: Boolean = true
) : BBMenuItem(menu, text, accelerator, enabled, SWT.CHECK, onSelect, sharedItem) {

    override fun build(parentMenu: Menu): MenuItem {
        val returnItem = super.build(parentMenu)
        returnItem.selection = isSelected
        return returnItem
    }

    override fun copy(): BBCheckMenuItem {
        val copy = BBCheckMenuItem(menu, text, accelerator, isSelected, onSelect)
        copy.swtOpts = swtOpts
        return copy
    }
}
