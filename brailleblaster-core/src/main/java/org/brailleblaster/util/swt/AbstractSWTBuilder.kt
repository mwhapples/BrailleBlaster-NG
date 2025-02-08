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
package org.brailleblaster.util.swt

import org.brailleblaster.util.swt.AccessibilityUtils.setName
import org.brailleblaster.util.Utils.addSwtBotKey
import org.brailleblaster.util.swt.EasySWT.calcAverageCharHeight
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.RowData
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control

open class AbstractSWTBuilder<W : Control, B : AbstractSWTBuilder<W, B>>(@JvmField protected val widget: W) {
    @Suppress("UNCHECKED_CAST")
    private val thisAsB: B
        get() = this as B
    fun swtBotId(swtBotId: String?): B {
        addSwtBotKey(widget, swtBotId)
        return thisAsB
    }

    fun gridDataWidth(width: Int): B {
        val gridData = gridData()
        gridData.widthHint = width
        return thisAsB
    }

    fun gridDataHeightFromGC(parent: Composite?, multiplier: Int): B {
        val gridData = gridData()
        gridData.heightHint = calcAverageCharHeight(parent) * multiplier
        return thisAsB
    }

    fun gridDataHorizontalFill(): B {
        val gridData = gridData()
        gridData.grabExcessHorizontalSpace = true
        gridData.horizontalAlignment = SWT.FILL
        return thisAsB
    }

    fun gridDataHorizontalSpan(span: Int): B {
        val gridData = gridData()
        gridData.horizontalSpan = span
        return thisAsB
    }

    fun gridData(): GridData {
        val gd: GridData
        if (widget.layoutData == null || widget.layoutData !is GridData) {
            gd = GridData()
            widget.layoutData = gd
        } else {
            gd = widget.layoutData as GridData
        }
        return gd
    }

    fun rowDataWidth(width: Int): B {
        val rd = rowData()
        rd.width = width
        return thisAsB
    }

    fun rowData(): RowData {
        val rd: RowData
        if (widget.layoutData == null || widget.layoutData !is GridData) {
            rd = RowData()
            widget.layoutData = rd
        } else {
            rd = widget.layoutData as RowData
        }
        return rd
    }

    fun setAccessibleName(name: String?): B {
        setName(widget, name)
        return thisAsB
    }

    fun get(): W {
        return widget
    }
}
