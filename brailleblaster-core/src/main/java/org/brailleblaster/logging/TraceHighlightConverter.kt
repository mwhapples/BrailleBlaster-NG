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

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ANSIConstants

/**
 * Color TRACE with magenta as by default HighlightingCompositeConverter doesn't color TRACE nor
 * DEBUG which makes them look the same
 */
class TraceHighlightConverter : HighlightingCompositeConverter() {
    override fun getForegroundColorCode(event: ILoggingEvent): String = when (event.level) {
        Level.DEBUG -> ANSIConstants.CYAN_FG
        Level.TRACE -> ANSIConstants.MAGENTA_FG
        else -> super.getForegroundColorCode(event)
    }
}