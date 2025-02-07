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
import org.brailleblaster.localization.LocaleHandler
import org.brailleblaster.usage.SimpleUsageManager
import org.brailleblaster.usage.USAGE_TRACKING_SETTING
import org.brailleblaster.usage.UsageManager
import org.brailleblaster.usage.UsageRecord
import org.brailleblaster.util.PropertyFileManager
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.widgets.CompositeFactory
import org.eclipse.jface.widgets.LabelFactory
import org.eclipse.jface.widgets.TextFactory
import org.eclipse.jface.wizard.WizardPage
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text
import java.util.*

class SurveySharedState {
    var shareData: Boolean = false
}

class ConsentPage(
        private val sharedState: SurveySharedState,
        private val userSettings: PropertyFileManager = BBIni.propertyFileManager
) : WizardPage(LocaleHandler.getDefault()["ConsentPage.title"]), ActionOnFinishWizardPage {
    private lateinit var sharingCombo: ComboViewer
    private val shareData: Boolean
        get() = sharedState.shareData

    override fun createControl(parent: Composite?) {
        control = CompositeFactory.newComposite(SWT.NONE).layout(GridLayout(2, false)).layoutData(GridDataFactory.fillDefaults().grab(true, false).create()).create(parent).also { pageControl ->
            LabelFactory.newLabel(SWT.WRAP).text(LocaleHandler.getDefault()["ConsentPage.sharingDetails"])
                    .layoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create()).create(pageControl)
            LabelFactory.newLabel(SWT.WRAP).text(LocaleHandler.getDefault()["ConsentPage.declarationMessage"])
                    .create(pageControl)
            sharingCombo = ComboViewer(pageControl).apply {
                contentProvider = ArrayContentProvider.getInstance()
                labelProvider = UserOptionLabelProvider()
                input = listOf(NoSelection, *YesNoOption.entries.toTypedArray())
                selection = StructuredSelection(NoSelection)
                addSelectionChangedListener { event ->
                    sharedState.shareData = event.structuredSelection.firstElement == YesNoOption.YES
                    container.updateButtons()
                }
            }
        }
    }

    override fun isPageComplete(): Boolean {
        return super.isPageComplete() || sharingCombo.structuredSelection.firstElement !is NoSelection
    }

    override fun performFinish(): Boolean {
        userSettings.saveAsBoolean(USAGE_TRACKING_SETTING, shareData)
        return true
    }

    init {
        isPageComplete = false
        title = LocaleHandler.getDefault()["ConsentPage.title"]
        description = LocaleHandler.getDefault()["ConsentPage.description"]
    }
}

class SurveyPage(
        private val sharedState: SurveySharedState,
        private val usageManager: UsageManager = SimpleUsageManager()
) : WizardPage(LocaleHandler.getDefault()["SurveyPage.title"]), OptionalWizardPage, ActionOnFinishWizardPage {
    private lateinit var jobTitleCombo: ComboViewer
    private lateinit var jobTitleTextBox: Text
    private lateinit var jobTitleTextBoxLabel: Label
    private lateinit var locationCombo: ComboViewer
    private lateinit var locationTextBox: Text
    private lateinit var locationTextBoxLabel: Label
    private var jobTitle: String? = null
    private var location: String? = null
    override val isEnabled: Boolean
        get() = sharedState.shareData

    private fun getSelectionText(selectedElement: UserOption, text: String): String? {
        return when (selectedElement) {
            NoSelection -> null
            is OtherSelection -> if (text.isNotEmpty()) "${LocaleHandler.getDefault(Locale.US)[selectedElement.stringKey]}: $text" else null
            else -> LocaleHandler.getDefault(Locale.US)[selectedElement.stringKey]
        }
    }

    override fun createControl(parent: Composite?) {
        control = CompositeFactory.newComposite(SWT.NONE).layout(GridLayout(2, false)).create(parent).also { pageControl ->
            val labelFactory = LabelFactory.newLabel(SWT.NONE)
            labelFactory.text(LocaleHandler.getDefault()["SurveyPage.jobTitle"]).create(pageControl)
            jobTitleCombo = ComboViewer(pageControl).apply {
                contentProvider = ArrayContentProvider.getInstance()
                labelProvider = UserOptionLabelProvider()
                input = listOf(NoSelection, *JobTitle.entries.toTypedArray(), OtherSelection())
                selection = StructuredSelection(NoSelection)
                addSelectionChangedListener { event ->
                    jobTitleTextBoxLabel.isVisible = event.structuredSelection.firstElement is OtherSelection
                    jobTitleTextBox.enabled = event.structuredSelection.firstElement is OtherSelection
                    jobTitleTextBox.isVisible = event.structuredSelection.firstElement is OtherSelection
                    updateJobTitle()
                }
            }
            jobTitleTextBoxLabel = labelFactory.text(LocaleHandler.getDefault()["SurveyPage.specify"]).create(pageControl)
            jobTitleTextBoxLabel.isVisible = false
            jobTitleTextBox =
                TextFactory.newText(SWT.BORDER).onModify { updateJobTitle() }.enabled(false).create(pageControl)
            jobTitleTextBox.isVisible = false

            labelFactory.text(LocaleHandler.getDefault()["SurveyPage.location"]).create(pageControl)
            locationCombo = ComboViewer(pageControl).apply {
                contentProvider = ArrayContentProvider.getInstance()
                labelProvider = UserOptionLabelProvider()
                input = listOf(NoSelection, *UserLocation.entries.toTypedArray(), OtherSelection("UserLocation.international"))
                selection = StructuredSelection(NoSelection)
                addSelectionChangedListener { event ->
                    locationTextBoxLabel.isVisible = event.structuredSelection.firstElement is OtherSelection
                    locationTextBox.enabled = event.structuredSelection.firstElement is OtherSelection
                    locationTextBox.isVisible = event.structuredSelection.firstElement is OtherSelection
                    updateLocation()
                }
            }
            locationTextBoxLabel = labelFactory.text(LocaleHandler.getDefault()["SurveyPage.specify"]).create(pageControl)
            locationTextBoxLabel.isVisible = false
            locationTextBox =
                    TextFactory.newText(SWT.BORDER).onModify { updateLocation() }.enabled(false).create(pageControl)
            locationTextBox.isVisible = false
        }
    }

    private fun updateLocation() {
        location = getSelectionText(locationCombo.structuredSelection.firstElement as UserOption, locationTextBox.text)
        container.updateButtons()
    }

    private fun updateJobTitle() {
        jobTitle = getSelectionText(jobTitleCombo.structuredSelection.firstElement as UserOption, jobTitleTextBox.text)
        container.updateButtons()
    }

    override fun isPageComplete(): Boolean {
        return super.isPageComplete() || (jobTitle != null && location != null)
    }

    override fun performFinish(): Boolean {
        jobTitle?.let { usageManager.logger.log(UsageRecord(tool = "survey1", event = "job title", message = it)) }
        location?.let { usageManager.logger.log(UsageRecord(tool = "survey1", event = "location", message = it)) }
        return true
    }

    init {
        isPageComplete = false
        title = LocaleHandler.getDefault()["SurveyPage.title"]
        description = LocaleHandler.getDefault()["SurveyPage.description"]
    }
}
