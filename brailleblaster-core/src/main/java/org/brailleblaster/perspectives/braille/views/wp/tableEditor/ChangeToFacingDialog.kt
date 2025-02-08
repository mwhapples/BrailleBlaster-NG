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
package org.brailleblaster.perspectives.braille.views.wp.tableEditor

import org.brailleblaster.utils.swt.EasyListeners
import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

/**
 * Dialog used to switch from any table type to Simple Facing
 */
internal class ChangeToFacingDialog {
    /**
     * @param advanced Enables dimension controls for "Change Dimensions" button
     * @return [0] = Page divide (-1 if Cancel was pressed)
     * [1] = Rows
     * [2] = Columns
     */
    fun open(parent: Shell?, state: ITable, advanced: Boolean): IntArray {
        val result = Result()
        val totalCols = state.displayedCols
        val totalRows = state.displayedRows
        result.row = totalRows
        result.col = totalCols
        val leftCols = if (totalCols % 2 == 0) totalCols / 2 else (totalCols / 2) + 1

        val shell = Shell(parent, SWT.DIALOG_TRIM or SWT.APPLICATION_MODAL)
        shell.layout = GridLayout(1, false)
        val container = EasySWT.makeComposite(shell, 1)
        val message = EasySWT.makeLabel(container, "Choose how to divide the columns", 1)
        EasySWT.buildGridData().setAlign(SWT.CENTER, SWT.CENTER).applyTo(message)

        val slider = createSlider(container, totalCols, leftCols)

        if (advanced) {
            val dimComp = EasySWT.makeComposite(container, 4)
            EasySWT.makeLabel(dimComp, "Rows:", 1)
            val rowText = EasySWT.makeText(dimComp, 1)
            rowText.text = totalRows.toString()
            rowText.textLimit = 2
            EasyListeners.modify(rowText) { e: ModifyEvent -> result.row = (e.widget as Text).text.toInt() }
            EasySWT.buildGridData().setHint(EasySWT.getWidthOfText("100"), SWT.DEFAULT).applyTo(rowText)
            EasySWT.makeLabel(dimComp, "Columns:", 1)
            val colText = EasySWT.makeText(dimComp, 1)
            colText.text = totalCols.toString()
            colText.textLimit = 2
            EasySWT.buildGridData().setHint(EasySWT.getWidthOfText("100"), SWT.DEFAULT).applyTo(colText)
            EasyListeners.verifyNumbersOnly(rowText)
            EasyListeners.verifyNumbersOnly(colText)
            colText.addModifyListener(changeDimensions(rowText, colText, slider, result))
        }

        val buttonPanel = EasySWT.makeComposite(container, 2)
        EasySWT.makePushButton(buttonPanel, TableEditor.OK_BUTTON, 1) {
            if (result.row < 1) {
                EasySWT.makeEasyOkDialog("Error", "A table must have at least 1 row", shell)
                return@makePushButton
            }
            if (result.col <= 1) {
                EasySWT.makeEasyOkDialog("Error", "A facing table must have at least two columns", shell)
                return@makePushButton
            }
            if (result.row * result.col > TableEditor.MAX_CELLS) {
                EasySWT.makeEasyOkDialog("Error", "Table cannot exceed " + TableEditor.MAX_CELLS + " cells", shell)
                return@makePushButton
            }
            result.selection = slider.widget.selection
            shell.close()
        }
        EasySWT.makePushButton(buttonPanel, TableEditor.CANCEL_BUTTON, 1) {
            result.selection = -1
            shell.close()
        }

        shell.pack()
        shell.open()
        while (!shell.isDisposed) {
            Display.getCurrent().readAndDispatch()
        }
        if (advanced) return intArrayOf(result.selection, result.row, result.col)
        return intArrayOf(result.selection)
    }

    /**
     * When the number of columns is changed, change the slider to reflect that
     */
    private fun changeDimensions(rowText: Text, colText: Text, slider: Slider, result: Result): ModifyListener {
        return ModifyListener {
            if (rowText.text.isEmpty() || colText.text.isEmpty()) return@ModifyListener
            val totalCols = colText.text.toInt()
            val leftCols = if (totalCols % 2 == 0) totalCols / 2 else (totalCols / 2) + 1
            result.col = totalCols
            when (totalCols) {
                2 -> {
                    slider.setLeft(1)
                    slider.setRight(1)
                    slider.widget.selection = 1
                    slider.widget.isEnabled = false
                }
                1 -> {
                    slider.widget.isEnabled = false
                }
                else -> {
                    slider.widget.isEnabled = true
                    slider.widget.minimum = 1 //SWT ignores setMinimum when minimum = maximum
                    slider.widget.maximum = totalCols - 1
                    slider.widget.selection = leftCols
                    moveSlider(slider)
                }
            }
        }
    }

    private fun createSlider(container: Composite?, totalCols: Int, leftCols: Int): Slider {
        val sliderCont = EasySWT.makeComposite(container, 3)
        val leftLabel = EasySWT.makeLabel(sliderCont, "", 1)
        EasySWT.buildGridData().setAlign(SWT.RIGHT, SWT.CENTER)
            .setHint(EasySWT.getWidthOfText("Left page: 000"), SWT.DEFAULT).applyTo(leftLabel)

        val scale = Scale(sliderCont, SWT.HORIZONTAL)
        scale.maximum = totalCols - 1
        scale.minimum = 1

        val rightLabel = EasySWT.makeLabel(sliderCont, "", 1)
        EasySWT.buildGridData().setAlign(SWT.RIGHT, SWT.CENTER)
            .setHint(EasySWT.getWidthOfText("Left page: 000"), SWT.DEFAULT).applyTo(rightLabel)
        val slider = Slider(scale, leftLabel, rightLabel)
        slider.setLeft(leftCols)
        slider.setRight(totalCols - leftCols)
        slider.widget.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                moveSlider(slider)
            }
        })
        slider.widget.selection = leftCols
        if (totalCols == 2) {
            slider.widget.isEnabled = false
        }
        return slider
    }

    fun moveSlider(slider: Slider) {
        val curSelection = slider.widget.selection
        slider.setLeft(curSelection)
        slider.setRight((slider.widget.maximum + 1) - curSelection)
    }

    data class Result(
        var selection: Int = -1,
        var row: Int = -1,
        var col: Int = -1
    )

    @JvmRecord
    data class Slider(val widget: Scale, val left: Label, val right: Label) {
        fun setLeft(num: Int) {
            left.text = "Left page: $num"
        }

        fun setRight(num: Int) {
            right.text = "Right page: $num"
        }
    }
}
