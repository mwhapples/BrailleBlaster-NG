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
package org.brailleblaster.perspectives.braille.views.wp.listeners

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.braille.views.wp.WPView
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent

class WPScrollListener(val manager: Manager, val wpView: WPView, val sender: Sender) : SelectionAdapter() {
    override fun widgetSelected(e: SelectionEvent) {
        wpView.setListenerLock(true)
        wpView.checkStatusBar(sender)
        wpView.setListenerLock(false)

        if (manager.text.hasChanged) manager.text.update(false)

        if (!wpView.lock && (e.detail == SWT.NONE || e.detail == SWT.ARROW_DOWN || e.detail == SWT.ARROW_UP)) {
            if (wpView.view.verticalBar.selection == (wpView.view.verticalBar.maximum - wpView.view.verticalBar.thumb)) manager.incrementView()
            else if (wpView.view.verticalBar.selection == 0) manager.decrementView()
        }
    }
}
