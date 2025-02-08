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

import org.brailleblaster.document.BBDocument
import org.brailleblaster.utils.localization.LocaleHandler
import org.brailleblaster.settings.PredominantQuoteSetting
import org.brailleblaster.utd.UTDTranslationEngine
import org.brailleblaster.util.swt.AccessibilityUtils
import org.brailleblaster.util.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.*

class FormatSettingsTab(folder: TabFolder, private val bbdoc: BBDocument) : SettingsUITab {
    var predominantQuote: PredominantQuoteSetting = bbdoc.settingsManager.predominantQuoteSetting
    override fun validate(): String? {
        // Should always be valid
        return null
    }

    override fun updateEngine(engine: UTDTranslationEngine): Boolean {
        return bbdoc.settingsManager.predominantQuoteSetting != predominantQuote
    }

    companion object {
        const val I18N_TITLE_KEY = "formatSettingsTabTitle"
    }

    init {
        val tab = TabItem(folder, SWT.NONE)
        tab.text = LocaleHandler.getDefault()[I18N_TITLE_KEY]
        val parent = Composite(folder, SWT.NONE)
        parent.layout = GridLayout(2, true)
        tab.control = parent
        val quoteSettingLabel = Label(parent, SWT.NONE)
        quoteSettingLabel.text = LocaleHandler.getDefault()["predominantQuoteSettingLabel"]
        val quoteSettingComposite = Composite(parent, SWT.NONE)
        quoteSettingComposite.layout = RowLayout(SWT.VERTICAL)
        for (quoteSetting in PredominantQuoteSetting.entries) {
            val btn = Button(quoteSettingComposite, SWT.RADIO)
            btn.text = LocaleHandler.getDefault()[quoteSetting.i8nKey]
            AccessibilityUtils.prependName(btn, quoteSettingLabel.text)
            if (quoteSetting == predominantQuote) {
                btn.selection = true
            }
            EasySWT.addSelectionListener(btn) { predominantQuote = quoteSetting }
        }
    }
}