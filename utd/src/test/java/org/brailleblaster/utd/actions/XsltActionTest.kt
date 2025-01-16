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

import org.brailleblaster.utd.properties.BrailleTableType
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class XsltActionTest {
    @DataProvider(name = "tableTypesProvider")
    fun tableTypesProvider(): Iterator<Array<Any>> {
        val data: MutableList<Array<Any>> = ArrayList()
        for (tableType in BrailleTableType.entries) {
            data.add(arrayOf(tableType))
        }
        return data.iterator()
    }

    @Test
    fun defaultConstructor() {
        val action = XsltAction()
        val expectedXsltResource = "/org/brailleblaster/utd/xslt/default.xsl"
        val expectedTableType = BrailleTableType.LITERARY
        Assert.assertEquals(
            action.xsltResource,
            expectedXsltResource,
            String.format("Expected xsltResource to be %s", expectedXsltResource)
        )
        Assert.assertNull(action.xsltUri, "xsltUri should be null")
        Assert.assertEquals(
            action.table,
            expectedTableType,
            String.format("Expected table type to be %s", expectedTableType)
        )
    }

    @Test(dataProvider = "tableTypesProvider")
    fun constructorWithTable(expectedTableType: BrailleTableType?) {
        val expectedXsltResource = "/org/brailleblaster/utd/xslt/default.xsl"
        val action = XsltAction(expectedTableType!!)
        Assert.assertEquals(
            action.xsltResource,
            expectedXsltResource,
            String.format("Expected xsltResource to be %s", expectedXsltResource)
        )
        Assert.assertNull(action.xsltUri, "xsltUri should be null")
        Assert.assertEquals(
            action.table,
            expectedTableType,
            String.format("Expected table type to be %s", expectedTableType)
        )
    }
}
