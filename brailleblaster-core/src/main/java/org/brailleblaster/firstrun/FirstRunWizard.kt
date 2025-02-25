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
import org.brailleblaster.usage.SimpleUsageManager
import org.brailleblaster.usage.USAGE_TRACKING_SETTING
import org.brailleblaster.usage.UsageManager
import org.brailleblaster.utils.PropertyFileManager
import org.brailleblaster.utils.localization.LocaleHandler
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.wizard.IWizardPage
import org.eclipse.jface.wizard.Wizard
import org.eclipse.jface.wizard.WizardDialog
import org.eclipse.swt.widgets.Shell
import java.util.function.Supplier

interface OptionalWizardPage : IWizardPage {
    val isEnabled: Boolean
}

interface ActionOnFinishWizardPage : IWizardPage {
    fun performFinish(): Boolean
}

class UserOptionLabelProvider : LabelProvider() {
    override fun getText(element: Any?): String = (element as UserOption).displayName
}

interface UserOption {
    val stringKey: String
    val displayName: String
        get() = LocaleHandler.getDefault()[stringKey]
}

object NoSelection : UserOption {
    override val stringKey: String = "FirstRunWizard.noSelection"
}

class OtherSelection(override val stringKey: String = "FirstRunWizard.otherSelection") : UserOption

enum class YesNoOption(override val stringKey: String) : UserOption {
    YES("Common.yes"),
    NO("Common.no")
}

enum class JobTitle(override val stringKey: String) : UserOption {
    TVI("JobTitle.tvi"),
    TGE("JobTitle.tge"),
    OM_INSTRUCTOR("JobTitle.om_instructor"),
    AT_SPECIALIST("JobTitle.at_specialist"),
    BRAILLE_TRANSCRIBER("JobTitle.braille_transcriber"),
    REHAB_PROFESSIONAL("JobTitle.rehab_professional"),
    STUDENT("JobTitle.student"),
    EOT("JobTitle.eot"),
    PARENT("JobTitle.parent")
}

enum class UserLocation(override val stringKey: String) : UserOption {
    NW_US("UserLocation.nw_us"),
    NE_US("UserLocation.ne_us"),
    SE_US("UserLocation.se_us"),
    SW_US("UserLocation.sw_us"),
    MW_US("UserLocation.mw_us"),
    AH_US("UserLocation.ah_us"),
    US_TERRITORY("UserLocation.us_territory")
}

class FirstRunWizard(private val pageSuppliers: List<Supplier<IWizardPage>>) : Wizard() {
    init {
        windowTitle = LocaleHandler.getDefault()["FirstRunWizard.title"]
    }

    override fun addPages() {
        for (supplier in pageSuppliers) {
            addPage(supplier.get())
        }
    }

    override fun canFinish(): Boolean {
        return pages.filter { canShowPage(it) }.all { it.isPageComplete }
    }

    private fun canShowPage(it: IWizardPage?) = it !is OptionalWizardPage || it.isEnabled

    override fun getNextPage(page: IWizardPage?): IWizardPage? {
        return super.getNextPage(page)?.let { if (canShowPage(it)) it else getNextPage(it) }
    }

    override fun getPreviousPage(page: IWizardPage?): IWizardPage? {
        return super.getPreviousPage(page)?.let { if (canShowPage(it)) it else getPreviousPage(it) }
    }

    override fun performFinish(): Boolean {
        // Assign to a variable so that all will not shortcut execution of each page.
        val results: List<Boolean> = pages.filterIsInstance<ActionOnFinishWizardPage>().filter { canShowPage(it) }
            .map { it.performFinish() }
        return results.all { it }
    }

    override fun performCancel(): Boolean {
        return MessageDialog.openQuestion(
            shell,
            LocaleHandler.getDefault()["FirstRunWizard.exitTitle"],
            LocaleHandler.getDefault()["FirstRunWizard.exitMessage"]
        )
    }
}

fun runFirstRunWizard(
    shell: Shell? = null,
    userSettings: PropertyFileManager = BBIni.propertyFileManager,
    usageManager: UsageManager = SimpleUsageManager()
): Boolean {
    val wizardPages = mutableListOf<Supplier<IWizardPage>>()
    val usageTracking = userSettings.getProperty(USAGE_TRACKING_SETTING)
    val licenseAccepted = userSettings.getProperty(LICENSE_ACCEPTED_SETTING, "")
    if (licenseAccepted != CURRENT_LICENSE_ID) {
        wizardPages.add { LicencePage() }
    }
    if (usageTracking == null) {
        val sharedState = SurveySharedState()
        wizardPages.add { ConsentPage(sharedState, userSettings) }
        wizardPages.add { SurveyPage(sharedState, usageManager) }
    }
    return if (wizardPages.isNotEmpty()) {
        val dialog = WizardDialog(shell, FirstRunWizard(wizardPages))
        val result = dialog.open()
        result != WizardDialog.CANCEL
    } else true
}