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
package org.brailleblaster.math.spatial

import org.brailleblaster.math.spatial.SpatialMathEnum.Passage
import org.brailleblaster.math.template.Template

class GridJson : ISpatialMathContainerJson {
    var rows = 0
    var cols = 0
    private var translateIdentifierAsMath = false
    var passage: Passage = Passage.NONE
    var array: List<List<ISpatialMathContainerJson>> = ArrayList()
    override fun jsonToContainer(): ISpatialMathContainer {
        val page = Grid()
        page.settings.rows = rows
        page.settings.cols = cols
        val containerArray: MutableList<MutableList<ISpatialMathContainer>> = ArrayList()
        for (iSpatialMathContainerJsons in array) {
            val row: MutableList<ISpatialMathContainer> = ArrayList()
            for (iSpatialMathContainerJson in iSpatialMathContainerJsons) {
                val container = iSpatialMathContainerJson.jsonToContainer()
                if (container is Template) {
                    translateIdentifierAsMath = container.settings
                        .isTranslateIdentifierAsMath
                }
                row.add(container)
            }
            containerArray.add(row)
        }
        page.settings.passage = passage
        page.settings.isTranslateIdentifierAsMath = translateIdentifierAsMath
        page.array = containerArray
        return page
    }

    override fun containerToJson(container: ISpatialMathContainer): ISpatialMathContainerJson {
        val grid = container as Grid
        rows = grid.settings.rows
        cols = grid.settings.cols
        array = grid.array.map { r ->
            r.map { c -> c.json }
        }
        passage = container.settings.passage
        return this
    }
}