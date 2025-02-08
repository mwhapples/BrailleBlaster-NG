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
import org.brailleblaster.wordprocessor.WPManager
import org.eclipse.swt.SWT
import org.eclipse.swt.events.FocusEvent
import org.eclipse.swt.events.FocusListener
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.List
import org.eclipse.swt.widgets.Shell

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer

/**
 * Generates the style selection popup window
 */
class AutoSaveDialog
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
    fun openAutoSaveDialog(title: String?, recentSaves: Iterable<Path>): Int {
        selection = -1
        val parent = parent
        val shell = Shell(parent, SWT.TITLE or SWT.BORDER or SWT.APPLICATION_MODAL or SWT.CENTER or SWT.CLOSE)
        shell.text = title
        shell.setLocation(250, 250)
        shell.layout = GridLayout(2, true)
        //label
        val lblInfo = Label(shell, SWT.NULL)
        lblInfo.text = "We have some data that have been saved automatically by the system, do you want to open it now?"
        val lbl = GridData(SWT.BEGINNING, SWT.BEGINNING, false, false)
        lbl.horizontalSpan = 2
        lblInfo.layoutData = lbl
        //list
        val data = GridData(SWT.CENTER, SWT.BEGINNING, false, false)
        val list = List(shell, SWT.BORDER or SWT.SINGLE or SWT.V_SCROLL)
        recentSaves.forEach(Consumer { curPath: Path ->
            //String fileName = curPath.getFileName().toString();
            val path = curPath.toString()
            if (Files.exists(curPath) && !Files.isDirectory(curPath)) {
                list.add(path)
            }
        })
        data.horizontalSpan = 2
        list.layoutData = data
        list.setBounds(0, 0, 100, 300)
        list.select(0)
        //btn
        val btn = GridData(SWT.CENTER, SWT.BEGINNING, false, false)
        val buttonOK = Button(shell, SWT.PUSH)
        buttonOK.text = localeHandler["Yes"]
        buttonOK.layoutData = btn
        val buttonCancel = Button(shell, SWT.PUSH)
        buttonCancel.text = localeHandler["No"]
        buttonOK.addListener(SWT.Selection) {
            val filePath = Paths.get(list.getItem(list.selectionIndex))
            // #6466: Move to $TEMP so WPManager doesn't delete and Archiver's have a source file
            val newPath: Path
            try {
                newPath = Files.createTempFile("recovered-", "-" + filePath.fileName.toString())
                Files.delete(newPath)
                Files.copy(filePath, newPath)
            } catch (ex: Exception) {
                throw RuntimeException("Failed to move $filePath to temp directory", ex)
            }
            WPManager.getInstance().addDocumentManager(newPath)
            shell.dispose()
        }
        buttonCancel.addListener(SWT.Selection) {
            selection = -1
            shell.dispose()
        }
        shell.addListener(SWT.Traverse) { event: Event -> if (event.detail == SWT.TRAVERSE_ESCAPE) event.doit = false }

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
