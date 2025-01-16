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

import org.brailleblaster.localization.LocaleHandler.Companion.getDefault
import org.eclipse.swt.SWT
import org.eclipse.swt.events.FocusEvent
import org.eclipse.swt.events.FocusListener
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import org.eclipse.swt.widgets.List

/**
 * Generates the style selection popup window
 */
class StyleSelector
/**
 * @param parent
 */
    (parent: Shell?) : Dialog(parent) {
    private var selection = 0

    /**
     * Makes the dialog visible.
     *
     * @return
     */
    fun open(title: String?, styleIds: Array<String>, categoryName: String?): Int {
        selection = -1
        val parent = parent
        val shell = Shell(parent, SWT.TITLE or SWT.BORDER or SWT.APPLICATION_MODAL or SWT.CENTER)
        shell.text = title
        shell.layout = GridLayout(2, true)

        val lblSelectedStyle = Label(shell, SWT.NULL)
        lblSelectedStyle.text = localeHandler["selectedStyle"] + ":"

        val lblStyleName = Label(shell, SWT.NULL)
        lblStyleName.text = localeHandler[categoryName!!]

        val lblLevels = Label(shell, SWT.NULL)
        lblLevels.text = localeHandler["lblLoadoutsLevelsOfNesting"]
        val data = GridData(SWT.BEGINNING, SWT.BEGINNING, false, false)
        lblLevels.layoutData = data

        val list = List(shell, SWT.BORDER or SWT.SINGLE or SWT.V_SCROLL)
        for (i in styleIds.indices) {
            var optionName = styleIds[i]
            optionName = optionName.substring(optionName.indexOf("/") + 1)
            list.add((i + 1).toString() + " - " + localeHandler[optionName])
        }

        list.setBounds(0, 0, 100, 300)
        list.select(0)
        list.addListener(SWT.Selection) { selection = list.selectionIndex }

        list.addListener(SWT.KeyDown) { e: Event ->
            if (e.keyCode in 49..56) {
                val index = e.keyCode - 48
                if (index <= list.itemCount) {
                    list.select(index - 1)
                }
                selection = index - 1
            }
        }

        val buttonOK = Button(shell, SWT.PUSH)
        buttonOK.text = localeHandler["lblOk"]
        buttonOK.layoutData = GridData(GridData.HORIZONTAL_ALIGN_END)

        val buttonCancel = Button(shell, SWT.PUSH)
        buttonCancel.text = localeHandler["lblCancel"]

        buttonOK.addListener(SWT.Selection) { shell.dispose() }

        buttonCancel.addListener(SWT.Selection) {
            selection = -1
            shell.dispose()
        }

        shell.addListener(SWT.Traverse) { event: Event ->
            if (event.detail == SWT.TRAVERSE_ESCAPE) event.doit = false
        }


        //shell.setDefaultButton(buttonOK);
        shell.addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent) {
                // TODO Auto-generated method stub
            }

            override fun focusGained(e: FocusEvent) {
                // TODO Auto-generated method stub
                list.setFocus()
            }
        })

        shell.pack()
        shell.open()

        val display = parent.display

        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }

        return selection
    }

    companion object {
        private val localeHandler = getDefault()
    }
}
