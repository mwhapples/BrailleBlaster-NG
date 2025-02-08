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
package org.brailleblaster.settings.ui

import org.brailleblaster.BBIni
import org.brailleblaster.utils.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.utd.UTDTranslationEngine
import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.TabFolder
import org.eclipse.swt.widgets.TabItem

class StatusBarSettingsTab (folder: TabFolder?) : SettingsUITab {

  init {
    //Boilerplate SWT, all the Settings tabs have this.
    val tab = TabItem(folder, SWT.NONE)
    tab.text = localeHandler["SettingsModule.statusBar"].replace("&", "")
    val parent = Composite(folder, SWT.NONE)
    parent.layout = GridLayout(1, true)
    tab.control = parent

    val statusBarLabels = arrayOf(
      STATUS_BAR_PRINT_PAGE,
      STATUS_BAR_BRAILLE_PAGE,
      STATUS_BAR_LINE,
      STATUS_BAR_CELL,
      STATUS_BAR_INDENTS,
      STATUS_BAR_ALIGNMENT,
      STATUS_BAR_STYLES,
    )

    val statusBarSettings = loadSettings()

    val statusBarGroup = Composite(parent, SWT.NONE)
    statusBarGroup.layout = RowLayout(SWT.VERTICAL)

    for (i in statusBarLabels.indices) {
      //Create a checkbox for each label.
      // These coordinate with booleans to pass into the WPView method that updates the status bar
      // And save the settings...
      val cb = Button(statusBarGroup, SWT.CHECK)
      cb.text = localeHandler[statusBarLabels[i]]
      cb.selection = statusBarSettings[i]

      EasySWT.addSelectionListener(cb) {
        statusBarSettings[i] = cb.selection
        //Save the changed item
        fileMgr.saveAsBoolean(statusBarLabels[i], statusBarSettings[i])
      }
    }
    //Settings Module has the Ok / Cancel buttons.
  }

  companion object {
    private val localeHandler = getDefault()
    //These get used as settings keywords and as locale strings
    private const val STATUS_BAR_PRINT_PAGE = "StatusBar.printPage"
    private const val STATUS_BAR_BRAILLE_PAGE = "StatusBar.braillePage"
    private const val STATUS_BAR_LINE = "StatusBar.line"
    private const val STATUS_BAR_CELL = "StatusBar.cell"
    private const val STATUS_BAR_INDENTS = "StatusBar.indents"
    private const val STATUS_BAR_ALIGNMENT = "StatusBar.alignment"
    private const val STATUS_BAR_STYLES = "StatusBar.styles"
    private val fileMgr = BBIni.propertyFileManager

    private fun loadSettings(): Array<Boolean>{
      return arrayOf(
        fileMgr.getPropertyAsBoolean(STATUS_BAR_PRINT_PAGE, true),
        fileMgr.getPropertyAsBoolean(STATUS_BAR_BRAILLE_PAGE, true),
        fileMgr.getPropertyAsBoolean(STATUS_BAR_LINE, true),
        fileMgr.getPropertyAsBoolean(STATUS_BAR_CELL, true),
        fileMgr.getPropertyAsBoolean(STATUS_BAR_INDENTS, true),
        fileMgr.getPropertyAsBoolean(STATUS_BAR_ALIGNMENT, true),
        fileMgr.getPropertyAsBoolean(STATUS_BAR_STYLES, true)
      )
    }
  }

  override fun validate(): String? {
    //Nothing to validate
    return null
  }

  override fun updateEngine(engine: UTDTranslationEngine): Boolean {
    //No need to refresh doc or do anything else...although it still does it for some reason?
    return false
  }

}