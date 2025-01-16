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
package org.brailleblaster.utd.actions

import nu.xom.*
import org.brailleblaster.utd.UTDTranslationEngine
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class SkipActionTest {
    @DataProvider(name = "nodeProvider")
    private fun nodeProvider(): Array<Array<Any>> {
        val root = Element("root")
        val child = Element("child")
        root.appendChild(child)
        return arrayOf(
            arrayOf(root),
            arrayOf(Text("Some text")),
            arrayOf(ProcessingInstruction("include", "somefile.txt")),
            arrayOf(
                Comment("Some comment")
            )
        )
    }

    @Test(dataProvider = "nodeProvider")
    fun applyTo(node: Node) {
        val action: IAction = SkipAction()
        val originalXML = node.toXML()
        Assert.assertTrue(action.applyTo(node, UTDTranslationEngine()).isEmpty())
        Assert.assertEquals(node.toXML(), originalXML)
    }
}