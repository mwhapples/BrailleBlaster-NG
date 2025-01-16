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
package org.brailleblaster.utils.braille

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import org.brailleblaster.libembosser.spi.BrlCell
import org.brailleblaster.utils.LengthUtils
import java.math.BigDecimal

@Deprecated(message = "Use org.brailleblaster.libembosser.spi.BrlCell instead.")
enum class BrailleCellType(val cell: BrlCell) {
    NLS(BrlCell.NLS), SMALL_ENGLISH(BrlCell.SMALL_ENGLISH);

    private val widthCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(
                    object : CacheLoader<BigDecimal, Int>() {
                        override fun load(key: BigDecimal): Int {
                            return cell.getCellsForWidth(key)
                        }
                    })
    private val heightCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(
                    object : CacheLoader<BigDecimal, Int>() {
                        override fun load(key: BigDecimal): Int {
                            return cell.getLinesForHeight(key)
                        }
                    })

    fun cellsFromWidth(width: Double): Int {
        return cellsFromWidth(LengthUtils.toLengthBigDecimal(width))
    }

    fun cellsFromWidth(width: BigDecimal): Int {
        return widthCache.getUnchecked(width)
    }

    fun linesFromHeight(height: Double): Int {
        return linesFromHeight(LengthUtils.toLengthBigDecimal(height))
    }

    fun linesFromHeight(height: BigDecimal): Int {
        return heightCache.getUnchecked(height)
    }
}