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
package org.brailleblaster.util.ui

import org.brailleblaster.utils.OS
import org.brailleblaster.utils.os
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Listener

object MenuUtils {
    private val listeners: MutableMap<Display, MutableMap<Int, Listener>> = mutableMapOf()
    fun addSystemMenuItem(display: Display, id: Int, listener: Listener) {
        if (os === OS.Mac) {
            val systemMenu = display.systemMenu
            systemMenu.items.firstOrNull { it.id == id }?.let {
                val sysMenuMap = listeners.getOrPut(display) { mutableMapOf() }
                sysMenuMap[id]?.let { l -> it.removeListener(SWT.Selection, l) }
                sysMenuMap[id] = listener
                systemMenu.addListener(SWT.Selection, listener)
            }
        }
    }
}