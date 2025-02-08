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
package org.brailleblaster.search

import org.brailleblaster.localization.LocaleHandler.Companion.getBanaStyles
import org.brailleblaster.utd.properties.EmphasisType
import java.util.*

/**
 * Intermediary between saved properties files, individual searches, and the UI
 * to keep everything straight. It should reflect the current state of the UI.
 */
class SearchCriteria {
    var isFindCaseSensitive: Boolean = false
    var isWholeWord: Boolean = false
    var isSearchForward: Boolean = true

    @JvmField
    var findString: String? = null

    @JvmField
    var replaceString: String? = null

    @JvmField
    val findContainerFormatting: MutableList<ContainerFormatting> = ArrayList()

    @JvmField
    val replaceContainerFormatting: MutableList<ContainerFormatting> = ArrayList()

    //Find can have multiple search criteria for styles and emphasis
    @JvmField
    val findStyleFormatting: MutableList<StyleFormatting> = ArrayList()

    @JvmField
    val findEmphasisFormatting: MutableList<EmphasisFormatting> = ArrayList()

    //Replace can only have one style, but multiple emphasis formats (IE Bold and Italic).
    @JvmField
    var replaceStyleFormatting: StyleFormatting? = null

    @JvmField
    val replaceEmphasisFormatting: MutableList<EmphasisFormatting> = ArrayList()

    //Taken from V2
    abstract class SearchFormatting(var isNot: Boolean) : Comparable<SearchFormatting> {
        //The compareTo method really only works right for alphabetizing
        override fun compareTo(other: SearchFormatting): Int {
            if (javaClass == other.javaClass) {
                if (!isNot && other.isNot) {
                    return -1
                } else if (isNot && !other.isNot) {
                    return 1
                }
                return 0
            }
            val compared = sortPriority.compareTo(other.sortPriority)
            return if (compared != 0) {
                compared
            } else {
                javaClass.name.compareTo(other.javaClass.name)
            }
        }

        //Used to sort the negated styles after the normal ones.
        abstract val sortPriority: Int
    }

    class EmphasisFormatting internal constructor(not: Boolean, @JvmField val emphasis: EmphasisType) :
        SearchFormatting(not) {
        override fun toString(): String {
            return (if (isNot) "Not " else "") + emphasis.longName
        }

        override fun compareTo(other: SearchFormatting): Int {
            val compared = super.compareTo(other)
            return if (compared == 0) {
                //"Transcriber Note Symbols" apparently causes an error here...Most curious.
                try {
                    emphasis.longName.compareTo((other as EmphasisFormatting).emphasis.longName)
                } catch (npe: NullPointerException) {
                    compared
                }
            } else {
                compared
            }
        }

        override val sortPriority: Int
            get() = 2
    }

    class StyleFormatting //Explicitly set the displayName
    internal constructor(not: Boolean, @JvmField val style: String, @JvmField val display: String) :
        SearchFormatting(not) {
        override fun toString(): String {
            return (if (isNot) "Not " else "") + style
        }

        override val sortPriority: Int
            get() = 3
    }

    class ContainerFormatting internal constructor(val isNot: Boolean, val name: String) {
        override fun toString(): String {
            return (if (isNot) "Not " else "") + this.name
        }
    }

    val availableEmphasis: List<SearchFormatting?>
        get() = EmphasisType.entries.map { getEmphasisFormatting(false, it) }.sorted()

    fun getEmphasisFormatting(not: Boolean, type: EmphasisType): EmphasisFormatting {
        return EmphasisFormatting(not, type)
    }

    fun getStyleFormatting(not: Boolean, s: String, d: String): StyleFormatting {
        return StyleFormatting(not, s, d)
    }

    fun styleFormattingContains(sf: StyleFormatting, sfl: List<StyleFormatting>): Boolean {
        return sfl.any { compareStyleFormatting(it, sf) }
    }

    fun emphasisFormattingContains(ef: EmphasisFormatting, efl: List<EmphasisFormatting>): Boolean {
        return efl.any { compareEmphasisFormatting(ef, it) }
    }

    fun containerFormattingContains(cf: ContainerFormatting, cfl: List<ContainerFormatting>): Boolean {
        return cfl.any { compareContainerFormatting(it, cf) }
    }

    //Because the override .equals / .compareTo methods don't work, and I can't be bothered to fix them "the right way"
    fun compareStyleFormatting(sf1: StyleFormatting, sf2: StyleFormatting): Boolean {
        return sf1.isNot == sf2.isNot && sf1.style == sf2.style && sf1.display == sf2.display
    }

    fun compareEmphasisFormatting(ef1: EmphasisFormatting, ef2: EmphasisFormatting): Boolean {
        return ef1.isNot == ef2.isNot && ef1.emphasis == ef2.emphasis
    }

    fun compareContainerFormatting(cf1: ContainerFormatting, cf2: ContainerFormatting): Boolean {
        return cf1.isNot == cf2.isNot && cf1.name == cf2.name
    }

    fun printStyleFormatting(sfl: List<StyleFormatting>): String {
        val rs = StringBuilder()
        for (sf in sfl) {
            //Should be formatted like "(Not) Body Text / Body Text"
            rs.append(if (sf.isNot) "Not " else "").append(getBanaStyles()[sf.style])
                .append(" / ").append(sf.style).append(" ")
        }
        return rs.toString()
    }

    fun findHasText(): Boolean {
        return !findString.isNullOrBlank()
    }

    fun replaceHasText(): Boolean {
        return !replaceString.isNullOrEmpty()
    }

    fun findHasEmphasis(): Boolean {
        return findEmphasisFormatting.isNotEmpty()
    }

    fun findHasStyle(): Boolean {
        return findStyleFormatting.isNotEmpty()
    }

    fun findHasContainer(): Boolean {
        return findContainerFormatting.isNotEmpty()
    }

    fun replaceHasEmphasis(): Boolean {
        return replaceEmphasisFormatting.isNotEmpty()
    }

    fun replaceHasStyle(): Boolean {
        return replaceStyleFormatting != null
    }

    fun replaceHasContainer(): Boolean {
        return replaceContainerFormatting.isNotEmpty()
    }

    fun findHasAttributes(): Boolean {
        return findHasEmphasis() || findHasStyle() || findHasContainer()
    }

    fun replaceHasAttributes(): Boolean {
        return replaceHasEmphasis() || replaceHasStyle() || replaceHasContainer()
    }

    fun reset() {
        findStyleFormatting.clear()
        findEmphasisFormatting.clear()
        findContainerFormatting.clear()

        replaceStyleFormatting = null
        replaceEmphasisFormatting.clear()
        replaceContainerFormatting.clear()

        replaceString = null
        findString = null
        isFindCaseSensitive = false
        isWholeWord = false
    }
}
