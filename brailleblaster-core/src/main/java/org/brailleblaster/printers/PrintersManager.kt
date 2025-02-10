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
package org.brailleblaster.printers

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.graphics.*
import org.eclipse.swt.printing.PrintDialog
import org.eclipse.swt.printing.Printer
import org.eclipse.swt.widgets.Shell
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PrintersManager(shell: Shell?, var text: StyledText) {
    var font: Font? = null
    var backgroundColor: Color? = null
    var gc: GC? = null
    var printerFontData: Array<FontData>? = null
    var printerForeground: RGB? = null
    var printerBackground: RGB? = null
    var lineHeight = 0
    var x = 0
    var y = 0
    var index = 0
    var end = 0
    var tabs: String? = null
    var dialog: PrintDialog = PrintDialog(shell, SWT.SHELL_TRIM)
    fun beginPrintJob() {
        val data = dialog.open() ?: return
        if (data.printToFile) {
            data.fileName = "print.out"
        }
        printerFontData = text.font.fontData
        printerForeground = text.foreground.rgb
        printerBackground = text.background.rgb
        val printer = Printer(data)
        val thread = text.print(printer)
        thread.run()
        printer.dispose()
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(PrintersManager::class.java)
    }
}