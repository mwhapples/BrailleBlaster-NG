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
package org.brailleblaster.math.spatial

import org.brailleblaster.math.spatial.MatrixConstants.BracketType
import org.brailleblaster.math.spatial.MatrixConstants.Wide
import org.brailleblaster.math.spatial.MatrixSettings.Companion.enumifyWide
import org.brailleblaster.math.spatial.SpatialMathEnum.Translation
import org.brailleblaster.perspectives.mvc.modules.views.DebugModule
import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.*

class MatrixWidget : ISpatialMathWidget {
  var entries = ArrayList<ArrayList<StyledText>>()
  var matrix: Matrix? = null
  override fun fillDebug(t: ISpatialMathContainer) {
    matrix = t as Matrix
    defaultDebug()
  }

  private fun makeRowsAndColumns(g3: Group, matrix: Matrix?) {
    for (i in 0 until matrix!!.settings.rows) {
      val g = EasySWT.makeGroup(g3, SWT.NONE, matrix.settings.cols, false)
      val array = ArrayList<StyledText>()
      entries.add(array)
      for (j in 0 until matrix.settings.cols) {
        //TODO: Add keylisteners for the st items so that they function like the Table Editor navigation does.
        //Ctrl-tab and Ctrl-Shift-tab already work, but arrow keys would be nice.
        val st = StyledText(g, SWT.NONE or SWT.WRAP or SWT.H_SCROLL or SWT.V_SCROLL)
        st.data = GridData(SWT.FILL, SWT.FILL, true, true)
        st.text = " ".repeat(50) + "\n\n"
         // set size gets ignored before open, this makes sure the wrap isn't set to very few chars
        entries[i].add(st)
      }
    }
  }

  private fun defaultDebug() {
    matrix!!.settings.model = entries.mapIndexed { i, r ->
      r.indices.map { j -> MatrixCell((i * matrix!!.settings.cols + j + 1).toString(), false) }
        .toMutableList()
    }.toMutableList()
  }

  override fun onOpen() {
    loadMathDataIntoTextBoxes()
  }

  private fun loadMathDataIntoTextBoxes() {
    matrix!!.settings.model.zip(matrix!!.widget.entries).flatMap { (s, e) -> s.zip(e) }
      .forEach { (s, e) -> e.text = if (s.isEllipsis) "" else s.text }
  }

  override fun getWidget(parent: Composite, container: ISpatialMathContainer): Composite {
    entries.clear()
    matrix = container as Matrix
    val g3 = EasySWT.makeGroup(parent, SWT.NONE, 1, false)
    makeRowsAndColumns(g3, matrix)
    return parent
  }

  override fun extractText() {
    matrix!!.settings.model.clear()
    val array: MutableList<MutableList<MatrixCell>> = ArrayList()
    for (i in entries.indices) {
      val row = ArrayList<MatrixCell>()
      array.add(row)
      for (j in entries[i].indices) {
        array[i].add(MatrixCell(entries[i][j].text, false))
      }
    }
    matrix!!.settings.model = array
  }

  override fun addMenuItems(shell: Shell, menu: Menu, settingsMenu: Menu): Menu {
    addSettingsMenu(shell, settingsMenu)
    addGroupingsMenu(shell, menu)
    addOverflowMenu(shell, menu)
    addRowMenu(shell, menu)
    addColumnMenu(shell, menu)
    if (DebugModule.enabled) {
      addDebugMenu(shell, menu)
    }
    return menu
  }

  private fun addDebugMenu(shell: Shell, menu: Menu) {
    val cascadeMenu = MenuItem(menu, SWT.CASCADE)
    cascadeMenu.text = "Debug Matrix"
    val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
    cascadeMenu.menu = dropDownMenu
    val numericPassage = MenuItem(dropDownMenu, SWT.NONE)
    numericPassage.text = "Fill Overflow"
    numericPassage.addListener(SWT.Selection) {
      val testLength = 12
      for (i in entries.indices) {
        for (j in entries[i].indices) {
          entries[i][j].text =
            (i * matrix!!.settings.cols + j).toString().repeat(testLength)
        }
      }
    }
    val ellipsesButton = MenuItem(dropDownMenu, SWT.NONE)
    ellipsesButton.text = "Fill Skinny"
    ellipsesButton.addListener(SWT.Selection) {
      for (i in entries.indices) {
        for (j in entries[i].indices) {
          entries[i][j].text = (i * matrix!!.settings.cols + j).toString()
        }
      }
    }
  }

  private fun addColumnMenu(shell: Shell, menu: Menu) {
    val cascadeMenu = MenuItem(menu, SWT.CASCADE)
    cascadeMenu.text = MatrixConstants.COL_LABEL
    val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
    cascadeMenu.menu = dropDownMenu
    for (s in AVAIL_ROWS_COLS) {
      val blankBlock = MenuItem(dropDownMenu, SWT.RADIO)
      blankBlock.text = s
      blankBlock.selection = matrix!!.settings.cols == s.toInt()
      blankBlock.addListener(SWT.Selection) {
        if (!blankBlock.selection) {
          return@addListener
        }
        extractText()
        matrix!!.settings.cols = s.toInt()
        SpatialMathDispatcher.dispatch()
      }
    }
  }

  private fun addRowMenu(shell: Shell, menu: Menu) {
    val cascadeMenu = MenuItem(menu, SWT.CASCADE)
    cascadeMenu.text = MatrixConstants.ROW_LABEL
    val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
    cascadeMenu.menu = dropDownMenu
    for (s in AVAIL_ROWS_COLS) {
      val blankBlock = MenuItem(dropDownMenu, SWT.RADIO)
      blankBlock.text = s
      blankBlock.selection = matrix!!.settings.rows == s.toInt()
      blankBlock.addListener(SWT.Selection) {
        if (!blankBlock.selection) {
          return@addListener
        }
        extractText()
        matrix!!.settings.rows = s.toInt()
        SpatialMathDispatcher.dispatch()
      }
    }
  }

  fun addSettingsMenu(shell: Shell?, settingsMenu: Menu?) {
    val cascadeMenu = MenuItem(settingsMenu, SWT.CASCADE)
    cascadeMenu.text = MatrixConstants.MATRIX_TRANSLATION
    val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
    cascadeMenu.menu = dropDownMenu
    for (bt in Translation.entries) {
      val blankBlock = MenuItem(dropDownMenu, SWT.RADIO)
      blankBlock.text = bt.prettyName
      blankBlock.selection = bt == matrix!!.settings.translation
      blankBlock.selection = bt == matrix!!.settings.translation
      blankBlock.addListener(SWT.Selection) {
        matrix!!.settings.translation = bt
        extractText()
        SpatialMathDispatcher.dispatch()
      }
    }
    val ellipsesButton = MenuItem(settingsMenu, SWT.CHECK)
    ellipsesButton.text = MatrixConstants.ELLIPSIS_LABEL
    ellipsesButton.selection = matrix!!.settings.isAddEllipses
    ellipsesButton.addListener(SWT.Selection) {
      matrix!!.settings.isAddEllipses = !matrix!!.settings.isAddEllipses
      ellipsesButton.selection = matrix!!.settings.isAddEllipses
    }
  }

  fun addGroupingsMenu(shell: Shell?, menu: Menu?) {
    val cascadeMenu = MenuItem(menu, SWT.CASCADE)
    cascadeMenu.text = MatrixConstants.BRACKET_TYPE_LABEL
    val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
    cascadeMenu.menu = dropDownMenu
    for (bt in BracketType.entries) {
      val blankBlock = MenuItem(dropDownMenu, SWT.RADIO)
      blankBlock.text = bt.label
      blankBlock.selection = bt == matrix!!.settings.bracketType
      blankBlock.addListener(SWT.Selection) {
        matrix!!.settings.bracketType = bt
        extractText()
        SpatialMathDispatcher.dispatch()
      }
    }
  }

  fun addOverflowMenu(shell: Shell?, menu: Menu?) {
    val cascadeMenu = MenuItem(menu, SWT.CASCADE)
    cascadeMenu.text = MatrixConstants.WIDE_LABEL
    val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
    cascadeMenu.menu = dropDownMenu
    val blankBlock = MenuItem(dropDownMenu, SWT.RADIO)
    blankBlock.text = MatrixConstants.BLOCK_BLANK_LABEL
    blankBlock.selection = Wide.BLOCK_BLANK == matrix!!.settings.wideType
    blankBlock.addListener(SWT.Selection) {
      matrix!!.settings.wideType = enumifyWide(blankBlock.text)
      extractText()
      SpatialMathDispatcher.dispatch()
    }
    val indentColumn = MenuItem(dropDownMenu, SWT.RADIO)
    indentColumn.text = MatrixConstants.INDENT_COLUMN_LABEL
    indentColumn.selection = Wide.INDENT_COLUMN == matrix!!.settings.wideType
    indentColumn.addListener(SWT.Selection) {
      matrix!!.settings.wideType = enumifyWide(indentColumn.text)
      extractText()
      SpatialMathDispatcher.dispatch()
    }
  }

  companion object {
    private val AVAIL_ROWS_COLS = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
  }
}
