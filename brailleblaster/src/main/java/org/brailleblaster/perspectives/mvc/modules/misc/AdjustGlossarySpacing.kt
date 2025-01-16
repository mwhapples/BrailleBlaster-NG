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

import nu.xom.Attribute
import nu.xom.Element
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.mvc.events.ModifyEvent
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.MessageBox
import org.eclipse.swt.widgets.Shell

object AdjustGlossarySpacing {
    //Pop up a window that asks for the number of spaces that should be added between the term and definition
    var window: Shell? = null
    @JvmStatic
	fun open(manager: Manager, parent: Shell?) {
        if (window == null || window!!.isDisposed) {
            window = Shell(parent)
            if (processShell(manager)) {
                window!!.open()
            } else {
                window!!.dispose()
            }
        } else {
            window!!.forceActive()
        }
    }

    private fun processShell(manager: Manager): Boolean {
        window!!.setSize(200, 100)
        window!!.text = "Adjust Term/Definition Spacing"
        window!!.layout = FillLayout(SWT.VERTICAL)
        val radioOne = Button(window, SWT.RADIO)
        radioOne.text = "One"
        radioOne.isEnabled = true
        val radioTwo = Button(window, SWT.RADIO)
        radioTwo.text = "Two"
        val okButton = Button(window, SWT.PUSH)
        okButton.text = "OK"
        val section = manager.simpleManager.currentSelection.start.node
        if (section is Element) {
            var `val` = "1"
            if (radioTwo.selection) {
                `val` = "2"
            }
            section.addAttribute(Attribute("spacing", `val`))
        } else {
            val message = MessageBox(window!!.parent as Shell)
            message.message = "Please select a glossary element."
            message.open()
            return false
        }
        okButton.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                if (section is Element) {
                    var `val` = "1"
                    if (radioTwo.selection) {
                        `val` = "2"
                    }
                    section.addAttribute(Attribute("spacing", `val`))
                    manager.simpleManager.dispatchEvent(ModifyEvent(Sender.NO_SENDER, true, section))
                } else {
                    val message = MessageBox(window!!.parent as Shell)
                    message.message = "Please select an element."
                    message.open()
                }
                window!!.close()
            }
        })
        return true
    }
}