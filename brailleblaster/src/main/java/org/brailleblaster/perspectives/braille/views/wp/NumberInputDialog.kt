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
package org.brailleblaster.perspectives.braille.views.wp

import org.brailleblaster.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.util.Utils
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

class NumberInputDialog(parent: Shell?) : Dialog(parent) {
    private var value = 0

    /**
     * Makes the dialog visible.
     */
    fun open(title: String?, callback: (Int) -> Unit) {
        val parent = parent
        val shell = Shell(parent, SWT.TITLE or SWT.BORDER or SWT.APPLICATION_MODAL or SWT.CENTER)
        shell.text = title
        shell.layout = GridLayout(2, true)
        val label = Label(shell, SWT.NULL)
        label.text = localeHandler["msgNumberInputDialog"]
        val text = Text(shell, SWT.SINGLE or SWT.BORDER)
        val buttonOK = Button(shell, SWT.PUSH)
        buttonOK.text = localeHandler["lblOk"]
        buttonOK.layoutData = GridData(GridData.HORIZONTAL_ALIGN_END)
        Utils.addSwtBotKey(buttonOK, SWTBOT_OK_BUTTON)
        val buttonCancel = Button(shell, SWT.PUSH)
        buttonCancel.text = localeHandler["lblCancel"]
        text.addListener(SWT.Modify) {
            try {
                val inputText = text.text
                val numbers = "0123456789"
                if (inputText.isEmpty() || !inputText.all { it.toString() in numbers }) return@addListener
                value = inputText.toInt()
                buttonOK.isEnabled = true
            } catch (e: Exception) {
                buttonOK.isEnabled = false
            }
        }
        buttonOK.addListener(SWT.Selection) {
            shell.dispose()
            callback(value)
        }
        buttonCancel.addListener(SWT.Selection) {
            value = -1
            shell.dispose()
        }
        shell.addListener(SWT.Traverse) { event: Event -> if (event.detail == SWT.TRAVERSE_ESCAPE) event.doit = false }
        text.text = ""
        shell.defaultButton = buttonOK
        shell.pack()
        shell.open()
    }

    companion object {
        private val localeHandler = getDefault()
        const val SWTBOT_OK_BUTTON = "numberInputDialog.okButton"
    }
}
