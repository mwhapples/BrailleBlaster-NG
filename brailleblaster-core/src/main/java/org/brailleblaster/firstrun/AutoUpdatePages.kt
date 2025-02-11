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
package org.brailleblaster.firstrun

import org.brailleblaster.BBIni
import org.brailleblaster.AUTO_UPDATE_SETTING
import org.brailleblaster.utils.localization.LocaleHandler
import org.brailleblaster.utils.PropertyFileManager
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.widgets.CompositeFactory
import org.eclipse.jface.widgets.LabelFactory
import org.eclipse.jface.wizard.WizardPage
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Composite

private const val PAGE_TITLE = "AutoUpdatesPage.title"
private const val PAGE_DESCRIPTION = "AutoUpdatesPage.description"
private const val AUTO_UPDATE_QUESTION = "AutoUpdatesPage.autoUpdate"

class AutoUpdatePage(private val userSettings: PropertyFileManager = BBIni.propertyFileManager) : WizardPage(LocaleHandler.getDefault()[PAGE_TITLE]), ActionOnFinishWizardPage {
    private lateinit var autoUpdatesCombo: ComboViewer
    private var autoUpdates: Boolean = false
    override fun createControl(parent: Composite?) {
        control = CompositeFactory.newComposite(SWT.NONE).layout(GridLayout(2, false)).layoutData(GridDataFactory.fillDefaults().grab(true, false).create()).create(parent).also {
            LabelFactory.newLabel(SWT.WRAP).text(LocaleHandler.getDefault()[AUTO_UPDATE_QUESTION]).layoutData(GridDataFactory.fillDefaults().grab(true, false).create()).create(it)
            autoUpdatesCombo = ComboViewer(it).apply {
                contentProvider = ArrayContentProvider.getInstance()
                labelProvider = object : LabelProvider() {
                    override fun getText(element: Any?): String = (element as UserOption).displayName
                }
                input = listOf(NoSelection, * YesNoOption.entries.toTypedArray())
                selection = StructuredSelection(NoSelection)
                addSelectionChangedListener { event ->
                    autoUpdates = event.structuredSelection.firstElement == YesNoOption.YES
                    container.updateButtons()
                }
            }
        }
    }

    override fun isPageComplete(): Boolean {
        return super.isPageComplete() || autoUpdatesCombo.structuredSelection.firstElement !is NoSelection
    }

    override fun performFinish(): Boolean {
        userSettings.saveAsBoolean(AUTO_UPDATE_SETTING, autoUpdates)
        return true
    }
    init {
        isPageComplete = false
        title = LocaleHandler.getDefault()[PAGE_TITLE]
        description = LocaleHandler.getDefault()[PAGE_DESCRIPTION]
    }
}