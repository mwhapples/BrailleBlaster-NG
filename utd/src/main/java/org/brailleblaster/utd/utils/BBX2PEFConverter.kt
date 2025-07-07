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

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.io.BaseEncoding
import nu.xom.Text
import org.brailleblaster.libembosser.utils.BrailleMapper
import org.brailleblaster.libembosser.utils.xml.DocumentUtils
import org.brailleblaster.utd.UTDTranslationEngine
import org.brailleblaster.utd.properties.UTDElements
import org.brailleblaster.utd.properties.UTDElements.Companion.findType
import org.brailleblaster.libembosser.spi.BrlCell
import org.brailleblaster.utils.BBData
import org.brailleblaster.utils.xom.DocumentTraversal
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.math.BigDecimal
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import java.util.function.IntPredicate
import javax.imageio.ImageIO
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathExpressionException
import javax.xml.xpath.XPathFactory
import kotlin.math.max
import kotlin.math.min

class BBX2PEFConverter : DocumentTraversal() {
    var paperHeight = 0.0
        private set
    var paperWidth = 0.0
        private set
    var leftMargin = 0.0
        private set
    var rightMargin = 0.0
        private set
    var topMargin = 0.0
        private set
    var bottomMargin = 0.0
        private set

    class ListBackedNodeList(private val nodes: List<Node> = emptyList()) : NodeList {


        override fun getLength(): Int {
            return nodes.size
        }

        override fun item(index: Int): Node {
            return nodes[index]
        }
    }

    private class Graphic(
        val id: Int,
        val image: BufferedImage?,
        val topLine: Int,
        val bottomLine: Int
    ) {

        val idString: String
            get() = String.format("tg_img%05d", id)
    }

    private var docBuilder: DocumentBuilder? = null
    private var pefDoc: Document? = null
    private var imagesElement: Element? = null
        get() {
            if (field == null) {
                field = pefDoc!!.createElementNS(PEFNamespaceContext.TG_NAMESPACE, "tg:images")
                pefDoc!!.documentElement.appendChild(field)
            }
            return field
        }
    var defaultIdentifier = "TempID"
    var volumeFilter: IntPredicate = ALL_VOLUMES
    private var volumeCounter = 0
    private var includeVolume = false
    /**
     * The converter's default duplex mode.
     *
     * @return The duplex mode the converter sets by default.
     */
    /**
     * Set the default duplex mode used by the converter.
     *
     *
     * When performing a conversion, unless the document contains an instruction otherwise, the
     * default duplex mode will be used in the PEF document.
     */
    var isDuplex = false
    private var cursorX = 0
    private var cursorY = 0
    private var pageGrid: Array<CharArray>? = Array(25) { CharArray(40) }
    private val graphics: MutableList<Graphic> = mutableListOf()
    private var imageCounter = 0

    /**
     * Number of new pages to supress
     *
     *
     * There are cases where new pages may have been handled before the utd:newPage element is
     * encountered and so the processing of the utd:newPage should be suppressed. One example of this
     * is image placeholders where the image placeholder does not appear inside a utd:brl element.
     */
    private var suppressNewPages = 0
    private val cellType = BrlCell.NLS
    private var bodyElement: Element? = null
    private var findDCFormat: XPathExpression? = null
    private var metaElement: Element? = null
    private var findDCIdentifier: XPathExpression? = null
    private var findRelativePages: XPathExpression? = null
    private var findRelativeSections: XPathExpression? = null
    private var findVolumes: XPathExpression? = null
    private var curVolElement: Element? = null
    private var curPageElement: Element? = null
    private var curSectionElement: Element? = null
    private var graphicElement: Element? = null

    init {
        val dbf = DocumentBuilderFactory.newInstance()
        dbf.isNamespaceAware = true
        docBuilder = try {
            dbf.newDocumentBuilder()
        } catch (e: ParserConfigurationException) {
            throw UnsupportedOperationException("No suitable XML DOM implementations.")
        }
        val xpath = XPathFactory.newInstance().newXPath()
        xpath.namespaceContext = PEFNamespaceContext()
        try {
            findDCFormat = xpath.compile("/pef:pef/pef:head/pef:meta/dc:format")
            findDCIdentifier = xpath.compile("/pef:pef/pef:head/pef:meta/dc:identifier")
            findVolumes = xpath.compile("pef:pef/pef:body/pef:volume")
            findRelativeSections = xpath.compile("./pef:section")
            findRelativePages = xpath.compile("./pef:page")
        } catch (e: XPathExpressionException) {
            throw UnsupportedOperationException("Unable to create some required XPath expressions")
        }
    }

    val rows: Int
        get() = pageGrid!!.size
    val cols: Int
        get() = if (rows > 0) pageGrid!![0].size else 0

    fun setPageSize(rows: Int, cols: Int) {
        pageGrid = Array(rows) { CharArray(cols) }
    }

    val pEFDoc: Document
        /**
         * Get the PEF document created by the last conversion.
         *
         * @return The PEF document.
         */
        get() {
            return pefDoc ?: throw NoSuchElementException("No BBX has been converted")
        }

    public override fun onStartElement(e: nu.xom.Element): Boolean {
        var descend = true
        if (UTDElements.BRL.isA(e)) {
            // Only process BRL elements when volume is to be included
            if (includeVolume) {
                processBrl(e)
            }
            descend = false
        } else if ("http://brailleblaster.org/ns/bb" == e.namespaceURI && "head" == e.localName) {
            processHeadElement(e)
            descend = false
        } else if ("http://brailleblaster.org/ns/bb".contentEquals(e.namespaceURI)
            && "BLOCK".contentEquals(e.localName)
        ) {
            if ("IMAGE_PLACEHOLDER".contentEquals(e.getAttributeValue("type", "http://brailleblaster.org/ns/bb")) && includeVolume) {
                processImagePlaceHolder(e)
                descend = false
            }
        }
        return descend
    }

    public override fun onEndElement(e: nu.xom.Element) {
        if ("http://brailleblaster.org/ns/bb" == e.namespaceURI && "CONTAINER" == e.localName && "VOLUME" == e.getAttributeValue(
                "type",
                "http://brailleblaster.org/ns/bb"
            )
        ) {
            endVolume()
            includeVolume = volumeFilter.test(++volumeCounter)
        }
    }

    public override fun onStartDocument(d: nu.xom.Document) {
        // Start volumeCount at 0 as volume indexes start at 0.
        volumeCounter = 0
        imageCounter = 0
        suppressNewPages = 0
        includeVolume = volumeFilter.test(volumeCounter)
        // Now initialise the PEF document.
        pefDoc = docBuilder!!.newDocument()
        val rootElement = pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "pef")
        rootElement.setAttribute("version", "2008-1")
        pefDoc!!.appendChild(rootElement)
        val headElement = pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "head")
        rootElement.appendChild(headElement)
        metaElement = pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "meta").apply {
            setAttributeNS(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:dc", PEFNamespaceContext.DC_NAMESPACE
            )
            setAttributeNS(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:paper", PEFNamespaceContext.PAPER_NAMESPACE
            )
        }
        headElement.appendChild(metaElement)
        bodyElement = pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "body")
        rootElement.appendChild(bodyElement)
    }

    public override fun onEndDocument(d: nu.xom.Document) {
        // When document ends then the volume must end
        endVolume()
        // Make sure the DOM complies with the minimum PEF requirements
        try {
            val formatList = findDCFormat!!.evaluate(pefDoc, XPathConstants.NODESET) as NodeList
            if (formatList.length != 1) {
                for (i in 0 until formatList.length) {
                    val n = formatList.item(i)
                    metaElement!!.removeChild(n)
                }
                val formatElement = pefDoc!!.createElementNS(PEFNamespaceContext.DC_NAMESPACE, "dc:format")
                formatElement.textContent = "application/x-pef+xml"
                metaElement!!.appendChild(formatElement)
            } else if ("application/x-pef+xml" == formatList.item(0).textContent) {
                formatList.item(0).textContent = "application/x-pef+xml"
            }
        } catch (e: XPathExpressionException) {
            val formatElement = pefDoc!!.createElementNS(PEFNamespaceContext.DC_NAMESPACE, "dc:format")
            formatElement.textContent = "application/x-pef+xml"
            metaElement!!.appendChild(formatElement)
        }
        try {
            val identifierList = findDCIdentifier!!.evaluate(pefDoc, XPathConstants.NODESET) as NodeList
            if (identifierList.length > 1) {
                // Only keep the first identifier
                for (i in 0 until identifierList.length) {
                    val n = identifierList.item(i)
                    metaElement!!.removeChild(n)
                }
            } else if (identifierList.length < 1) {
                val identifier: Node = pefDoc!!.createElementNS(PEFNamespaceContext.DC_NAMESPACE, "dc:identifier")
                identifier.textContent = defaultIdentifier
                metaElement!!.appendChild(identifier)
            }
        } catch (e: XPathExpressionException) {
            val identifier: Node = pefDoc!!.createElementNS(PEFNamespaceContext.DC_NAMESPACE, "dc:identifier")
            identifier.textContent = defaultIdentifier
            metaElement!!.appendChild(identifier)
        }
        try {
            var vols = findVolumes!!.evaluate(pefDoc, XPathConstants.NODESET) as NodeList
            if (vols.length == 0) {
                val newVol = createVolumeElement()
                bodyElement!!.appendChild(newVol)
                vols = ListBackedNodeList(listOf(newVol))
            }
            for (volIndex in 0 until vols.length) {
                val vol = vols.item(volIndex)
                var sections = findRelativeSections!!.evaluate(vol, XPathConstants.NODESET) as NodeList
                if (sections.length == 0) {
                    val section = createSectionElement()
                    vol.appendChild(section)
                    sections = ListBackedNodeList(listOf(section))
                }
                for (sectionIndex in 0 until sections.length) {
                    val section = sections.item(sectionIndex)
                    val pages = findRelativePages!!.evaluate(section, XPathConstants.NODESET) as NodeList
                    if (pages.length == 0) {
                        section.appendChild(createPageElement())
                    }
                }
            }
        } catch (e: XPathExpressionException) {
            // We will assume there was no suitable nodes so we create an empty volume to ensure validity
            val vol = createVolumeElement()
            bodyElement!!.appendChild(vol)
            val section = createSectionElement()
            vol.appendChild(section)
            val page = createPageElement()
            section.appendChild(page)
        }
    }

    private fun createPageElement(): Element {
        return pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "page")
    }

    private fun createSectionElement(): Element {
        return pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "section")
    }

    private fun createVolumeElement(): Element {
        val newVol = pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "volume")
        newVol.setAttribute("duplex", isDuplex.toString())
        newVol.setAttribute("rowgap", 0.toString())
        newVol.setAttribute("rows", rows.toString())
        newVol.setAttribute("cols", cols.toString())
        return newVol
    }

    private fun startVolume() {
        curVolElement = createVolumeElement()
        bodyElement!!.appendChild(curVolElement)
    }

    private fun endVolume() {
        // When a volume ends then the section must end
        endSection()
        // Set curVolume to null so that next new page knows to start a new volume.
        curVolElement = null
    }

    private fun startSection() {
        // Check we are currently in a volume, if not start a new one.
        if (curVolElement == null) {
            startVolume()
        }
        curSectionElement = createSectionElement()
        curVolElement!!.appendChild(curSectionElement)
    }

    private fun endSection() {
        // When a section ends then the page must also end.
        endPage()
        // Set curSection to null so next new page knows to create a new section
        curSectionElement = null
    }

    private fun startPage() {
        // Check that there is a current section and start a new section if not
        if (curSectionElement == null) {
            startSection()
        }
        // Blank the page grid using \u2800 empty Braille cell
        for (row in pageGrid!!) {
            Arrays.fill(row, '\u2800')
        }
        graphics.clear()
        // Set the cursor to the top left
        setCursor(0, 0)
        curPageElement = createPageElement()
        curSectionElement!!.appendChild(curPageElement)
    }

    private val insertionElement: Element?
        /**
         * Get the element for inserting rows into.
         *
         * @return The element where rows should be inserted.
         */
        get() = if (graphicElement == null) curPageElement else graphicElement

    private fun endPage() {
        if (curPageElement != null && pageGrid != null) {
            var lastNonBlankLine = -1
            graphicElement = null
            for (i in pageGrid!!.indices) {
                // required so it can be used in lambdas.
                var graphic = graphics.firstOrNull { it.topLine == i }
                if (graphic != null) {
                    insertBlankLines(lastNonBlankLine, i)
                    graphicElement = createGraphicElement(graphic, cols)
                    curPageElement!!.appendChild(graphicElement)
                    lastNonBlankLine = i - 1
                }
                val curLine = getTrimmedLine(pageGrid!![i])
                if (!Strings.isNullOrEmpty(curLine)) {
                    // Insert the blank lines which come before this line
                    insertBlankLines(lastNonBlankLine, i)
                    val row =
                        pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "row")
                    row.textContent = curLine
                    curPageElement!!.appendChild(row)
                    lastNonBlankLine = i
                }
                // Exit the graphic
                graphic = graphics.firstOrNull { it.bottomLine == i }
                if (graphic != null) {
                    // Insert the blank lines as alt Braille
                    insertBlankLines(lastNonBlankLine, i + 1)
                    lastNonBlankLine = i
                    graphicElement = null
                }
            }
        }
        // Set curPage to null so that new page will create a new one.
        curPageElement = null
    }

    private fun insertBlankLines(lastNonBlankLine: Int, i: Int) {
        for (lineCounter in lastNonBlankLine + 1 until i) {
            insertionElement!!
                .appendChild(pefDoc!!.createElementNS(PEFNamespaceContext.PEF_NAMESPACE, "row"))
        }
    }

    private fun createGraphicElement(graphic: Graphic, lineLength: Int): Element {
        val gElem = pefDoc!!.createElementNS(PEFNamespaceContext.TG_NAMESPACE, "tg:graphic")
        gElem.setAttribute("height", (graphic.bottomLine + 1 - graphic.topLine).toString())
        gElem.setAttribute("width", lineLength.toString())
        if (graphic.image != null) {
            addImageToStore(graphic.idString, graphic.image)
            gElem.setAttribute("idref", graphic.idString)
        }
        return gElem
    }

    private fun addImageToStore(imageId: String, img: BufferedImage) {
        try {
            ByteArrayOutputStream().use { output ->
                ImageIO.write(img, "png", output)
                val imageData = BaseEncoding.base64().encode(output.toByteArray())
                val imageElement = pefDoc!!.createElementNS(PEFNamespaceContext.TG_NAMESPACE, "tg:imageData")
                imageElement.setAttribute("format", "image/png")
                imageElement.setAttribute("encoding", "base64")
                imageElement.textContent = imageData
                imageElement.setAttribute("id", imageId)
                imagesElement!!.appendChild(imageElement)
            }
        } catch (e: IOException) {
            // Cannot do anything
        }
    }

    private fun processHeadElement(head: nu.xom.Element) {
        val childElements = head.childElements
        for (i in 0 until childElements.size()) {
            val child = childElements[i]
            if (PEFNamespaceContext.DC_NAMESPACE == child.namespaceURI) {
                val name = child.localName
                val value = child.value
                addMetaItem(PEFNamespaceContext.DC_NAMESPACE, "dc:$name", value)
            } else if ("meta" == child.localName) {
                val name = child.getAttributeValue("name")
                if (name != null && name.startsWith("dc:")) {
                    val value = child.value
                    addMetaItem(PEFNamespaceContext.DC_NAMESPACE, name, value)
                }
            }
        }
        addMetaItem(
            PEFNamespaceContext.PAPER_NAMESPACE,
            "paper:height", String.format(DIMENSION_TEMPLATE, paperHeight)
        )
        addMetaItem(
            PEFNamespaceContext.PAPER_NAMESPACE,
            "paper:width", String.format(DIMENSION_TEMPLATE, paperWidth)
        )
        addMetaItem(
            PEFNamespaceContext.PAPER_NAMESPACE,
            "paper:leftMargin", String.format(DIMENSION_TEMPLATE, leftMargin)
        )
        addMetaItem(
            PEFNamespaceContext.PAPER_NAMESPACE,
            "paper:rightMargin", String.format(DIMENSION_TEMPLATE, rightMargin)
        )
        addMetaItem(
            PEFNamespaceContext.PAPER_NAMESPACE,
            "paper:topMargin", String.format(DIMENSION_TEMPLATE, topMargin)
        )
        addMetaItem(
            PEFNamespaceContext.PAPER_NAMESPACE,
            "paper:bottomMargin", String.format(DIMENSION_TEMPLATE, bottomMargin)
        )
    }

    private fun addMetaItem(namespaceUri: String, name: String, value: String) {
        val element = pefDoc!!.createElementNS(namespaceUri, name)
        element.textContent = value
        metaElement!!.appendChild(element)
    }

    private fun processBrl(e: nu.xom.Element) {
        for (i in 0 until e.childCount) {
            val child = e.getChild(i)
            if (child is Text) {
                // Prevent overwriting by resetting suppressNewPages.
                suppressNewPages = 0
                insertText(child.value)
            } else if (child is nu.xom.Element) {
                val utdElementType = findType(child)
                if (utdElementType != null) {
                    // For any content reset suppressNewPages, if we were not to then you may encounter
                    // overwriting.
                    when (utdElementType) {
                        UTDElements.NEW_PAGE -> if (suppressNewPages > 0) {
                            suppressNewPages--
                        } else {
                            // First end a page, harmless for start of document.
                            endPage()
                            startPage()
                        }

                        UTDElements.MOVE_TO -> {
                            suppressNewPages = 0
                            moveTo(
                                BigDecimal(child.getAttributeValue("hPos")),
                                BigDecimal(child.getAttributeValue("vPos"))
                            )
                        }

                        UTDElements.BRL_PAGE_NUM, UTDElements.PRINT_PAGE_NUM, UTDElements.BRLONLY -> {
                            suppressNewPages = 0
                            insertTextValue(child)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun processImagePlaceHolder(e: nu.xom.Element) {
        val imageHeight = e.getAttributeValue("skipLines", UTDElements.UTD_NAMESPACE)?.toIntOrNull() ?: 1
        // prevent images of height 0 or less
        if (imageHeight < 1) {
            return
        }
        suppressNewPages = e.getAttributeValue("newPages", UTDElements.UTD_NAMESPACE)?.toIntOrNull() ?: 0
        for (i in 0 until suppressNewPages) {
            endPage()
            startPage()
        }
        // Move to a new line if not at the beginning of a line.
        if (cursorX > 0) {
            cursorY++
            cursorX = 0
        }
        // Check there is enough space on the page, start a new one as appropriate.
        if (cursorY + imageHeight >= pageGrid!!.size) {
            endPage()
            startPage()
        }
        val image = e.getAttributeValue("src", UTDElements.UTD_NAMESPACE)?.let { loadImage(it) }
        val endLine = cursorY + imageHeight - 1
        val graphic = Graphic(imageCounter++, image, cursorY, endLine)
        graphics.add(graphic)
        cursorY = endLine
        cursorX = pageGrid!![endLine].size
    }

    private fun loadImage(imgSrc: String): BufferedImage? {
        val imageUri: URI = try {
            URI(imgSrc)
        } catch (e1: URISyntaxException) {
            File(imgSrc).toURI()
        }
        return if ("file" == imageUri.scheme) {
            loadImageFile(File(imageUri))
        } else {
            null
        }
    }

    private fun loadImageFile(imgFile: File): BufferedImage? {
        return try {
            ImageIO.read(imgFile)
        } catch (e: IOException) {
            null
        }
    }

    private fun moveTo(x: BigDecimal, y: BigDecimal) {
        val cells = cellType.getCellsForWidth(x)
        val lines = cellType.getLinesForHeight(y)
        setCursor(cells, lines)
    }

    private fun setCursor(x: Int, y: Int) {
        // Like a string a line uses positions.
        cursorX = Preconditions.checkPositionIndex(x, cols)
        // Lines are elements, you cannot be beyond the last line.
        cursorY = Preconditions.checkElementIndex(y, rows)
    }

    private fun insertTextValue(e: nu.xom.Element) {
        for (i in 0 until e.childCount) {
            val child = e.getChild(i)
            if (child is Text) {
                insertText(child.value)
            }
        }
    }

    private fun insertText(text: String) {
        val remainingCells = cols - cursorX
        val insertEnd = min(remainingCells.toDouble(), text.length.toDouble()).toInt()
        // Convert text to Unicode Braille
        val unicodeBrl = BrailleMapper.ASCII_TO_UNICODE_FAST.map(text)
        // Only copy the cells which fit on the remaining line
        unicodeBrl.toCharArray(pageGrid!![cursorY], cursorX, 0, insertEnd)
        cursorX += insertEnd
        // Now check that any uncopied cells are either whitespace or empty Braille cells
        for (i in insertEnd until unicodeBrl.length) {
            val curChar = unicodeBrl[i]
            if (curChar != '\u2800' && !Character.isWhitespace(curChar)) {
                throw RuntimeException(
                    String.format(
                        "Braille not permitted outside the page area, cursor at (%d,%d), Braille is \"%s\" insert end is %d",
                        cursorX,
                        cursorY,
                        text,
                        insertEnd
                    )
                )
            }
        }
    }

    private fun getTrimmedLine(line: CharArray): String {
        val sb = StringBuilder(line.size)
        var lastNonSpaceIndex = -1
        for (i in line.indices) {
            // When encountering non-whitespace, empty Braille cell or null char (\x00)
            // Insert empty Braille cells to pad back to the previous cell with dots
            if (line[i] != '\u0000' && line[i] != '\u2800' && !Character.isWhitespace(line[i])) {
                // Remember lastNonSpaceIndex is the index before the first space we need to pad
                sb.append("\u2800".repeat(max(0.0, (i - (lastNonSpaceIndex + 1)).toDouble()).toInt()))
                lastNonSpaceIndex = i
                sb.append(line[i])
            }
        }
        return sb.toString()
    }

    companion object {
        private const val DIMENSION_TEMPLATE = "%.1fmm"
        @JvmField
        val ALL_VOLUMES = IntPredicate { true }

        @JvmStatic
        fun convertBBX2PEF(
            doc: nu.xom.Document?,
            defaultIdentifier: String,
            engine: UTDTranslationEngine,
            volumeFilter: IntPredicate,
            out: OutputStream?
        ) {
            val result = convertBBX2PEF(doc, defaultIdentifier, engine, volumeFilter)
            DocumentUtils.prettyPrintDOM(result, out)
        }

        /**
         * Helper method for converting BBX to PEF in one function call.
         *
         * @param doc The XOM Document object representing the BBX document.
         * @param defaultIdentifier The default identifier to be used, if not actually contained in the
         * XML document.
         * @param engine The UTDTranslationEngine containing the document settings.
         * @param volumeFilter A filter function for which volumes to include.
         * @return The PEF document object.
         */
        fun convertBBX2PEF(
            doc: nu.xom.Document?,
            defaultIdentifier: String,
            engine: UTDTranslationEngine,
            volumeFilter: IntPredicate
        ): Document {
            val converter = BBX2PEFConverter()
            val brlCellType = engine.brailleSettings.cellType
            val pageSettings = engine.pageSettings
            converter.paperHeight = pageSettings.paperHeight
            converter.paperWidth = pageSettings.paperWidth
            converter.leftMargin = pageSettings.leftMargin
            converter.rightMargin = pageSettings.rightMargin
            converter.topMargin = pageSettings.topMargin
            converter.bottomMargin = pageSettings.bottomMargin
            val cols = brlCellType.getCellsForWidth(pageSettings.drawableWidth.toBigDecimal())
            val rows = brlCellType.getLinesForHeight(pageSettings.drawableHeight.toBigDecimal())
            converter.defaultIdentifier = defaultIdentifier
            converter.setPageSize(rows, cols)
            converter.isDuplex = pageSettings.interpoint
            converter.volumeFilter = volumeFilter
            converter.traverseDocument(doc!!)
            return converter.pEFDoc
        }
    }
}
