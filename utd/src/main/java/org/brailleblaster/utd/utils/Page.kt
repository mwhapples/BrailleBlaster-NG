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

import org.brailleblaster.utils.LengthUtils
import java.text.DecimalFormat

class Page(
    private val type: String,
    /**
     * Get the width in micrometres.
     *
     * @return The width of the page in micrometres.
     */
    val width: Int,
    /**
     * Get the height in micrometres.
     *
     * @return The page height in micrometres.
     */
    val height: Int,
    val leftMargin: Int,
    val rightMargin: Int,
    val topMargin: Int,
    val bottomMargin: Int
) {

    constructor(
        type: String,
        width: Double,
        height: Double,
        leftMargin: Double,
        rightMargin: Double,
        topMargin: Double,
        bottomMargin: Double,
        units: LengthUtils.Units
    ) : this(
        type,
        units.fromUnits(width),
        units.fromUnits(height),
        units.fromUnits(leftMargin),
        units.fromUnits(rightMargin),
        units.fromUnits(topMargin),
        units.fromUnits(bottomMargin)
    )

    fun getWidth(units: LengthUtils.Units): Double = units.asUnits(width)

    fun getHeight(units: LengthUtils.Units): Double = units.asUnits(height)

    fun getLeftMargin(units: LengthUtils.Units): Double = units.asUnits(leftMargin)

    fun getRightMargin(units: LengthUtils.Units): Double = units.asUnits(rightMargin)

    fun getTopMargin(units: LengthUtils.Units): Double = units.asUnits(topMargin)

    fun getBottomMargin(units: LengthUtils.Units): Double = units.asUnits(bottomMargin)

    override fun toString(): String = "$type ($width, $height)"

    fun toString(units: LengthUtils.Units): String = "$type (${units.asUnits(width)}, ${units.asUnits(height)}"

    fun toString(units: LengthUtils.Units, df: DecimalFormat): String = "$type (${df.format(units.asUnits(width))}, ${df.format(units.asUnits(height))})"

    companion object {
        @JvmField
        val STANDARD_PAGES: List<Page> = listOf(
            Page("Standard", 292100, 279400, 25400, 18700, 12700, 16700),
            Page("Letter", 215900, 279400, 7600, 9900, 12700, 16700),
            Page("Legal", 215900, 355600, 12700, 17200, 25400, 30200),
            Page("A3", 297000, 420000, 25000, 24000, 35000, 35000),
            Page("A4", 210000, 297000, 15100, 15100, 23500, 23500),
            Page("A5", 148000, 210000, 17000, 12700, 10000, 10000)
        )
        val DEFAULT_PAGE = STANDARD_PAGES[0]
    }
}