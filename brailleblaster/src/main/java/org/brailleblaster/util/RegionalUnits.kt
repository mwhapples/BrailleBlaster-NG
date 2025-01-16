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
package org.brailleblaster.util

import org.brailleblaster.BBIni
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

enum class RegionalUnits(private val mmPerUnit: BigDecimal) {
    METRIC(BigDecimal.ONE), US(BigDecimal("25.4"));

    fun setAsPreferred() {
        BBIni.propertyFileManager.save(UNITS_PROPERTY, name)
    }

    fun lengthToMM(length: BigDecimal?): BigDecimal {
        return mmPerUnit.multiply(length)
    }

    fun mmToLength(mmLength: BigDecimal): BigDecimal {
        return mmLength.divide(mmPerUnit, 2, RoundingMode.HALF_UP)
    }

    companion object {
        private const val UNITS_PROPERTY = "regionalUnits"
        val preferred: RegionalUnits
            get() {
                val units = BBIni.propertyFileManager.getProperty(UNITS_PROPERTY)
                return findUnitsForName(units, METRIC)
            }

        fun findUnitsForName(units: String?, defaultValue: RegionalUnits): RegionalUnits {
            return if (units != null) {
                Arrays.stream(entries.toTypedArray())
                    .filter { p: RegionalUnits -> p.name == units }
                    .findFirst()
                    .orElse(defaultValue)
            } else {
                defaultValue
            }
        }
    }
}