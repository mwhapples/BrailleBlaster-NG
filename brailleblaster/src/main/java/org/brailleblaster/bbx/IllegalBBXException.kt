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
package org.brailleblaster.bbx

import nu.xom.Node
import org.brailleblaster.utd.exceptions.NodeException
import org.brailleblaster.util.Utils

class IllegalBBXException : NodeException {
    constructor(node: Node?, message: String?) : super(message!!, node)
    constructor(cause: Throwable?, node: Node?, message: String?) : super(message!!, node, cause)
    constructor(node: Node?, message: String?, vararg messageArgs: Any?) : super(
        Utils.formatMessage(
            message,
            *messageArgs
        ), node
    )

    constructor(cause: Throwable?, node: Node?, message: String?, vararg messageArgs: Any?) : super(
        Utils.formatMessage(
            message,
            *messageArgs
        ), node, cause
    )
}