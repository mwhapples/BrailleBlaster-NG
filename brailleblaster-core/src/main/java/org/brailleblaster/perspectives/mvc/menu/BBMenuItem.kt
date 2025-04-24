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

import org.brailleblaster.perspectives.mvc.menu.MenuManager.addToListenerMap
import org.brailleblaster.perspectives.mvc.menu.MenuManager.menuItemAcceleratorSuffix
import org.brailleblaster.util.FormUIUtils
import org.brailleblaster.wordprocessor.WPManager
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import java.util.function.Consumer

/**
 * An abstraction of SWT's MenuItem to be used internally in MenuManager
 */
open class BBMenuItem internal constructor(
    override val menu: TopMenu?,
    val text: String,
    val accelerator: Int,
    val onSelect: Consumer<BBSelectionData>,
    val isEnabled: Boolean = true,
    var swtOpts: Int = SWT.PUSH,
    val sharedItem: SharedItem? = null
) : IBBMenu {
    var listener: EnableListener? = null

    override fun build(parentMenu: Menu): MenuItem {
        val item = MenuItem(parentMenu, swtOpts)
        val textBuilder = StringBuilder()
        textBuilder.append(text)
        if (accelerator > 0) {
            menuItemAcceleratorSuffix(textBuilder, accelerator)
            item.accelerator = accelerator
        }
        item.text = textBuilder.toString()
        FormUIUtils.addSelectionListener(item) { e: SelectionEvent ->
            //SWT dispatches two selection events when a radio button is clicked, one for the new selection
            //and one for the previous selection
            if (this !is BBRadioMenuItem || (e.widget as MenuItem).selection) {
                val data = BBSelectionData(e.widget, WPManager.getInstance())
                data.menuItem = item
                if (sharedItem != null && MenuManager.sharedToolBars.containsKey(
                        sharedItem
                    )
                ) data.toolBarItem = MenuManager.sharedToolBars[sharedItem]
                onSelect.accept(data)
            }
        }
        if (listener != null) {
            addToListenerMap(listener!!, item)
        }
        if (sharedItem != null) {
            MenuManager.sharedMenuItems[sharedItem] = item
        }
        item.isEnabled = isEnabled
        return item
    }

    override fun copy(): BBMenuItem {
        return BBMenuItem(
            menu = menu,
            text = text,
            accelerator = accelerator,
            onSelect = onSelect,
            isEnabled = true,
            swtOpts = swtOpts
        )
    }
}
