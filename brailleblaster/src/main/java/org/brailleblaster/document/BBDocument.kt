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
package org.brailleblaster.document

import nu.xom.*
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.BBX.SubType
import org.brailleblaster.perspectives.Controller
import org.brailleblaster.settings.UTDManager
import org.brailleblaster.utd.BRFWriter
import org.brailleblaster.utd.UTDTranslationEngine
import org.brailleblaster.utd.properties.UTDElements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Path

open class BBDocument(
    protected val controller: Controller,
    /**
     * Gets the XOM document, typically for either passing to another methods or file writers
     *
     * @return XOM Document
     */
    @JvmField val doc: Document
) {
    protected val utdManager: UTDManager

    fun translateDocument() {
        settingsManager.engine.expectedTranslate = true
        val translatedDoc = engine.translateAndFormatDocument(doc)
        settingsManager.engine.expectedTranslate = false

        //Swap root elements since the doc reference needs to stay the same
        val translatedRoot = translatedDoc.rootElement
        //XOM doesn't allow you to detach the root element, but you can swap it
        translatedDoc.replaceChild(translatedRoot, Element("empty"))
        doc.replaceChild(doc.rootElement, translatedRoot)
    }

    /**
     * Creates an element and adds document namespace
     *
     * @param subType:      Element subtype
     * @param attribute: attribute key: typically of type semantic, but can be others
     * @param value:     value of attribute typically a semantic-action value
     * @return Element created
     */
    fun makeElement(subType: SubType, attribute: String?, value: String): Element {
        val e = makeElement(subType)
        if (attribute != null) e.addAttribute(Attribute(attribute, value))

        return e
    }

    fun makeElement(subType: SubType): Element {
        return subType.create()
    }

    /**
     * Adds the document namespace to the element being inserted into the DOM.
     * Elements created and inserted into the XOM document do not have an initial namespace
     * and may be skipped by the XOM api in certain cases.
     *
     * @param e: Element to which it and all child elements will have the document uri added
     */
    protected fun addNamespace(e: Element) {
        e.namespaceURI = doc.rootElement.namespaceURI

        val els = e.childElements
        for (i in 0 until els.size()) addNamespace(els[i])
    }

    val rootElement: Element
        /**
         * Gets the root element of the DOM
         *
         * @return Root element of DOM
         */
        get() = doc.rootElement

    /**
     * recursive method that strips brl nodes, helper method used by getNewXML method
     *
     * @param e: Element which braille will be removed
     */
    fun removeAllBraille(e: Element) {
        if (UTDElements.META.isA(e)) {
            if (attributeExists(e, "name") && e.getAttributeValue("name") == "utd") e.parent.removeChild(e)
            else {
                if (attributeExists(e, "semantics")) {
                    val attr = e.getAttribute("semantics")
                    e.removeAttribute(attr)
                }
            }
        } else if (e.getAttribute("semantics") != null) {
            val attr = e.getAttribute("semantics")
            e.removeAttribute(attr)
        }

        for (curElement in e.childElements) {
            if (UTDElements.BRL.isA(curElement)) {
                e.removeChild(curElement)
            } else if (BBX.CONTAINER.TABLE.isA(curElement)
                && curElement.getAttribute("class") != null && curElement.getAttributeValue("class")
                    .contains("utd:table")
            ) e.removeChild(curElement)
            else {
                removeAllBraille(curElement)
            }
        }
    }

    var currentStart: Int = 0
    var print: Boolean = false
    var nlflag: Boolean = false
    var psFlag: Boolean = false

    init {
        try {
            utdManager = TEST_UTD.let { testUtd ->
                if (TEST_MODE && testUtd != null) {
                    log.warn("Using cached Test UTDManager")
                    testUtd.also {
                        it.reloadMapsFromDoc()
                    }
                } else {
                    UTDManager().also {
                        it.loadEngineFromDoc(doc, "bbx")
                    }
                }
            }
            TEST_UTD = this.utdManager

            if (TEST_MODE) {
                utdManager.testSetup()
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize UTD engine", e)
        }
    }

    fun createBrlFile(filePath: Path, start: Int, end: Int, scope: Int): Boolean {
        // Setting now to be ignored, embosser drivers will handle margins.
        // boolean addMargin = BBIni.getPropertyFileManager().getPropertyAsBoolean(PagePropertiesTab.getSettingPageUseMarginForEmboss());
        val addMargin = false
        try {
            if (start == end && start == 1 && scope == 0) {
                print = true
                val output = FileOutputStream(filePath.toFile())
                setMarginTopLines(output, addMargin)
                setMarginLeftCells(output, addMargin)
                engine.toBRF(doc, { onChar: Char ->
                    if (nlflag) {
                        setMarginLeftCells(output, addMargin)
                        nlflag = false
                    }
                    if (psFlag) {
                        setMarginTopLines(output, addMargin)
                        setMarginLeftCells(output, addMargin)
                        psFlag = false
                    }
                    if (onChar == BRFWriter.NEWLINE) {
                        nlflag = true
                    }
                    if (onChar == BRFWriter.PAGE_SEPARATOR) {
                        psFlag = true
                    }
                    if (print) {
                        BRFWriter.lineEndingRewriter { b: Char -> output.write(b.code) }.accept(onChar)
                    }
                }, BRFWriter.OPTS_DEFAULT, BRFWriter.EMPTY_PAGE_LISTENER)
            } else {
                val output = FileOutputStream(filePath.toFile())
                if (start == 1) {
                    print = true
                    setMarginTopLines(output, addMargin)
                    setMarginLeftCells(output, addMargin)
                }

                engine.toBRF(doc, { onChar: Char ->
                    if (nlflag && print) {
                        setMarginLeftCells(output, addMargin)
                        nlflag = false
                    }
                    if (psFlag) {
                        psFlag = false
                        print = true
                        setMarginTopLines(output, addMargin)
                        setMarginLeftCells(output, addMargin)
                    }

                    if (onChar == BRFWriter.PAGE_SEPARATOR) {
                        currentStart++
                        when (currentStart) {
                            start - 1 -> {
                                psFlag = true
                            }
                            in start..<end -> {
                                psFlag = true
                            }
                            end -> {
                                output.write(onChar.code)
                                print = false
                                output.close()
                            }
                        }
                    } else if (onChar == BRFWriter.NEWLINE && print) {
                        nlflag = true
                    }
                    if (print) {
                        BRFWriter.lineEndingRewriter { b: Char -> output.write(b.code) }.accept(onChar)
                    }
                }, BRFWriter.OPTS_DEFAULT, BRFWriter.EMPTY_PAGE_LISTENER)
            }
            print = false
            currentStart = 0
            nlflag = false
            psFlag = false
            return true
        } catch (e: IOException) {
            log.debug("Attempting to save BRF", e)
            return false
        }
    }

    fun setMarginTopLines(output: FileOutputStream, addMargin: Boolean): FileOutputStream {
        if (addMargin) {
            val pageSettings = engine.pageSettings
            val marginTopLines =
                engine.brailleSettings.cellType.getLinesForHeight(BigDecimal.valueOf(pageSettings.topMargin))
            for (i in 0 until marginTopLines) {
                try {
                    output.write(BRFWriter.NEWLINE.code)
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    log.debug("Attempting to add top margin", e)
                }
            }
        }
        return output
    }

    fun setMarginLeftCells(output: FileOutputStream, addMargin: Boolean): FileOutputStream {
        if (addMargin) {
            val pageSettings = engine.pageSettings
            val marginLeftCells =
                engine.brailleSettings.cellType.getCellsForWidth(BigDecimal.valueOf(pageSettings.leftMargin))
            for (i in 0 until marginLeftCells) {
                try {
                    output.write(' '.code)
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    log.debug("Attempting to add left margin", e)
                }
            }
        }
        return output
    }


    /**
     * Checks whether an element attribute value matches a specified value
     *
     * @param e:Element           to check
     * @param attribute:attribute name
     * @param value:              attribute value
     * @return true if attribute contains that value, false if attribute does not exist or value is different
     */
    fun checkAttributeValue(e: Element, attribute: String?, value: String): Boolean {
        return try {
            e.getAttributeValue(attribute) == value
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * Checks whether an element contains a specified attribute
     *
     * @param e:         Element to check
     * @param attribute: String value of attribute name to check
     * @return true if elements contains the attribute, false if not
     */
    fun attributeExists(e: Element, attribute: String?): Boolean {
        return e.getAttribute(attribute) != null
    }

    /**
     * Searches UTDML markup to find the print page translation within a pagenum element
     * this is the text representation in the UTDML markup, not the braille representation
     *
     * @param e: Element to search
     * @return the text node containing the print page translation
     */
    fun findPrintPageNode(e: Element): Node? {
        val count = e.childCount
        for (i in 0 until count) {
            if (e.getChild(i) is Text) return e.getChild(i)

            //TODO: maybe handle case when print page has no text
        }

        return null
    }

    /**
     * Queries the document using xpath
     *
     * @param query: xpath query
     * @return NodeList cotaining query result
     */
    fun query(query: String?): Nodes {
        val context = XPathContext.makeNamespaceContext(doc.rootElement)
        return doc.query(query, context)
    }

    val settingsManager: UTDManager
        get() = utdManager

    val linesPerPage: Int
        get() {
            val heightMM = engine.pageSettings.drawableHeight
            return engine.brailleSettings.cellType.getLinesForHeight(BigDecimal.valueOf(heightMM))
        }

    val engine: UTDTranslationEngine
        get() = utdManager.engine


    companion object {
        const val TEST_MODE: Boolean = false
        var TEST_UTD: UTDManager? = null
        private val log: Logger = LoggerFactory.getLogger(BBDocument::class.java)
    }
}
