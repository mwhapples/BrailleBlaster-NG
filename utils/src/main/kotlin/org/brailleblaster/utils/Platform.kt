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
@file:JvmName("Platform")
package org.brailleblaster.utils

enum class OS {
    WindowsCE, Windows, Mac, Linux, Android, Unknown
}

val os: OS by lazy {
    val osName = System.getProperty("os.name")
    if (osName.startsWith("Windows CE", ignoreCase = true)) OS.WindowsCE
    else if (osName.startsWith("Windows", ignoreCase = true)) OS.Windows
    else if (osName.startsWith("Mac", ignoreCase = true) || osName.startsWith("Darwin", ignoreCase = true)) OS.Mac
    else if (osName.startsWith("Linux", ignoreCase = true)) {
        if ("dalvik".equals(System.getProperty("java.vm.name"), ignoreCase = true)) OS.Android
        else OS.Linux
    }
    else OS.Unknown
}

enum class Architecture {
    X86_64, X86, AArch64, ARM, Unknown
}

val arch: Architecture by lazy {
    val arch = System.getProperty("os.arch").lowercase()
    if (arch == "x86-64" || arch == "x86_64" || arch == "amd64") Architecture.X86_64
    else if (arch == "x86") Architecture.X86
    else if (arch == "aarch64") Architecture.AArch64
    else if (arch.startsWith("ARM")) Architecture.ARM
    else Architecture.Unknown
}