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

import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Shell
import java.util.function.Consumer

/**
 * Almost identical to MenuBuilder, but allows a readable distinction between a top-level menu and
 * a sub-menu.
 *
 * @see MenuBuilder.addSubMenu
 */
class SubMenuBuilder(parent: Shell?) {
    val mb: MenuBuilder = MenuBuilder(parent!!, SWT.DROP_DOWN)

    init {
        mb.barItems.add(mb.menu)
    }

    fun addPushItem(
        text: String?, accelerator: Int, onClick: Consumer<SelectionEvent>
    ): SubMenuBuilder {
        mb.addPushItem(text!!, accelerator, onClick)
        return this
    }

    fun addSubMenu(text: String?, newSubMenu: SubMenuBuilder?): SubMenuBuilder {
        mb.addSubMenu(text, newSubMenu!!)
        return this
    }
}
