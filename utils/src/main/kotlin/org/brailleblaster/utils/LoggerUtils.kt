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
@file:JvmName("LoggerUtils")

package org.brailleblaster.utils

import org.slf4j.Logger
import java.util.function.Supplier

/**
 * Log as trace deferring message generation.
 *
 *
 * This will log using the trace level and will only call the message supplier if the log level
 * is such that the logging will happen.
 *
 * @param message A supplier of the message to log.
 */
fun Logger.trace(message: Supplier<String>) {
    if (isTraceEnabled) {
        trace(message.get())
    }
}

/**
 * Log as info deferring message generation.
 *
 *
 * This will log using the info level and will only call the message supplier if the log level
 * is such that the logging will happen.
 *
 * @param message A supplier of the message to log.
 */
fun Logger.info(message: Supplier<String>) {
    if (isInfoEnabled) {
        info(message.get())
    }
}

/**
 * Log as debug deferring message generation.
 *
 *
 * This will log using the debug level and will only call the message supplier if the log level
 * is such that the logging will happen.
 *
 * @param message A supplier of the message to log.
 */
fun Logger.debug(message: Supplier<String>) {
    if (isDebugEnabled) {
        debug(message.get())
    }
}

/**
 * Log as warn deferring message generation.
 *
 *
 * This will log using the warn level and will only call the message supplier if the log level
 * is such that the logging will happen.
 *
 * @param message A supplier of the message to log.
 */
fun Logger.warn(message: Supplier<String>) {
    if (isWarnEnabled) {
        warn(message.get())
    }
}

/**
 * Log as error deferring message generation.
 *
 *
 * This will log using the error level and will only call the message supplier if the log level
 * is such that the logging will happen.
 *
 * @param message A supplier of the message to log.
 */
fun Logger.error(message: Supplier<String>) {
    if (isErrorEnabled) {
        error(message.get())
    }
}