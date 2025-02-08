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
package org.brailleblaster.logging

import ch.qos.logback.core.PropertyDefinerBase
import org.brailleblaster.BBData
import java.io.File

/**
 * Allow referring to the BB user directory in logback configurations.
 */
@Suppress("unused")
class UserPathFinder : PropertyDefinerBase() {
    private val bbUserDir: File = BBData.getUserDataPath("log").apply {
        if (!exists()) mkdirs()
    }

    override fun getPropertyValue(): String {
        return bbUserDir.absolutePath
    }
}