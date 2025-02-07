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
package org.brailleblaster.perspectives.braille.views.tree

import org.brailleblaster.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.perspectives.braille.Manager
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Shell

class BookTreeDialog(manager: Manager) {
    val shell: Shell = Shell(manager.wp.shell, SWT.SHELL_TRIM or SWT.APPLICATION_MODAL)
    val navigate: Button
    val close: Button
    val bk: BookTree2

    init {
        shell.text = localeHandler["bookTree"]
        setShellSize(shell)
        val layout = GridLayout(2, false)
        shell.layout = layout

        bk = BookTree2(manager, this)
        val gd = GridData(GridData.FILL_BOTH or GridData.GRAB_HORIZONTAL)
        gd.horizontalSpan = 2
        bk.tree.layoutData = gd

        navigate = Button(shell, SWT.PUSH)
        navigate.text = localeHandler["buttonNavigate"]

        close = Button(shell, SWT.PUSH)
        close.text = localeHandler["buttonClose"]

        initializeListeners()
        shell.open()
    }

    private fun initializeListeners() {
        navigate.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val success = bk.navigate()
                if (success) close()
            }
        })

        close.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                close()
            }
        })
    }

    fun close() {
        bk.dispose()
        shell.close()
    }

    private fun setShellSize(shell: Shell) {
        val display = shell.display
        val monitor = display.primaryMonitor
        val rect = if (monitor != null) monitor.clientArea
        else display.bounds

        shell.setSize(rect.width / 2, rect.height / 2)
    }

    companion object {
        private val localeHandler = getDefault()
    }
}
