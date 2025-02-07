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
package org.brailleblaster

import org.brailleblaster.perspectives.mvc.modules.views.DebugModule
import org.brailleblaster.userHelp.VersionInfo
import org.brailleblaster.util.InstallId
import org.brailleblaster.util.Notify
import org.brailleblaster.util.PropertyFileManager
import org.brailleblaster.util.Utils
import org.brailleblaster.wordprocessor.WPManager
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.program.Program
import org.eclipse.swt.widgets.*
import org.slf4j.LoggerFactory

class CheckUpdates(private val userInitiated: Boolean, private val display: Display) : Runnable {
    private var userWantsUpdates = true
    private val userVersion: String = VersionInfo.Project.BB.version
    private var newestVersion: String? = null
    override fun run() {
        if (userInitiated) {
            updateControlUserInitiated()
        } else {
            updateControlDefault()
        }
    }

    /** call from menu  */
    private fun updateControlUserInitiated() {
        userWantsUpdates = readUserUpdateSettings()
        if (VersionInfo.Project.BB.overrideVersion == null && !BBIni.isReleaseBuild) {
            display.asyncExec {
                Notify.showMessage(
                    "Your installation is not a release build. "
                            + "Download release from "
                            + DOWNLOAD_URL
                )
            }
            return
        }
        if (needsUpdating()) {
            createUpdateDialog()
        } else {
            noUpdatesDialog()
        }
    }

    /** call from WPManager  */
    private fun updateControlDefault() {
        userWantsUpdates = readUserUpdateSettings()
        if (!userWantsUpdates) {
            return
        }
        if (VersionInfo.Project.BB.overrideVersion == null && !BBIni.isReleaseBuild) {
            logger.info("Dev build detected, not updating")
            return
        }
        if (needsUpdating()) {
            createUpdateDialog()
        } else {
            logger.info("No new updates found")
        }
    }

    /** @return
     */
    private fun needsUpdating(): Boolean {
        newestVersion = try {
            Utils.httpGet(
                checkUpdateUrlPrefix + "v=" + userVersion + "&channel=" + releaseChannel + "&cid=" + InstallId.id, true
            )
        } catch (e: Exception) {
            /*
       * null could mean no Internet connection or any type of failure
       */
            logger.error("Failed to check for updates", e)
            return false
        }
        return (newestVersion ?: "").trim { it <= ' ' }.isNotEmpty()
        // return !newestVersion.trim().equals(userVersion);
    }

    private fun createUpdateDialog() {
        display.syncExec {
            val display = Display.getCurrent()
            val shell = Shell(display, SWT.DIALOG_TRIM or SWT.APPLICATION_MODAL)
            val label = Label(shell, SWT.RESIZE)
            label.text = """
                $NEEDS_UPDATE_MESSAGE
                
                Your version: $userVersion
                Latest version: $newestVersion
                """.trimIndent()
            val gd = GridData()
            label.data = gd
            shell.layout = GridLayout(1, true)
            val groupUpdatePreferences = Group(shell, SWT.NONE)
            groupUpdatePreferences.layout = GridLayout(2, true)
            groupUpdatePreferences.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
            groupUpdatePreferences.text = "Change Release Channel"
            val release = Button(groupUpdatePreferences, SWT.RADIO)
            release.text = "Stable"
            release.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
            val betaAndRelease = Button(groupUpdatePreferences, SWT.RADIO)
            betaAndRelease.text = "Early Access and Stable"
            betaAndRelease.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
            if (releaseChannel == CHANNEL_STABLE) {
                release.selection = true
            } else {
                betaAndRelease.selection = true
            }
            val groupUpdateChoices = Group(shell, SWT.NONE)
            groupUpdateChoices.layout = GridLayout(2, true)
            groupUpdateChoices.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
            val dontAskMe = Button(groupUpdateChoices, SWT.CHECK)
            dontAskMe.text = "Don't ask me again"
            dontAskMe.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 2, 1)
            dontAskMe.addSelectionListener(
                object : SelectionAdapter() {
                    override fun widgetSelected(e: SelectionEvent) {
                        userWantsUpdates = false
                        changeDontAskProperty()
                        releaseChannel = if (release.selection) {
                            CHANNEL_STABLE
                        } else {
                            CHANNEL_BETA
                        }
                        shell.close()
                    }
                })
            val yes = Button(groupUpdateChoices, SWT.PUSH)
            yes.text = "Yes"
            yes.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
            yes.addSelectionListener(
                object : SelectionAdapter() {
                    override fun widgetSelected(e: SelectionEvent) {
                        userWantsUpdates = true
                        changeDontAskProperty()
                        releaseChannel = if (release.selection) {
                            CHANNEL_STABLE
                        } else {
                            CHANNEL_BETA
                        }
                        shell.close()
                        goToSite()
                        WPManager.getInstance().shell.close()
                    }
                })
            val no = Button(groupUpdateChoices, SWT.PUSH)
            no.text = "No"
            no.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)
            no.addSelectionListener(
                object : SelectionAdapter() {
                    override fun widgetSelected(e: SelectionEvent) {
                        userWantsUpdates = true
                        changeDontAskProperty()
                        releaseChannel = if (release.selection) {
                            CHANNEL_STABLE
                        } else {
                            CHANNEL_BETA
                        }
                        shell.close()
                    }
                })
            shell.pack()
            shell.open()
        }
    }

    private fun changeDontAskProperty() {
        BBIni.propertyFileManager.save("wantsUpdates", userWantsUpdates.toString())
    }

    /**  */
    private fun noUpdatesDialog() {
        display.syncExec {
            Notify.notify(
                "You are up to date with $userVersion the latest version of BrailleBlaster!",
                "Update status",
                false
            )
        }
    }

    private fun goToSite() {
        Program.launch(DOWNLOAD_URL)
    }

    companion object {
        const val AUTO_UPDATE_SETTING = "autoUpdate"
        private val logger = LoggerFactory.getLogger(CheckUpdates::class.java)
        private const val SETTING_RELEASE_CHANNEL = "checkUpdates.releaseChannel"
        private const val DOWNLOAD_URL = "https://brailleblaster.org/download.php"
        private const val CHECK_URL_PREFIX = "https://brailleblaster.org/dist/update.php?"
        private const val DEBUG_CHECK_URL_PREFIX = "https://dev.brailleblaster.org/dist/update.php?"
        private val checkUpdateUrlPrefix: String
            get() = if (DebugModule.enabled) DEBUG_CHECK_URL_PREFIX else CHECK_URL_PREFIX

        private const val NEEDS_UPDATE_MESSAGE = ("There is an updated version of BrailleBlaster available."
                + "\nWould you like to close BrailleBlaster and view the update?")
        const val CHANNEL_STABLE = "stable"
        const val CHANNEL_BETA = "beta"

        /**  */
        fun readUserUpdateSettings(settings: PropertyFileManager = BBIni.propertyFileManager): Boolean {
            /*
         * Uncomment when we have a release updatePreference = (String)
         * BBIni.getPropertyFileManager().getProperty("updatePreference",
         * "both");
         */
            return settings.getPropertyAsBoolean(AUTO_UPDATE_SETTING, true)
        }
        // guess based on ending version
        private var releaseChannel: String
            get() {
                var channel = BBIni.propertyFileManager.getProperty(SETTING_RELEASE_CHANNEL)
                if (channel == null) {
                    // guess based on ending version
                    channel = if (VersionInfo.Project.BB.isStableRelease) {
                        CHANNEL_STABLE
                    } else {
                        CHANNEL_BETA
                    }
                }
                return channel
            }
            set(channel) {
                BBIni.propertyFileManager.save(SETTING_RELEASE_CHANNEL, channel)
            }
    }

}