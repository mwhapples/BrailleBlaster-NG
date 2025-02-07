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
package org.brailleblaster.math.numberLine

import org.brailleblaster.math.numberLine.NumberLineSegmentPoint.Companion.getPotentialPoints
import org.brailleblaster.math.numberLine.NumberLineSegmentPoint.Companion.getPrettyString
import org.brailleblaster.math.spatial.ISpatialMathWidget
import org.brailleblaster.math.spatial.MathFormattingException
import org.brailleblaster.math.spatial.MathText
import org.brailleblaster.math.spatial.SpatialMathDispatcher
import org.brailleblaster.math.spatial.SpatialMathEnum.Fill
import org.brailleblaster.math.spatial.SpatialMathEnum.IntervalType
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineOptions
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineSection
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineType
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineViews
import org.brailleblaster.math.spatial.SpatialMathEnum.Translation
import org.brailleblaster.math.spatial.SpatialMathUtils.translate
import org.brailleblaster.util.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import org.eclipse.swt.widgets.Shell
import org.slf4j.LoggerFactory

abstract class NumberLineWidget : ISpatialMathWidget {
    lateinit var numberLine: NumberLine
    protected fun addAdditionalSegmentDropDown(shell: Shell?, menu: Menu?) {
        val cascadeMenu = MenuItem(menu, SWT.CASCADE)
        cascadeMenu.text = NumberLineConstants.NUMBER_POINTS
        val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
        cascadeMenu.menu = dropDownMenu
        for (i in 1..MAX_SEGMENTS) {
            val button = MenuItem(dropDownMenu, SWT.RADIO)
            button.text = i.toString()
            button.selection = numberLine.numberLineText.points.size == i
            button.addListener(
                SWT.Selection
            ) {
                if (button.selection) {
                    numberLine.rebuildPoints(i)
                    SpatialMathDispatcher.dispatch()
                }
            }
        }
    }

    protected fun addSectionTypeMenu(shell: Shell?, menu: Menu?) {
        val cascadeMenu = MenuItem(menu, SWT.CASCADE)
        cascadeMenu.text = NumberLineConstants.SECTION_TYPE
        val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
        cascadeMenu.menu = dropDownMenu
        for (type in NumberLineSection.entries) {
            val menuItem = MenuItem(dropDownMenu, SWT.RADIO)
            menuItem.text = type.prettyName
            menuItem.selection = type == numberLine.settings.sectionType
            menuItem.addListener(
                SWT.Selection
            ) {
                if (!menuItem.selection) {
                    return@addListener
                }
                numberLine.settings.sectionType = type
                menuItem.selection = type == numberLine.settings.sectionType
                try {
                    numberLine.parse()
                    SpatialMathDispatcher.dispatch()
                } catch (e: MathFormattingException) {
                    logger.warn("Problem parsing number line", e)
                }
            }
        }
    }

    protected fun addPointsType(shell: Shell?, menu: Menu?) {
        val cascadeMenu = MenuItem(menu, SWT.CASCADE)
        cascadeMenu.text = NumberLineConstants.POINTS_CIRCLE_LABEL
        val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
        cascadeMenu.menu = dropDownMenu
        for (i in 1..numberLine.numberLineText.points.size) {
            val segmentNumber = i - 1
            val segment = MenuItem(dropDownMenu, SWT.CASCADE)
            val segmentMenu = Menu(dropDownMenu)
            segment.menu = segmentMenu
            segment.text = NumberLineConstants.POINT + " " + i
            for (fill in Fill.entries) {
                val button = MenuItem(segmentMenu, SWT.RADIO)
                button.text = fill.prettyName
                val isSelected =
                    if (numberLine.segmentPoints.size > segmentNumber)
                        numberLine.segmentPoints[segmentNumber].circle == fill
                    else numberLine.settings.startSegmentCircle == fill
                button.selection = isSelected
                button.addListener(
                    SWT.Selection
                ) { numberLine.segmentPoints[segmentNumber].circle = fill }
            }
        }
    }

    protected fun addNumberLineType(shell: Shell?, menu: Menu?) {
        val cascadeMenu = MenuItem(menu, SWT.CASCADE)
        cascadeMenu.text = NumberLineConstants.NUMBER_LINE_TYPE
        val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
        cascadeMenu.menu = dropDownMenu
        for (type in NumberLineType.entries) {
            val button = MenuItem(dropDownMenu, SWT.RADIO)
            button.text = type.prettyName
            button.selection = type == numberLine.settings.type
            button.addListener(
                SWT.Selection
            ) {
                if (button.selection) {
                    numberLine.settings.type = type
                    if (type == NumberLineType.AUTOMATIC_MATH) {
                        numberLine.settings.view = NumberLineViews.AUTOMATIC_MATH
                        numberLine.widget = numberLine.createWidgetForType(NumberLineViews.AUTOMATIC_MATH)
                        convertBlankEditToMath()
                    } else {
                        numberLine.settings.view = NumberLineViews.USER_DEFINED
                        numberLine.widget = numberLine.createWidgetForType(NumberLineViews.USER_DEFINED)
                        convertMathToBlankEdit()
                    }
                    button.selection = type == numberLine.settings.type
                    SpatialMathDispatcher.dispatch()
                }
            }
        }
    }

    private fun convertBlankEditToMath() {
        numberLine.numberLineText.clear()
        numberLine.settings.stringParser.clear()
    }

    private fun convertMathToBlankEdit() {
        try {
            numberLine.numberLineText = numberLine.settings.stringParser.parse()
        } catch (e: MathFormattingException) {
            logger.warn("Problem setting number line text", e)
        }
        val newIntervals: MutableList<NumberLineInterval> = ArrayList()
        val oldIntervals: List<NumberLineInterval> = numberLine.settings.userDefinedArray
        val intervals: List<NumberLineSegmentPoint> = try {
            getPotentialPoints(numberLine)
        } catch (e1: MathFormattingException) {
            return
        }
        if (intervals.isNotEmpty()) {
            for (i in intervals.indices) {
                val interval = NumberLineInterval(mathText=intervals[i].point)
                if (oldIntervals.size > i) {
                    interval.blankType = oldIntervals[i].blankType
                    interval.labelText = oldIntervals[i].labelText
                }
                try {
                    val print = getPrettyString(numberLine, intervals[i].point)
                    val braille = translate(
                        numberLine.settings.translationUserDefined,
                        getPrettyString(numberLine, intervals[i].point)
                    )
                    interval.userText = MathText(print=print, braille=braille)
                } catch (e1: MathFormattingException) {
                    return
                }
                newIntervals.add(interval)
            }
            numberLine.settings.numUserDefinedIntervals = newIntervals.size
            numberLine.settings.userDefinedArray = newIntervals
            if (NumberLineSection.SEGMENT == numberLine.settings.sectionType) {
                try {
                    numberLine.setSegmentIntervals()
                } catch (e: MathFormattingException) {
                    logger.warn("Problem when setting segment intervals", e)
                }
            }
        }
    }

    protected fun addEndSegmentType(shell: Shell?, menu: Menu?) {
        val cascadeMenu = MenuItem(menu, SWT.CASCADE)
        cascadeMenu.text = NumberLineConstants.END_SEGMENT_SYMBOL_LABEL
        val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
        cascadeMenu.menu = dropDownMenu
        for (fill in Fill.entries) {
            val button = MenuItem(dropDownMenu, SWT.RADIO)
            button.text = fill.prettyName
            button.selection = numberLine.settings.endSegmentCircle == fill
            button.addListener(
                SWT.Selection
            ) {
                numberLine.settings.endSegmentCircle = fill
                numberLine.segment.endSegmentCircle = fill
            }
        }
    }

    protected fun addStartSegmentType(shell: Shell?, menu: Menu?) {
        val cascadeMenu = MenuItem(menu, SWT.CASCADE)
        cascadeMenu.text = NumberLineConstants.START_SEGMENT_SYMBOL_LABEL
        val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
        cascadeMenu.menu = dropDownMenu
        for (fill in Fill.entries) {
            val button = MenuItem(dropDownMenu, SWT.RADIO)
            button.text = fill.prettyName
            button.selection = numberLine.settings.startSegmentCircle == fill
            button.addListener(
                SWT.Selection
            ) {
                if (!button.selection) {
                    return@addListener
                }
                numberLine.settings.startSegmentCircle = fill
                numberLine.segment.startSegmentCircle = fill
            }
        }
    }

    protected fun addOptionsMenu(shell: Shell, menu: Menu?, settingsMenu: Menu?) {
        val reducedFraction = MenuItem(settingsMenu, SWT.CHECK)
        reducedFraction.text = NumberLineConstants.REDUCE_FRACTION
        reducedFraction.selection = numberLine.settings.isReduceFraction
        reducedFraction.addListener(
            SWT.Selection
        ) {
            numberLine.settings.isReduceFraction = !numberLine.settings.isReduceFraction
            reducedFraction.selection = numberLine.settings.isReduceFraction
        }
        val beveledFraction = MenuItem(settingsMenu, SWT.CHECK)
        beveledFraction.text = NumberLineConstants.BEVELED_FRACTION
        beveledFraction.selection = numberLine.settings.isBeveledFraction
        beveledFraction.addListener(
            SWT.Selection
        ) {
            numberLine.settings.isBeveledFraction = !numberLine.settings.isBeveledFraction
            beveledFraction.selection = numberLine.settings.isBeveledFraction
        }
        val arrow = MenuItem(settingsMenu, SWT.CHECK)
        arrow.text = NumberLineConstants.ARROW_LABEL
        arrow.selection = numberLine.settings.isArrow
        arrow.addListener(
            SWT.Selection
        ) {
            numberLine.settings.isArrow = !numberLine.settings.isArrow
            arrow.selection = numberLine.settings.isArrow
        }
        val stretchButton = MenuItem(settingsMenu, SWT.CHECK)
        stretchButton.text = NumberLineConstants.STRETCH_LABEL
        stretchButton.selection = numberLine.settings.isStretch
        stretchButton.addListener(
            SWT.Selection
        ) {
            numberLine.settings.isStretch = !numberLine.settings.isStretch
            stretchButton.selection = numberLine.settings.isStretch
        }
        val leadingZeros = MenuItem(settingsMenu, SWT.CHECK)
        leadingZeros.text = NumberLineConstants.REMOVE_LEADING_ZEROS_LABEL
        leadingZeros.selection = numberLine.settings.isRemoveLeadingZeros
        leadingZeros.addListener(
            SWT.Selection
        ) {
            numberLine.settings.isRemoveLeadingZeros = !numberLine.settings.isRemoveLeadingZeros
        }
        if (NumberLineType.USER_DEFINED == numberLine.settings.type) {
            val cascadeMenu = MenuItem(settingsMenu, SWT.CASCADE)
            cascadeMenu.text = NumberLineConstants.TRANSLATION_TYPE
            val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
            cascadeMenu.menu = dropDownMenu
            for (translationType in Translation.entries) {
                val translation = MenuItem(dropDownMenu, SWT.RADIO)
                translation.text = translationType.prettyName
                translation.selection = translationType == numberLine.settings.translationUserDefined
                translation.addListener(
                    SWT.Selection
                ) {
                    if (translation.selection) {
                        numberLine.settings.translationUserDefined = translationType
                        translation.selection = translationType == numberLine.settings.translationUserDefined
                        numberLine.retranslateUserText()
                    }
                }
            }
        }
        if (numberLine.settings.options.contains(NumberLineOptions.LABELS)) {
            val cascadeMenu = MenuItem(settingsMenu, SWT.CASCADE)
            cascadeMenu.text = NumberLineConstants.TRANSLATION_LABEL
            val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
            cascadeMenu.menu = dropDownMenu
            for (translationType in Translation.entries) {
                val translation = MenuItem(dropDownMenu, SWT.RADIO)
                translation.text = translationType.prettyName
                translation.selection = translationType == numberLine.settings.translationLabel
                translation.addListener(
                    SWT.Selection
                ) {
                    if (translation.selection) {
                        numberLine.settings.translationLabel = translationType
                        translation.selection = translationType == numberLine.settings.translationLabel
                        numberLine.retranslateLabels()
                    }
                }
            }
        }
    }

    protected fun addBlankAndLabels(composite: Composite?) {
        val g = EasySWT.makeGroup(composite, SWT.NONE, 2, false)
        for (x in NumberLineOptions.entries) {
            EasySWT.makePushButton(g, x.prettyName, 1) {
                numberLine.settings.options.add(x)
                if (x == NumberLineOptions.BLANKS) {
                    numberLine.settings.view = NumberLineViews.BLANKS
                    numberLine.widget = numberLine.createWidgetForType(NumberLineViews.BLANKS)
                } else {
                    numberLine.settings.view = NumberLineViews.LABELS
                    numberLine.widget = numberLine.createWidgetForType(NumberLineViews.LABELS)
                }
                SpatialMathDispatcher.dispatch()
            }
        }
    }

    protected fun addDoneButton(composite: Composite?) {
        EasySWT.makePushButton(composite, NumberLineConstants.DONE_LABEL, 1) {
            if (NumberLineType.AUTOMATIC_MATH == numberLine.settings.type) {
                numberLine.settings.view = NumberLineViews.AUTOMATIC_MATH
                numberLine.widget = numberLine.createWidgetForType(NumberLineViews.AUTOMATIC_MATH)
            } else {
                numberLine.settings.view = NumberLineViews.USER_DEFINED
                numberLine.widget = numberLine.createWidgetForType(NumberLineViews.USER_DEFINED)
            }
            SpatialMathDispatcher.dispatch()
        }
    }

    protected fun addDebugMenu(shell: Shell?, menu: Menu?) {
        val cascadeMenu = MenuItem(menu, SWT.CASCADE)
        cascadeMenu.text = "Debug Number Line"
        val dropDownMenu = Menu(shell, SWT.DROP_DOWN)
        cascadeMenu.menu = dropDownMenu
        val a = MenuItem(dropDownMenu, SWT.NONE)
        a.text = "Whole"
        a.addListener(
            SWT.Selection
        ) {
            numberLine.settings.intervalType = IntervalType.WHOLE
            fillDebug(numberLine)
            SpatialMathDispatcher.dispatch()
        }
        val b = MenuItem(dropDownMenu, SWT.NONE)
        b.text = "Decimal"
        b.addListener(
            SWT.Selection
        ) {
            numberLine.settings.intervalType = IntervalType.DECIMAL
            fillDebug(numberLine)
            SpatialMathDispatcher.dispatch()
        }
        val c = MenuItem(dropDownMenu, SWT.NONE)
        c.text = "Improper"
        c.addListener(
            SWT.Selection
        ) {
            numberLine.settings.intervalType = IntervalType.IMPROPER
            fillDebug(numberLine)
            SpatialMathDispatcher.dispatch()
        }
        val d = MenuItem(dropDownMenu, SWT.NONE)
        d.text = "Mixed"
        d.addListener(
            SWT.Selection
        ) {
            numberLine.settings.intervalType = IntervalType.MIXED
            fillDebug(numberLine)
            SpatialMathDispatcher.dispatch()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(NumberLineWidget::class.java)
        protected const val MAX_SEGMENTS = 5
    }
}
