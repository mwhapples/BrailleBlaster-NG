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
package org.brailleblaster.utd.config

import jakarta.xml.bind.Marshaller
import jakarta.xml.bind.Unmarshaller
import jakarta.xml.bind.annotation.*
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.brailleblaster.utd.Style
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Consumer

@XmlRootElement
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
class StyleDefinitions {
    @XmlTransient
    private var versionSet = false
    var version = 1
        @XmlAttribute private  set(value) {
            field = value
            versionSet = true
        }

    @XmlElement(name = "style")
    private val _styles: MutableList<Style> = ArrayList()

    fun addStyles(stylesInput: Iterable<Style?>) {
        stylesInput.forEach(Consumer { style: Style? -> this.addStyle(style) })
    }

    fun addStyle(style: Style?) {
        //Guard to help find bugs
        if (style == null) throw NullPointerException("style")
        if (style.name.isBlank()) throw RuntimeException("Style $style cannot have a blank name")
        val baseStyle = style.baseStyle
        if (baseStyle != null && !(_styles.contains(baseStyle) || baseStyle == defaultStyle)) {
            throw RuntimeException(
                String.format(
                    "Base style %s is not in style definitions for style %s",
                    baseStyle.name,
                    style
                )
            )
        }
        val existingStyle = getStyleByName(style.name)
        if (existingStyle != null) throw RuntimeException("Style $style is a duplicate of existing style $existingStyle")
        _styles.add(style)
    }

    fun removeStyle(style: Style?) {
        //Guard to help find bugs
        if (style == null) throw NullPointerException("style")
        if (!_styles.contains(style)) throw RuntimeException("Style $style is not contained in definitions!")
        _styles.remove(style)
    }

    fun clear() {
        _styles.clear()
    }

    val styles: List<Style>
        get() = _styles.toList()

    /**
     * Return first style that has the specified name
     *
     * @param name
     * @return
     */
    fun getStyleByName(name: String?): Style? = _styles.firstOrNull { it.name == name }

    val defaultStyle: Style?
        get() = getStyleByName(DEFAULT_STYLE)

    /**
     * Update this style definitions with the other style definitions.
     *
     * @param other The other style definitions to update from.
     */
    fun updateFrom(other: StyleDefinitions) {
        _styles.addAll(other.styles)
        this.version = other.version
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other)
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    /**
     * JAXB callback, see [Unmarshaller]
     */
    fun afterUnmarshal(u: Unmarshaller?, parent: Any?) {
        if (!versionSet) {
            log.debug("Old style definitions file, setting version to 0")
            version = 0
        }
        if (version < 1) {
            for (style in _styles) {
                if (style === defaultStyle) {
                    continue
                } else if (style.baseStyleName != null) {
                    val baseStyle = getStyleByName(style.baseStyleName)
                        ?: throw IllegalArgumentException(
                            "Can't find base style "
                                    + style.baseStyleName
                                    + " for style " + style.name
                        )
                    style.baseStyle = baseStyle
                } else if (style.baseStyle == null) {
                    style.baseStyle = defaultStyle
                }
            }
        }
    }

    /**
     * JAXB Callback, see [Marshaller]
     */
    fun beforeMarshal(m: Marshaller?) {
        for (curStyle in _styles) {
            val baseStyle = curStyle.baseStyle
            if (baseStyle != null) {
                curStyle.baseStyleName = baseStyle.name
            }
        }
    }

    companion object {
        const val DEFAULT_STYLE: String = "DEFAULT"

        @XmlTransient
        private val log: Logger = LoggerFactory.getLogger(StyleDefinitions::class.java)
    }
}
