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
package org.brailleblaster.search

import org.brailleblaster.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.perspectives.braille.Manager
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell

class SearchNotices {
    fun endOfDoc(display: Shell?, message: String, man: Manager) {
        val endShell = Shell(display, SWT.DIALOG_TRIM or SWT.APPLICATION_MODAL)
        endShell.layout = GridLayout(1, true)
        endShell.setLocation(500, 250)
        endShell.text = SearchConstants.END_DOC_SHELL

        val label0 = Label(endShell, SWT.RESIZE)
        if (message == "beginning") label0.text = SearchConstants.END_DOC_SHELL
        else label0.text = SearchConstants.BEGIN_DOC

        val label = Label(endShell, SWT.RESIZE)
        label.text = SearchConstants.RESET_END_BEGIN

        val ok = Button(endShell, SWT.NONE)
        ok.text = SearchConstants.OK
        val endData = GridData(SWT.HORIZONTAL)
        ok.layoutData = endData
        endShell.defaultButton = ok
        ok.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                if (message == "beginning") {
                    man.resetSection(0)
                    man.text.view.caretOffset = 0
                } else {
                    man.resetSection(man.sectionList.size - 1)
                }
                endShell.close()
            }
        })
        val close = Button(endShell, SWT.NONE)
        close.text = SearchConstants.CLOSE
        close.layoutData = endData
        close.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                endShell.close()
            }
        })
        endShell.pack(true)
        endShell.open()
    }

    /**
     * All words were replaced dialog alert.
     */
    fun replaceAllMessage(display: Shell?, numberReplaceAlls: Int, numberTableWords: Int, findCombo: Combo?) {
        val replaceAllShell = Shell(display, SWT.DIALOG_TRIM or SWT.APPLICATION_MODAL)
        replaceAllShell.layout = GridLayout(1, true)
        replaceAllShell.setLocation(500, 250)

        val label0 = Label(replaceAllShell, SWT.RESIZE)
        label0.text = SearchConstants.REPLACE_ALL_COMPLETE

        if (numberReplaceAlls > 0) {
            val label = Label(replaceAllShell, SWT.RESIZE)
            label.text = localeHandler.format(SearchConstants.INSTANCES_REPLACED, numberReplaceAlls)
        }

        if (numberTableWords > 0) {
            val label2 = Label(replaceAllShell, SWT.RESIZE)
            label2.text = localeHandler.format(SearchConstants.REPLACE_ALL_TABLE_WARNING, numberTableWords)
        }

        val ok = Button(replaceAllShell, SWT.NONE)
        ok.text = SearchConstants.OK
        val replaceAllData = GridData(SWT.HORIZONTAL)
        ok.layoutData = replaceAllData
        replaceAllShell.defaultButton = ok
        ok.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                replaceAllShell.close()
                if (findCombo != null && !findCombo.isDisposed) {
                    findCombo.setFocus()
                }
            }
        })
        replaceAllShell.pack(true)
        replaceAllShell.open()
    }

    /**
     * BB couldn't find the word dialog alert
     */
    fun wordNotFoundMessage(display: Shell?, findCombo: Combo?) {
        val errorMessageShell = Shell(display, SWT.DIALOG_TRIM or SWT.APPLICATION_MODAL)
        errorMessageShell.layout = GridLayout(1, true)
        errorMessageShell.text = SearchConstants.WORD_NOT_FOUND
        errorMessageShell.setLocation(500, 250)

        val label = Label(errorMessageShell, SWT.RESIZE)
        label.text = SearchConstants.WORD_NOT_FOUND

        val ok = Button(errorMessageShell, SWT.NONE)
        ok.text = SearchConstants.OK
        val errorMessageData = GridData(SWT.HORIZONTAL)
        ok.layoutData = errorMessageData
        errorMessageShell.defaultButton = ok
        ok.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                errorMessageShell.close()
                if (findCombo != null && !findCombo.isDisposed) {
                    findCombo.setFocus()
                }
            }
        })
        errorMessageShell.pack(true)
        errorMessageShell.open()
    }

    fun invalidMessage(display: Shell?, message: String?) {
        val messageShell = Shell(display, SWT.DIALOG_TRIM or SWT.APPLICATION_MODAL)
        messageShell.layout = GridLayout(1, true)
        messageShell.setLocation(500, 250)
        messageShell.text = "Warning"

        val label = Label(messageShell, SWT.RESIZE)
        label.text = message

        val ok = Button(messageShell, SWT.NONE)
        ok.text = SearchConstants.OK
        val errorMessageData = GridData(SWT.HORIZONTAL)
        ok.layoutData = errorMessageData
        messageShell.defaultButton = ok
        ok.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                messageShell.close()
            }
        })
        messageShell.pack(true)
        messageShell.open()
    }

    companion object {
        private val localeHandler = getDefault()
    }
}
