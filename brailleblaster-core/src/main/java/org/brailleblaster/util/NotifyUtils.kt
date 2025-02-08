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
package org.brailleblaster.util

import org.apache.commons.lang3.exception.ExceptionUtils
import org.brailleblaster.Main.isInitted
import org.brailleblaster.utils.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.userHelp.VersionInfo.versionsSimple
import org.brailleblaster.wordprocessor.WPManager

/**
 * Minimal dependency Notify utils
 *
 *
 * DO NOT USE SWT INSIDE THIS CLASS. This is used for error handling before SWT
 * is loaded, and will cause a NoClassDefFoundError
 */
object NotifyUtils {
    private val localeHandler = getDefault()

    @JvmField
    val REPORT_TEXT = localeHandler["reportText"]

    @JvmField
    val REPORT_COMMENT_TEXT = localeHandler["reportComment"]
    val REPORT_DISABLED_NON_RELEASE_TEXT = localeHandler["notRelease"]
    private val ENCOUNTERED_ERROR_COPY_TEXT = localeHandler["encounteredErrorCopyText"]

    @JvmStatic
    fun generateExceptionMessage(message: String?, exception: Throwable?): String {
        //Might need to display as message
        var prefix: String = message ?: ""
        if (prefix.isNotBlank()) {
            prefix += System.lineSeparator() + System.lineSeparator()
        } else {
            prefix = ""
        }
        val book: String? = if (isInitted) {
            WPManager.getInstanceOrNull()?.getControllerOrNull()?.archiver?.path?.toString()
        } else {
            null
        }
        return ("$prefix$ENCOUNTERED_ERROR_COPY_TEXT${System.lineSeparator()}${System.lineSeparator()}Book $book${System.lineSeparator()}${
            ExceptionUtils.getStackTrace(
                exception
            )
        }${System.lineSeparator()}$versionsSimple")
    }
}