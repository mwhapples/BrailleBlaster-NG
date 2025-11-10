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
package org.brailleblaster.util

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.elements.TextMapElement
import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.widgets.Shell
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Useful utilities for creating the SWT UI
 */
object FormUIUtils {

    /**
     * If the value is different from the getter, update the object with the setter.
     */
	@JvmStatic
	fun <V> updateObject(getter: Supplier<V>, setter: Consumer<V>, value: V?, updateFlag: Boolean): Boolean {
        if (value == null) {
            throw RuntimeException("Value is null.")
        }
        val getterValue = getter.get()
        // Value didn't need updating but still need to pass on flag
        return if (getterValue == null || getterValue != value) {
            setter.accept(value)
            true
        } else updateFlag
    }

    /**
     * Make dialog that has application modal, meaning the main shell can NOT be
     * clicked on. Is resizable and has dialog trim
     */
    fun makeDialog(manager: Manager): Shell {
        return EasySWT.makeDialog(manager.wpManager.shell)
    }

    /**
     * Make dialog that doesn't have any modality, meaning the main shell can be
     * clicked on. Is resizable and has dialog trim
     */
    fun makeDialogFloating(manager: Manager): Shell {
        return EasySWT.makeDialogFloating(manager.wpManager.shell)
    }

    fun getCaretAtTextNodeOffset(view: StyledText, tme: TextMapElement, offset: Int, manager: Manager): Int {
        var offset = offset
        var text = view.getTextRange(tme.getStart(manager.mapList), offset)
        var lineBreakIndex = text.indexOf(LINE_BREAK)
        while (lineBreakIndex >= 0) {
            offset += LINE_BREAK.length
            text = view.getTextRange(tme.getStart(manager.mapList), offset)
            lineBreakIndex = text.indexOf(LINE_BREAK, lineBreakIndex + 1)
        }
        return offset + tme.getStart(manager.mapList)
    }

    fun setCaretAtTextNodeOffset(view: StyledText, tme: TextMapElement, offset: Int, manager: Manager) {
        EasySWT.setCaretAfterLineBreaks(view, getCaretAtTextNodeOffset(view, tme, offset, manager))
    }
}
