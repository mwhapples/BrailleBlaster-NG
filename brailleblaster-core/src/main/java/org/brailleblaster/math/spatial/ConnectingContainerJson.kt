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

import org.brailleblaster.math.mathml.MathModule
import org.brailleblaster.math.spatial.SpatialMathEnum.HorizontalJustify
import org.brailleblaster.math.spatial.SpatialMathEnum.VerticalJustify

class ConnectingContainerJson @JvmOverloads constructor(
    private var printText: String = "",
    var isMath: Boolean = false,
    private var verticalJustify: VerticalJustify? = null,
    private var horizontalJustify: HorizontalJustify? = null
) : ISpatialMathContainerJson {

    override fun jsonToContainer(): ConnectingContainer {
        val cc = ConnectingContainer()
        val connectingContainerJson = this
        connectingContainerJson.horizontalJustify?.let {
            cc.settings.horizontal = it
        }
        connectingContainerJson.verticalJustify?.let {
            cc.settings.vertical = it
        }
        cc.settings.isTranslateAsMath = connectingContainerJson.isMath
        cc.text = MathText(
            print = connectingContainerJson.printText,
            braille =
                if (isMath) MathModule.translateMathPrint(connectingContainerJson.printText) else MathModule.translateMainPrint(
                    connectingContainerJson.printText
                )
        )
        return cc
    }
}

fun ConnectingContainer.createConnectingContainerJson(): ConnectingContainerJson = ConnectingContainerJson(
    isMath = settings.isTranslateAsMath,
    printText = printText,
    verticalJustify = settings.vertical,
    horizontalJustify = settings.horizontal
)