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

class GridJson @JvmOverloads constructor(
    private var rows: Int = 0,
    private var cols: Int = 0,
    private var array: List<List<ISpatialMathContainerJson>> = listOf(),
    private var passage: Passage = Passage.NONE
) : ISpatialMathContainerJson {

    private var translateIdentifierAsMath = false
    override fun jsonToContainer(): Grid {
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
}

fun Grid.createGridJson(): GridJson = GridJson(
    rows = settings.rows,
    cols = settings.cols,
    array = array.map { r ->
        r.map { c -> c.json }
    },
    passage = settings.passage
)