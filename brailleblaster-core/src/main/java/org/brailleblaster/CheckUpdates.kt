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

import org.brailleblaster.utils.PropertyFileManager

const val AUTO_UPDATE_SETTING = "autoUpdate"
const val SETTING_RELEASE_CHANNEL = "checkUpdates.releaseChannel"
const val NEEDS_UPDATE_MESSAGE = ("There is an updated version of BrailleBlaster available."
        + "\nWould you like to close BrailleBlaster and view the update?")
const val CHANNEL_STABLE = "stable"
const val CHANNEL_BETA = "beta"

fun readUserUpdateSettings(settings: PropertyFileManager = BBIni.propertyFileManager): Boolean {
    /*
 * Uncomment when we have a release updatePreference = (String)
 * BBIni.getPropertyFileManager().getProperty("updatePreference",
 * "both");
 */
    return settings.getPropertyAsBoolean(AUTO_UPDATE_SETTING, true)
}

