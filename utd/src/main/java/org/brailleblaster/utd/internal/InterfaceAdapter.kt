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
package org.brailleblaster.utd.internal

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBElement
import jakarta.xml.bind.annotation.adapters.XmlAdapter
import org.w3c.dom.Element
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.FactoryConfigurationError
import javax.xml.parsers.ParserConfigurationException

/**
 * A JAXB adapter for persisting any object.
 * This adapter will store the class name in a type attribute so that it can recreate the Object without the original JAXBContext knowing of the class used.
 */
class InterfaceAdapter : XmlAdapter<Any?, Any?> {
    private val adapters: List<XmlAdapter<*, *>>

    @get:Throws(ParserConfigurationException::class, FactoryConfigurationError::class)
    private var documentBuilder: DocumentBuilder? = null
        get() {
            if (field == null) {
                field = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            }
            return field
        }

    constructor() {
        adapters = ArrayList()
    }

    constructor(adapters: List<XmlAdapter<*, *>>) {
        this.adapters = ArrayList(adapters)
    }

    @Throws(Exception::class)
    override fun marshal(interfaceObj: Any?): Any? {
        if (interfaceObj == null) {
            return null
        }
        val qName = QName("object")
        val type = interfaceObj.javaClass
        val jaxbElement: JAXBElement<*> = JAXBElement(qName, type, interfaceObj)
        val jc = JAXBContext.newInstance(type)
        val m = jc.createMarshaller()

        m.eventHandler = JAXBUtils.FAIL_ON_EXCEPTIONS_HANDLER

        val doc = documentBuilder!!.newDocument()
        m.marshal(jaxbElement, doc)
        val element = doc.documentElement
        element.setAttribute("type", type.name)
        return element
    }

    @Throws(Exception::class)
    override fun unmarshal(obj: Any?): Any? {
        if (obj == null) {
            return null
        }
        val element = obj as Element
        val type = Class.forName(element.getAttribute("type"))

        //		log.trace("Unmarshalling " + JAXBUtils.toXML(element));
        JAXBUtils.validateRequiredFieldsInUnmarshall(element, type)

        val jc = JAXBContext.newInstance(type)
        val unmarshaller = jc.createUnmarshaller()
        for (adapter in adapters) {
            unmarshaller.setAdapter(adapter)
        }
        unmarshaller.setAdapter(this)
        unmarshaller.eventHandler = JAXBUtils.FAIL_ON_EXCEPTIONS_HANDLER

        val jaxbElement = unmarshaller.unmarshal(element, type)
        return jaxbElement.value
    }
}
