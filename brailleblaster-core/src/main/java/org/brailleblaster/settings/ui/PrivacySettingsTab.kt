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
import org.brailleblaster.utils.localization.LocaleHandler
import org.brailleblaster.perspectives.mvc.modules.misc.ExceptionReportingModule
import org.brailleblaster.usage.USAGE_TRACKING_SETTING
import org.brailleblaster.utd.UTDTranslationEngine
import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

class PrivacySettingsTab(shell: Shell?) : SettingsUITab {
    private var usageSharingCheckBox: Button
    private val reportingCombo: Combo
    private val recoveryCombo: Combo
    private val uploadCombo: Combo
    override fun validate(): String? {
        // nothing to validate in combos
        return null
    }

    override fun updateEngine(engine: UTDTranslationEngine): Boolean {
        BBIni.propertyFileManager.saveAsBoolean(USAGE_TRACKING_SETTING, usageSharingCheckBox.selection)
        val selectedReporting = reportingCombo.selectionIndex
        ExceptionReportingModule.exceptionReportingLevel = ExceptionReportingModule.ExceptionReportingLevel.entries[selectedReporting]
        val selectedRecovery = recoveryCombo.selectionIndex
        ExceptionReportingModule.exceptionRecoveryLevel = ExceptionReportingModule.ExceptionRecoveryLevel.entries[selectedRecovery]
        val selectedUpload = uploadCombo.selectionIndex
        if (selectedUpload != -1) {
            ExceptionReportingModule.setAutoUploadEnabled(selectedUpload == 0)
        }

        // document doesn't need to refresh
        return false
    }

    init {
        val lh = LocaleHandler.getDefault()
        val parent = Composite(shell, 0)
        parent.layout = GridLayout(1, true)
        val usageGroup = Group(parent, SWT.NONE)
        usageGroup.layout = GridLayout(1, true)
        usageGroup.text = lh["PrivacySettingsTab.usageGroupText"]
        EasySWT.setGridDataGroup(usageGroup)
        usageSharingCheckBox = EasySWT.makeButton(parent, SWT.CHECK or SWT.WRAP).text(lh["PrivacySettingsTab.usageDataSharing"]).get()
        val standardsGroup = Group(parent, SWT.NONE)
        standardsGroup.layout = GridLayout(2, true)
        standardsGroup.text = lh["ExceptionSettingsTab.onError"]
        EasySWT.setGridDataGroup(standardsGroup)
        EasySWT.newLabel(standardsGroup, lh["ExceptionSettingsTab.notifications"])
        reportingCombo = Combo(standardsGroup, SWT.DROP_DOWN or SWT.READ_ONLY)
        EasySWT.setGridData(reportingCombo)
        // follow order of ExceptionReportingLevel
        reportingCombo.add(lh["ExceptionSettingsTab.displayErrorInformation"])
        reportingCombo.add(lh["ExceptionSettingsTab.notifyErrorOccurred"])
        reportingCombo.add(lh["ExceptionSettingsTab.displayInStatusBar"])
        // HIDE_ALL is unused
        EasySWT.newLabel(standardsGroup, lh["ExceptionSettingsTab.errorRecovery"])
        recoveryCombo = Combo(standardsGroup, SWT.DROP_DOWN or SWT.READ_ONLY)
        EasySWT.setGridData(recoveryCombo)
        // follow order of ExceptionRecoveryLevel
        recoveryCombo.add(lh["ExceptionSettingsTab.automaticallyRecover"])
        recoveryCombo.add(lh["ExceptionSettingsTab.doNotRecover"])
        EasySWT.newLabel(standardsGroup, lh["ExceptionSettingsTab.reportErrors"])
        uploadCombo = Combo(standardsGroup, SWT.DROP_DOWN or SWT.READ_ONLY)
        EasySWT.setGridData(uploadCombo)
        // follow order of ExceptionRecoveryLevel
        uploadCombo.add(lh["Common.yes"])
        uploadCombo.add(lh["Common.no"])

        // ------- data --------
        usageSharingCheckBox.selection = BBIni.propertyFileManager.getPropertyAsBoolean(USAGE_TRACKING_SETTING, false)
        reportingCombo.select(ExceptionReportingModule.exceptionReportingLevel.ordinal)
        recoveryCombo.select(ExceptionReportingModule.exceptionRecoveryLevel.ordinal)
        val reportCurrent = ExceptionReportingModule.isAutoUploadEnabledOrNull()
        if (reportCurrent != null) {
            uploadCombo.select(if (reportCurrent) 0 else 1)
        }
    }
}