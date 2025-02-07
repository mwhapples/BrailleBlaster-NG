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

import org.brailleblaster.util.swt.EasySWT.getWidthOfText
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label

/**
 * Provides an easy way to create or apply GridData to a control. Ending the builder chain with
 * build() will return a GridData, and ending it with applyTo(Control control) will build and
 * apply the data to a control. Note: When applying grid data to a label or button, the applyTo()
 * method will either set the width to be long enough to display the text, or if the width hint
 * has been set it will ensure the width is long enough. The bulid() method can be used to get
 * around this behavior.
 */
class GridDataBuilder {
    private var widthHint: Int? = null
    private var heightHint: Int? = null
    private var columns = 1
    private var horizontalAlignment: Int? = null
    private var verticalAlignment: Int? = null
    private var verticalSpan: Int? = null
    private var grabHorizontalSpace = false
    private var grabVerticalSpace = false

    fun setHint(width: Int?, height: Int?): GridDataBuilder {
        this.widthHint = width
        this.heightHint = height
        return this
    }

    fun setColumns(columns: Int): GridDataBuilder {
        this.columns = columns
        return this
    }

    fun setAlign(horizontalAlignment: Int?, verticalAlignment: Int?): GridDataBuilder {
        this.horizontalAlignment = horizontalAlignment
        this.verticalAlignment = verticalAlignment
        return this
    }

    fun setGrabSpace(horizontally: Boolean, vertically: Boolean): GridDataBuilder {
        grabHorizontalSpace = horizontally
        grabVerticalSpace = vertically
        return this
    }

    fun verticalSpan(rows: Int): GridDataBuilder {
        verticalSpan = rows
        return this
    }

    fun build(): GridData {
        if (horizontalAlignment == null) horizontalAlignment = -1
        if (verticalAlignment == null) verticalAlignment = -1
        val newData =
            GridData(
                horizontalAlignment!!,
                verticalAlignment!!,
                grabHorizontalSpace,
                grabVerticalSpace,
                columns,
                1
            )
        widthHint?.let { newData.widthHint = it }
        heightHint?.let { newData.heightHint = it }
        verticalSpan?.let {
            newData.verticalSpan = it
        }
        return newData
    }

    fun applyTo(control: Control): GridData {
        if (control is Button) {
            val textWidth = getWidthOfText(control.text) + EasySWT.TEXT_PADDING
            if (widthHint == null || widthHint!! < textWidth) {
                widthHint = textWidth
            }
        } else if (control is Label) {
            val textWidth = getWidthOfText(control.text)
            if (widthHint == null || widthHint!! < textWidth) {
                widthHint = textWidth
            }
        }
        val data = build()
        control.layoutData = data
        return data
    }
}
