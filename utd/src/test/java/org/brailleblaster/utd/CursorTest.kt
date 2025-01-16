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
package org.brailleblaster.utd

import nu.xom.Attribute
import nu.xom.Element
import org.brailleblaster.utd.properties.PageNumberType
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class CursorTest {
    @Test
    fun defaultConstructor() {
        val (x, y) = Cursor()
        Assert.assertEquals(x, 0.0, POSITION_DELTA.toDouble())
        Assert.assertEquals(y, 0.0, POSITION_DELTA.toDouble())
    }

    @DataProvider(name = "cursorPositionsProvider")
    fun cursorPositionsProvider(): Array<Array<Any>> {
        return arrayOf(arrayOf(1.2, 2.5), arrayOf(2.4, 1.1))
    }

    @Test(dataProvider = "cursorPositionsProvider")
    fun constructorWithPosition(x: Double, y: Double) {
        val (x1, y1) = Cursor(x, y)
        Assert.assertEquals(x1, x, POSITION_DELTA.toDouble())
        Assert.assertEquals(y1, y, POSITION_DELTA.toDouble())
    }

    @DataProvider(name = "cursorProvider")
    fun cursorProvider(): Iterator<Array<Any>> {
        val dataList: MutableList<Array<Any>> = ArrayList()
        var cursor = Cursor(0.1, 2.5)
        dataList.add(arrayOf(cursor))
        cursor = Cursor(1.4, 0.0)
        dataList.add(arrayOf(cursor))
        return dataList.iterator()
    }

    @Test(dataProvider = "cursorProvider")
    fun constructorWithExistingCursor(existingCursor: Cursor) {
        val testCursor = Cursor(existingCursor)
        Assert.assertNotSame(testCursor, existingCursor)
        Assert.assertEquals(testCursor.x, existingCursor.x, POSITION_DELTA.toDouble())
        Assert.assertEquals(testCursor.y, existingCursor.y, POSITION_DELTA.toDouble())
    }

    @DataProvider(name = "cursorPositionAndPageProvider")
    fun cursorPositionAndPageProvider(): Array<Array<Any>> {
        return arrayOf(arrayOf(1.2, 2.5, PageNumberType.NORMAL, 0), arrayOf(2.4, 1.1, PageNumberType.T_PAGE, 2))
    }

    @DataProvider(name = "invalidCursorPositionsProvider")
    fun invalidCursorPositionsProvider(): Array<Array<Any>> {
        return arrayOf(arrayOf(-1.1f, 1.8f), arrayOf(0.1f, -2.1f), arrayOf(-1.1f, -2.2f), arrayOf(-1.1f, -1.1f))
    }

    @Test(dataProvider = "invalidCursorPositionsProvider", expectedExceptions = [IllegalArgumentException::class])
    fun createWithInvalidValues(x: Float, y: Float) {
        Cursor(x.toDouble(), y.toDouble())
    }

    @DataProvider(name = "moveValuesProvider")
    fun moveValuesProvider(): Array<Array<Any>> {
        return arrayOf(arrayOf(0.0, 1.0), arrayOf(1.1, 2.3), arrayOf(1.1, -1.0), arrayOf(2.5, -1.9))
    }

    @Test(dataProvider = "moveValuesProvider")
    fun moveX(start: Double, moveBy: Double) {
        val y = Math.random()
        val cursor = Cursor(start, y)
        cursor.moveX(moveBy)
        Assert.assertEquals(cursor.y, y, POSITION_DELTA.toDouble())
        Assert.assertEquals(cursor.x, start + moveBy, POSITION_DELTA.toDouble())
    }

    @Test(dataProvider = "moveValuesProvider")
    fun setX(start: Double, moveTo: Double) {
        val y = Math.random()
        val cursor = Cursor(start, y)
        cursor.x = moveTo
        Assert.assertEquals(cursor.y, y, POSITION_DELTA.toDouble())
        Assert.assertEquals(cursor.x, moveTo, POSITION_DELTA.toDouble())
    }

    @Test(dataProvider = "moveValuesProvider")
    fun moveY(start: Double, moveBy: Double) {
        val x = Math.random()
        val cursor = Cursor(x, start)
        cursor.moveY(moveBy)
        Assert.assertEquals(cursor.x, x, POSITION_DELTA.toDouble())
        Assert.assertEquals(cursor.y, start + moveBy, POSITION_DELTA.toDouble())
    }

    @Test(dataProvider = "moveValuesProvider")
    fun setY(start: Double, moveTo: Double) {
        val x = Math.random()
        val cursor = Cursor(x, start)
        cursor.y = moveTo
        Assert.assertEquals(cursor.x, x, POSITION_DELTA.toDouble())
        Assert.assertEquals(cursor.y, moveTo, POSITION_DELTA.toDouble())
    }

    @Test
    fun moveAfterMoveTo() {
        val moveTo = Element("moveTo")
        moveTo.addAttribute(Attribute("hPos", "68.2"))
        moveTo.addAttribute(Attribute("vPos", "110"))
        val style: IStyle = Style()
        val cursor = Cursor()
        cursor.moveAfter(moveTo, style)
        Assert.assertEquals(cursor.x, 68.2, 0.01)
        Assert.assertEquals(cursor.y, 110.0, 0.01)
    }

    fun nodeBuilder(): Element {
        val root = Element("book")
        val h1 = Element("h1")
        h1.appendChild("Document Title")
        root.appendChild(h1)
        val pageNum = Element("pagenum")
        pageNum.appendChild("1")
        root.appendChild(pageNum)
        val filler = Element("brl")
        root.appendChild(filler)
        val pageNum2 = Element("pagenum")
        pageNum2.appendChild("ii")
        root.appendChild(pageNum2)
        return root
    }

    companion object {
        const val POSITION_DELTA = 0.1f
    }
}