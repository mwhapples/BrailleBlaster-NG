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
package org.brailleblaster.perspectives.mvc.modules.views

import org.brailleblaster.BBIni
import org.brailleblaster.abstractClasses.BBEditorView
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.BBViewListener
import org.brailleblaster.perspectives.mvc.events.BuildMenuEvent
import org.brailleblaster.perspectives.mvc.menu.MenuManager
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.VerifyKeyListener
import org.eclipse.swt.events.VerifyEvent

class DebugModule(private val currentEditor: Manager) : AbstractModule(), SimpleListener, VerifyKeyListener,
    BBViewListener {
    init {
        sender = Sender.DEBUG
    }

    override fun onEvent(event: SimpleEvent) {
        if (enabled && event is BuildMenuEvent) {
            MenuManager.addMenuItem(XMLViewerTool)
            MenuManager.addMenuItem(StyleViewerTool)
            MenuManager.addMenuItem(MapListViewerTool)
            MenuManager.addMenuItem(PageNumberViewerTool)
            MenuManager.addMenuItem(SaveWithBrlTool)
            MenuManager.addMenuItem(SaveFormattedWithBrlTool)
            MenuManager.addMenuItem(SaveFormattedWithoutBrlTool)
            MenuManager.addMenuItem(SetLogTool)
            MenuManager.addMenuItem(DisableFocusLostListenerTool)
            MenuManager.addMenuItem(TriggerExceptionTool)
            MenuManager.addMenuItem(TriggerFatalExceptionTool)
            MenuManager.addMenuItem(DarkModeToggleTool)
            MenuManager.addMenuItem(MathTableExceptionsTool)
            MenuManager.addMenuItem(TogglePandocImportTool)
        }
    }

    override fun initializeListener(view: BBEditorView) {
        view.view.addVerifyKeyListener(this)
    }

    override fun removeListener(view: BBEditorView) {
        view.view.removeVerifyKeyListener(this)
    }

    override fun verifyKey(e: VerifyEvent) {
        if (e.stateMask == SWT.MOD1 + SWT.MOD2 && e.keyCode == '`'.code) {
            enabled = !enabled //Toggle
            currentEditor.simpleManager.initMenu(currentEditor.wp.shell)
        }
    }

    class DebugFatalException(message: String?) : RuntimeException(message)

    companion object {
        @JvmField
        var enabled = BBIni.debugging
    }
}
