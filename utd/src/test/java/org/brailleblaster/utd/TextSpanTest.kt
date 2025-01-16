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

import nu.xom.Element
import nu.xom.Node
import nu.xom.Text
import org.brailleblaster.utd.properties.ContentType
import org.brailleblaster.utd.properties.EmphasisType
import org.testng.Assert
import org.testng.annotations.Test
import java.util.*

/**
 * This class will contain tests for TextSpan methods.
 */
class TextSpanTest {
    @Test
    fun testGetNode() {
        val node: Node = Element("b")
        val input = TextSpan(node, "")
        Assert.assertEquals(input.node, node)
    }

    @Test
    fun testGetAndSetText() {
        val node: Node = Text("Text")
        val input = TextSpan(node, "Test")
        Assert.assertEquals(input.text, "Test")
    }

    @Test
    fun testGetAndSetContentType() {
        val node: Node = Text("Std Text")
        val input = TextSpan(node, "Std Text")
        val type = ContentType.StandardText
        input.contentType = type
        Assert.assertEquals(input.contentType, type)
    }

    @Test
    fun testGetAndSetEmphasis() {
        val node: Node = Text("Special text")
        val input = TextSpan(node, "Special text")
        val bold = EmphasisType.BOLD
        val boldInput = EnumSet.of(bold)
        input.emphasis = boldInput
        Assert.assertEquals(input.emphasis, boldInput)
        val italics = EmphasisType.ITALICS
        input.emphasis = EnumSet.of(bold, italics)
        boldInput.add(italics)
        Assert.assertEquals(input.emphasis, boldInput)
    }

    @Test
    fun testAddAndRemoveEmphasis() {
        val node: Node = Text("Special text")
        val input = TextSpan(node, "Special text")
        val bold = EmphasisType.BOLD
        input.addEmphasis(bold)
        Assert.assertEquals(input.emphasis, EnumSet.of(bold))
        input.removeEmphasis(bold)
        Assert.assertEquals(input.emphasis, EnumSet.noneOf(EmphasisType::class.java))
    }
}