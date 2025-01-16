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

import org.brailleblaster.util.swt.EasySWT.error
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import org.eclipse.swt.widgets.Shell
import java.util.*
import java.util.function.Consumer

/**
 * Provides an easy way to create a menu for an SWT shell. Notes:
 *
 *
 *  * Each instance of `add____Item` will add the item to the last menu option made
 * with `addToMenu`. An exception is throw if an `add____Item` method
 * is called before `addToMenu`
 *  * The text of each menu item will automatically be changed to include the passed
 * accelerator. If an accelerator is not desired, `-1` can passed instead.
 *
 */
class MenuBuilder {
    private val parent: Shell
    @JvmField
    val menu: Menu
    @JvmField
    val barItems: MutableList<Menu>

    constructor(parent: Shell) {
        this.parent = parent
        menu = Menu(parent, SWT.BAR)
        barItems = ArrayList()
    }

    /**
     * For use by SubMenuBuilder
     */
    internal constructor(parent: Shell, style: Int) {
        this.parent = parent
        menu = Menu(parent, style)
        barItems = ArrayList()
    }

    @JvmOverloads
    fun addToMenu(text: String?, enabled: Boolean = true): MenuBuilder {
        val newItem = MenuItem(menu, SWT.CASCADE)
        newItem.text = text
        val newMenu = Menu(parent, SWT.DROP_DOWN)
        newItem.menu = newMenu
        barItems.add(newMenu)
        newItem.isEnabled = enabled
        return this
    }

    fun addPushItem(
        text: String, accelerator: Int, enabled: Boolean, onClick: Consumer<SelectionEvent>?
    ): MenuBuilder {
        return addItem(text, SWT.PUSH, accelerator, onClick, enabled, null)
    }

    fun addPushItem(text: String, accelerator: Int, onClick: Consumer<SelectionEvent>?): MenuBuilder {
        return addPushItem(text, accelerator, true, onClick)
    }

    fun addCheckItem(
        text: String,
        accelerator: Int,
        enabled: Boolean,
        selected: Boolean,
        onClick: Consumer<SelectionEvent>?
    ): MenuBuilder {
        return addItem(text, SWT.CHECK, accelerator, onClick, enabled, selected)
    }

    fun addCheckItem(
        text: String, accelerator: Int, selected: Boolean, onClick: Consumer<SelectionEvent>?
    ): MenuBuilder {
        return addCheckItem(text, accelerator, true, selected, onClick)
    }

    private fun addItem(
        text: String,
        style: Int,
        accelerator: Int,
        onClick: Consumer<SelectionEvent>?,
        enabled: Boolean,
        selected: Boolean?
    ): MenuBuilder {
        require(!(selected != null && (style and SWT.PUSH) != 0)) { "Selected does not apply to push buttons" }
        if (barItems.isEmpty()) error(EasySWT.NOMENU)
        val lastMenu = barItems[barItems.size - 1]
        val newItem = MenuItem(lastMenu, style)
        newItem.text = text
        if (onClick != null) {
            newItem.addSelectionListener(
                object : SelectionAdapter() {
                    override fun widgetSelected(e: SelectionEvent) {
                        onClick.accept(e)
                    }
                })
        }
        if (accelerator >= 0) {
            val accelString = StringBuilder()
            if ((accelerator and SWT.MOD1) != 0) {
                val onMac = (
                        System.getProperty("os.name") != null
                                && System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac"))
                if (onMac) accelString.append("⌘ + ")
                else accelString.append("Ctrl + ")
            }
            if ((accelerator and SWT.MOD2) != 0) {
                accelString.append("Shift + ")
            }
            if ((accelerator and SWT.MOD3) != 0) {
                accelString.append("Alt + ")
            }
            if ((accelerator and SWT.MOD4) != 0) { // Mac only
                accelString.append("Ctrl + ")
            }
            if (accelString.isNotEmpty()) {
                val character = (255 and accelerator).toChar()
                newItem.text = newItem.text + "\t" + accelString + character
                newItem.accelerator = accelerator
            }
        }
        if (selected != null) {
            newItem.selection = selected
        }
        newItem.isEnabled = enabled
        return this
    }

    fun addSubMenu(text: String?, newSubMenu: SubMenuBuilder): MenuBuilder {
        if (barItems.isEmpty()) error(EasySWT.NOMENU)
        val lastMenu = barItems[barItems.size - 1]
        val newItem = MenuItem(lastMenu, SWT.CASCADE)
        newItem.text = text
        newItem.menu = newSubMenu.mb.menu
        return this
    }

    fun build(): Menu {
        parent.menuBar = menu
        return menu
    }
}
