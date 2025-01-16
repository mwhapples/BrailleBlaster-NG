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
package org.brailleblaster.utd.utils

import nu.xom.*
import org.brailleblaster.utd.exceptions.UTDException
import org.brailleblaster.utd.properties.UTDElements
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class UTDHelperTest {
    fun associatedElementBuilder(node: Node): Node {
        val parent: ParentNode = Element("myElement")
        val associate = UTDElements.BRL.create()
        parent.appendChild(node)
        parent.appendChild(associate)

        return node
    }

    fun associatedNodeBuilder(element: Element): Element {
        val parent: ParentNode = Element("myElement")
        val node: Node = Text("This text.")
        parent.appendChild(node)
        parent.appendChild(element)

        return element
    }

    fun relatedElementsBuilder(): Node {
        //<p>Some text<brl>...</brl><strong>Bold text<brl>...</brl></strong></p>
        val head: ParentNode = Element("p")
        val node: Node = Text("Some text")
        val brl = UTDElements.BRL.create()
        val parent: ParentNode = Element("strong")
        val bold: Node = Text("Bold text")
        val brl2 = UTDElements.BRL.create()
        head.appendChild(node)
        head.appendChild(brl)
        head.appendChild(parent)
        parent.appendChild(bold)
        parent.appendChild(brl2)
        return head
    }

    @DataProvider(name = "associatedElementsProvider")
    fun associatedElementsProvider(): Iterator<Array<Any>> {
        var e = Element("p")
        val brlOnly = Attribute("type", "brlonly")
        e.addAttribute(brlOnly)

        val dataList: MutableList<Array<Any>> = ArrayList()
        dataList.add(arrayOf(associatedElementBuilder(Element("p"))))
        dataList.add(arrayOf(associatedElementBuilder(Text("Some text"))))
        dataList.add(arrayOf(Text("p")))
        dataList.add(arrayOf(e))
        e = Element("root")
        e.appendChild(Element("b"))
        e.appendChild("Some text")
        dataList.add(arrayOf(e))
        return dataList.iterator()
    }

    @DataProvider(name = "associatedNodeProvider")
    fun associatedNodeProvider(): Array<Array<Any>> {
        val e = UTDElements.BRL.create()
        val brlOnly = Attribute("type", "brlonly")
        e.addAttribute(brlOnly)

        return arrayOf(
            arrayOf(associatedNodeBuilder(UTDElements.BRL.create())),
            arrayOf(associatedNodeBuilder(e))
        )
    }

    @DataProvider(name = "relatedElementsProvider")
    fun relatedElementsProvider(): Array<Array<Any>> {
        val node = associatedElementBuilder(Element("p"))
        val text = associatedElementBuilder(Text("Some text"))
        val relatedElements = relatedElementsBuilder()

        return arrayOf(
            arrayOf(node, node.query("following-sibling::node()[position()=1]")),
            arrayOf(text, text.query("following-sibling::node()[position()=1]")),
            arrayOf(Text("p"), Nodes()),
            arrayOf(relatedElements, UTDHelper.getDescendantBrlFastNodes(relatedElements))
        )
    }

    @Test(dataProvider = "relatedElementsProvider")
    fun checkRelatedElements(node: Node?, expectedResult: Nodes) {
        val brlElements = UTDHelper.getBrlElements(node)

        Assert.assertEquals(brlElements.size(), expectedResult.size())
        for (i in 0 until expectedResult.size()) {
            Assert.assertEquals(brlElements[i], expectedResult[i])
        }
    }

    @Test(expectedExceptions = [NullPointerException::class])
    fun checkNullRelatedElements() {
        UTDHelper.getBrlElements(null)
    }

    @Test(dataProvider = "associatedElementsProvider")
    fun checkAssociatedElement(node: Node) {
        val associate = UTDHelper.getAssociatedBrlElement(node)
        val expected =
            node.query("following-sibling::node()[position()=1 and local-name()='brl' and not(@type='brlonly')]")
        if (expected.size() == 0) {
            Assert.assertNull(associate)
        } else {
            Assert.assertEquals(associate, expected[0])
        }
    }

    @Test(expectedExceptions = [NullPointerException::class])
    fun checkNullAssociatedElement() {
        UTDHelper.getAssociatedBrlElement(null)
    }

    @Test(dataProvider = "associatedNodeProvider")
    fun checkAssociatedNode(element: Element) {
        val result = UTDHelper.getAssociatedNode(element)

        if ("true" != element.getAttributeValue("brlonly")) {
            Assert.assertEquals(result, element.query("preceding-sibling::node()[last()]")[0])
        } else {
            Assert.assertNull(result)
        }
    }

    @Test(expectedExceptions = [NullPointerException::class])
    fun checkNullAssociatedNode() {
        UTDHelper.getAssociatedNode(null)
    }

    @Test(expectedExceptions = [IllegalArgumentException::class])
    fun checkInvalidAssociatedNode() {
        UTDHelper.getAssociatedNode(Element("p"))
    }

    @Test(expectedExceptions = [UTDException::class])
    fun checkSiblingBeforeNode() {
        val parent: ParentNode = Element("p")
        val e = UTDElements.BRL.create()
        parent.appendChild(e)
        UTDHelper.getAssociatedNode(e)
    }

    @Test
    fun checkRelatedElements() {
        val brl = Element("brl")
        val brlOnly = Element("brlonly")
        brlOnly.appendChild("brlonly here")
        brl.appendChild(brlOnly)
        brl.appendChild("Some text")

        Assert.assertEquals("Some text", UTDHelper.getTextChild(brl).value)
    }

    @Test
    fun endsWithWhitespaceTest() {
        Assert.assertEquals(UTDHelper.endsWithWhitespace("this"), 0)
        Assert.assertEquals(UTDHelper.endsWithWhitespace("this" + UTDHelper.BRAILLE_SPACE), 1)
        Assert.assertEquals(
            UTDHelper.endsWithWhitespace(
                "this" + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE
            ), 2
        )
        Assert.assertEquals(
            UTDHelper.endsWithWhitespace(
                "this" + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE
            ), 3
        )
        Assert.assertEquals(
            UTDHelper.endsWithWhitespace(
                UTDHelper.BRAILLE_SPACE.toString() + "this" + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE
            ), 2
        )
        Assert.assertEquals(
            UTDHelper.endsWithWhitespace(
                UTDHelper.BRAILLE_SPACE.toString() + "" + UTDHelper.BRAILLE_SPACE + "this" + UTDHelper.BRAILLE_SPACE
            ), 1
        )
        Assert.assertEquals(
            UTDHelper.endsWithWhitespace(
                UTDHelper.BRAILLE_SPACE.toString() + "" + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE + "this"
            ), 0
        )
    }

    @Test
    fun startsWithWhitespaceTest() {
        Assert.assertEquals(UTDHelper.startsWithWhitespace("this"), 0)
        Assert.assertEquals(UTDHelper.startsWithWhitespace("this" + UTDHelper.BRAILLE_SPACE), 0)
        Assert.assertEquals(
            UTDHelper.startsWithWhitespace(
                "this" + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE
            ), 0
        )
        Assert.assertEquals(
            UTDHelper.startsWithWhitespace(
                "this" + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE
            ), 0
        )
        Assert.assertEquals(
            UTDHelper.startsWithWhitespace(
                UTDHelper.BRAILLE_SPACE.toString() + "this" + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE
            ), 1
        )
        Assert.assertEquals(
            UTDHelper.startsWithWhitespace(
                UTDHelper.BRAILLE_SPACE.toString() + "" + UTDHelper.BRAILLE_SPACE + "this" + UTDHelper.BRAILLE_SPACE
            ), 2
        )
        Assert.assertEquals(
            UTDHelper.startsWithWhitespace(
                UTDHelper.BRAILLE_SPACE.toString() + "" + UTDHelper.BRAILLE_SPACE + UTDHelper.BRAILLE_SPACE + "this"
            ), 3
        )
    }
}
