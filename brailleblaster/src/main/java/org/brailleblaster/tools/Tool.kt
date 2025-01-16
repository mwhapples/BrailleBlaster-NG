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
package org.brailleblaster.tools

import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.perspectives.mvc.menu.MenuManager
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.EmphasisItem
import org.brailleblaster.perspectives.mvc.menu.EnableListener
import org.brailleblaster.perspectives.mvc.menu.SharedItem
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.BuildMenuEvent
import org.brailleblaster.perspectives.mvc.modules.views.DebugModule
import org.brailleblaster.perspectives.mvc.modules.views.EmphasisModule
import org.brailleblaster.usage.logEnd
import org.brailleblaster.usage.logException
import org.brailleblaster.usage.logStart
import org.eclipse.swt.SWT

interface Tool {
    val id: String
        get() = javaClass.simpleName
    fun onRun(bbData: BBSelectionData)
    fun run(bbData: BBSelectionData) {
        val usage = bbData.wpManager.usageManager.logger
        usage.logStart(id)
        try {
            onRun(bbData)
        } catch (e: Exception) {
            usage.logException(id, e)
            throw e
        } finally {
            usage.logEnd(id)
        }
    }
}
interface ToggleTool : Tool {
    val active: Boolean
}

interface MenuTool : Tool {
    val topMenu: TopMenu
    val title: String
    val accelerator: Int
        get() = 0
    val swtOpts: Int
        get() = SWT.NONE
    val enabled: Boolean
        get() = true
    val enableListener: EnableListener?
        get() = null
    val sharedItem: SharedItem?
        get() = null
}
interface CheckMenuTool : ToggleTool, MenuTool

interface MenuToolListener : MenuTool, SimpleListener {
    override fun onEvent(event: SimpleEvent) {
        if (event is BuildMenuEvent) {
            MenuManager.addMenuItem(this)
        }
    }
}
interface DebugMenuToolListener : MenuToolListener {
    override val topMenu: TopMenu
        get() = TopMenu.DEBUG
    override fun onEvent(event: SimpleEvent) {
        if (event is  BuildMenuEvent && DebugModule.enabled) {
            MenuManager.addMenuItem(this)
        }
    }
}
interface EmphasisMenuTool : MenuToolListener {
    override val topMenu: TopMenu
        get() = TopMenu.EMPHASIS
    val emphasis: EmphasisItem
    override val title: String
        get() = emphasis.longName

    override fun onRun(bbData: BBSelectionData) {
        EmphasisModule.addEmphasis(bbData.manager.simpleManager, emphasis.emphasisType)
    }
}
