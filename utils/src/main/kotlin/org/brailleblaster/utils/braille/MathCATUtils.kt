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
package org.brailleblaster.utils.braille

import onl.mdw.mathcat4j.api.MathCat
import onl.mdw.mathcat4j.core.mathCAT
import java.util.concurrent.Callable
import java.util.concurrent.Executors

private val mathCATExecutor = Executors.newSingleThreadExecutor().also {
    Runtime.getRuntime().addShutdownHook(Thread(it::shutdown))
}

fun <T> singleThreadedMathCAT(block: MathCat.() -> T): T = mathCATExecutor.submit(Callable { mathCAT(block) }).get()