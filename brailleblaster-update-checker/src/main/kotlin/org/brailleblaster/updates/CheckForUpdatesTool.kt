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
package org.brailleblaster.updates

import org.brailleblaster.perspectives.mvc.BBSimpleManager
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.AppStartedEvent
import org.brailleblaster.perspectives.mvc.events.BuildMenuEvent
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.MenuManager
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.tools.MenuTool
import org.eclipse.swt.widgets.Display

object CheckForUpdatesTool : MenuTool, BBSimpleManager.SimpleListener {
    override val topMenu: TopMenu = TopMenu.HELP
    override val title: String = "Check For Updates"
    override fun onRun(bbData: BBSelectionData) {
        Thread(CheckUpdates(true, Display.getCurrent())).start()
    }

    override fun onEvent(event: SimpleEvent) = when(event) {
        is AppStartedEvent -> Thread(CheckUpdates(false, Display.getCurrent())).start()
        is BuildMenuEvent -> MenuManager.addMenuItem(this)
        else -> {}
    }
}