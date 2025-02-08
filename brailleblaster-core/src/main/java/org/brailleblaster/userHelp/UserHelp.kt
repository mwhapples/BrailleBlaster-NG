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
package org.brailleblaster.userHelp

import org.apache.commons.io.FileUtils
import org.brailleblaster.utils.BBData.getBrailleblasterPath
import org.brailleblaster.BBIni
import org.brailleblaster.CheckUpdates
import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.BuildMenuEvent
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.MenuManager
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.tools.MenuTool
import org.brailleblaster.userHelp.VersionInfo.versionsSimple
import org.brailleblaster.util.FormUIUtils
import org.brailleblaster.util.Notify.notify
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.program.Program
import org.eclipse.swt.widgets.*
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

object GoToWebsiteTool : MenuTool {
    override val topMenu: TopMenu = TopMenu.HELP
    override val title: String = "BrailleBlaster Website"
    override fun onRun(bbData: BBSelectionData) {
        showHelp(HelpOptions.GoToSite)
    }
}
object UserGuideTool : MenuTool {
    override val topMenu: TopMenu = TopMenu.HELP
    override val title: String = "BrailleBlaster User Guide"
    override val accelerator: Int = SWT.F1
    override fun onRun(bbData: BBSelectionData) {
        showHelp(HelpOptions.UserGuide)
    }
}
object AboutTool : MenuTool {
    override val topMenu: TopMenu = TopMenu.HELP
    override val title: String = "About BrailleBlaster"
    override fun onRun(bbData: BBSelectionData) {
        showHelp(HelpOptions.AboutBB)
    }
}
object CheckForUpdatesTool : MenuTool {
    override val topMenu: TopMenu = TopMenu.HELP
    override val title: String = "Check For Updates"
    override fun onRun(bbData: BBSelectionData) {
        Thread(CheckUpdates(true, Display.getCurrent())).start()
    }
}
/**
 * This class handles the items on the help menu.
 */
object UserHelp : SimpleListener {
    override fun onEvent(event: SimpleEvent) {
        if (event is BuildMenuEvent) {
            MenuManager.addMenuItem(
                GoToWebsiteTool
            )
            MenuManager.addMenuItem(
                UserGuideTool
            )
            MenuManager.addMenuItem(
                AboutTool
            )
            MenuManager.addMenuItem(
                CheckForUpdatesTool
            )
        }
    }
}
private enum class HelpOptions {
    UserGuide, AboutBB, GoToSite
}

private val helpPath = BBIni.helpDocsPath.toString() + FileSystems.getDefault().separator
private fun showHelp(helpChoice: HelpOptions) {
    when (helpChoice) {
        HelpOptions.GoToSite -> Program.launch("www.brailleblaster.org")
        HelpOptions.UserGuide -> showHelp("manualV2_1.html")
        HelpOptions.AboutBB -> showAbout()
    }
}
private fun showAbout() {
    val shell = Shell(SWT.DIALOG_TRIM or SWT.SYSTEM_MODAL)
    shell.text = "About BrailleBlaster"
    shell.layout = GridLayout(2, false)
    run {
        val img: Image = try {
            Image(
                Display.getCurrent(),
                Files.newInputStream(BBIni.programDataPath.resolve(Paths.get("images", "APH_Logo.png")))
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to load APH logo", e)
        }
        val text = Label(shell, SWT.PUSH)
        text.text = "APH Logo"
        text.image = img
        FormUIUtils.setGridDataVertical(text)
    }
    val sidePanel = Composite(shell, SWT.NONE)
    FormUIUtils.setGridDataVertical(sidePanel)
    sidePanel.layout = GridLayout()
    FormUIUtils.newLabel(
        sidePanel,
        "For more information or to report bugs visit http://www.brailleblaster.org"
    )
    run {
        val text = StyledText(sidePanel, SWT.BORDER or SWT.READ_ONLY)
        text.text = versionsSimple
        FormUIUtils.setGridDataVertical(text)
    }
    val bottomPanel = Composite(sidePanel, SWT.NONE)
    FormUIUtils.setGridDataVertical(bottomPanel)
    //		((GridData) bottomPanel.getLayoutData()).horizontalSpan = 2;
    val rowLayout = RowLayout(SWT.HORIZONTAL)
    rowLayout.center = true
    bottomPanel.layout = rowLayout
    val license = Button(bottomPanel, SWT.PUSH)
    license.text = "License"

    // TODO #6115
    val privacyPolicy = Button(bottomPanel, SWT.PUSH)
    privacyPolicy.text = "APH Privacy Policy"

    // ------ Listeners ------
    FormUIUtils.addSelectionListener(license) {
        val licensePath = getBrailleblasterPath("LICENSE.txt")
        val text: String = try {
            FileUtils.readFileToString(licensePath, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            throw RuntimeException("Unable to open license at " + licensePath.absolutePath, ex)
        }
        notify(text, "License")
    }
    FormUIUtils.addSelectionListener(privacyPolicy) {
        val licensePath = getBrailleblasterPath("APH_Privacy_Policy.txt")
        val text: String = try {
            FileUtils.readFileToString(licensePath, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            throw RuntimeException("Unable to open license at " + licensePath.absolutePath, ex)
        }
        notify(text, "APH Privacy Policy")
    }
    shell.pack()
    shell.setActive()
    shell.open()
}

/**
 * Display help documents in the local browser.
 */
private fun showHelp(fileName: String) {
    val us = "file:///" + helpPath.replace('\\', '/') + fileName
    val uriString = us.replace(" ", "%20")
    Program.launch(uriString)
}