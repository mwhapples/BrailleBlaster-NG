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
package org.brailleblaster.perspectives.mvc.modules.misc

import org.brailleblaster.abstractClasses.BBEditorView
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.BBViewListener
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.VerifyKeyListener
import org.eclipse.swt.events.VerifyEvent

class RefreshModule(private val manager: Manager) : SimpleListener, VerifyKeyListener, BBViewListener {
    override fun onEvent(event: SimpleEvent) {
//		MenuManager.addMenuItem(TopMenu.VIEW, LocaleHandler.get("&RefreshTranslation"), SWT.F5, e -> e.manager.refresh(), SharedItem.REFRESH);
    }

    override fun verifyKey(e: VerifyEvent) {
        if (e.keyCode == SWT.F5) {
            manager.refresh()
            while (!manager.wp.shell.isDisposed) {
                val display = manager.wp.shell.display
                if (!display.readAndDispatch()) display.sleep()
            }
        }
    }

    override fun initializeListener(view: BBEditorView) {
        view.view.addVerifyKeyListener(this)
    }

    override fun removeListener(view: BBEditorView) {
        view.view.removeVerifyKeyListener(this)
    }
}