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
import org.brailleblaster.math.numberLine.NumberLineSegmentPoint.Companion.getPotentialPointsStringArray
import org.brailleblaster.math.spatial.ISpatialMathContainer
import org.brailleblaster.math.spatial.MathFormattingException
import org.brailleblaster.math.spatial.SpatialMathDispatcher
import org.brailleblaster.math.spatial.SpatialMathEnum.IntervalType
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineSection
import org.brailleblaster.perspectives.mvc.modules.views.DebugModule
import org.brailleblaster.util.FormUIUtils
import org.brailleblaster.util.Notify
import org.brailleblaster.util.Notify.notify
import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.events.FocusEvent
import org.eclipse.swt.events.FocusListener
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.Shell

class NumberLineWidgetAutomaticMath : NumberLineWidget() {
    override fun addMenuItems(shell: Shell, menu: Menu, settingsMenu: Menu): Menu {
        if (DebugModule.enabled) {
            addDebugMenu(shell, menu)
        }
        addNumberLineType(shell, menu)
        addOptionsMenu(shell, menu, settingsMenu)
        addSectionTypeMenu(shell, menu)
        if (NumberLineSection.SEGMENT == numberLine.settings.sectionType) {
            addStartSegmentType(shell, menu)
            addEndSegmentType(shell, menu)
        } else if (NumberLineSection.POINTS == numberLine.settings.sectionType) {
            addAdditionalSegmentDropDown(shell, menu)
            addPointsType(shell, menu)
        }
        return menu
    }

    override fun fillDebug(t: ISpatialMathContainer) {
        numberLine = t as NumberLine
        defaultDebug()
    }

    private fun defaultDebug() {
        when (numberLine.settings.intervalType) {
            IntervalType.WHOLE -> {
                val text = NumberLineText(
                    interval = createNumberLineComponent(whole = "1"),
                    lineEnd = createNumberLineComponent(whole = "4"),
                    lineStart =
                        createNumberLineComponent(whole = "1"),
                    segment =
                        NumberLineSegment(
                            segmentEnd =
                                createNumberLineComponent(
                                    whole = "3"
                                ),
                            segmentStart =
                                createNumberLineComponent(
                                    whole = "1"
                                )
                        )
                )
                numberLine.numberLineText = text
            }

            IntervalType.MIXED -> {
                val text = NumberLineText(
                    interval =
                        createNumberLineComponent(
                            whole = "1",
                            numerator = "1",
                            denominator = "2"
                        ),
                    lineEnd =
                        createNumberLineComponent(
                            whole = "4",
                            numerator = "1",
                            denominator = "2"
                        ),
                    lineStart =
                        createNumberLineComponent(
                            whole = "1",
                            numerator = "1",
                            denominator = "2"
                        ),
                    segment =
                        NumberLineSegment(
                            segmentEnd =
                                createNumberLineComponent(
                                    whole = "3",
                                    numerator = "",
                                    denominator = ""
                                ),
                            segmentStart =
                                createNumberLineComponent(
                                    whole = "1",
                                    numerator = "1",
                                    denominator = "2"
                                )
                        )
                )
                numberLine.numberLineText = text
            }

            IntervalType.IMPROPER -> {
                val text = NumberLineText(
                    interval =
                        createNumberLineComponent(
                            numerator = "2",
                            denominator = "5"
                        ),
                    lineEnd =
                        createNumberLineComponent(
                            numerator = "6",
                            denominator = "5"
                        ),
                    lineStart =
                        createNumberLineComponent(
                            numerator = "2",
                            denominator = "5"
                        ),
                    segment =
                        NumberLineSegment(
                            segmentEnd =
                                createNumberLineComponent(
                                    numerator = "4",
                                    denominator = "5"
                                ),
                            segmentStart =
                                createNumberLineComponent(
                                    numerator = "2",
                                    denominator = "5"
                                )
                        )
                )
                numberLine.numberLineText = text
            }

            IntervalType.DECIMAL -> {
                val text = NumberLineText(
                    interval =
                        createNumberLineComponent(
                            whole = "1",
                            decimal = "2"
                        ),
                    lineEnd =
                        createNumberLineComponent(
                            whole = "3",
                            decimal = "6"
                        ),
                    lineStart =
                        createNumberLineComponent(
                            whole = "1",
                            decimal = "2"
                        ),
                    segment =
                        NumberLineSegment(
                            segmentEnd =
                                createNumberLineComponent(
                                    whole = "2",
                                    decimal = "4"
                                ),
                            segmentStart =
                                createNumberLineComponent(
                                    whole = "1",
                                    decimal = "2"
                                )
                        )
                )
                numberLine.numberLineText = text
            }
        }
        numberLine.loadStringParser()
    }

    override fun onOpen() {}
    override fun extractText() {}
    override fun getWidget(parent: Composite, container: ISpatialMathContainer): Composite {
        numberLine = container as NumberLine
        val g = EasySWT.makeGroup(parent, 0, 2, false)
        intervalGroup(g)
        makeStartLineGroup(g)
        makeEndLineGroup(g)
        if (NumberLineSection.SEGMENT == numberLine.settings.sectionType) {
            makeStartSegmentGroup(g)
            makeEndSegmentGroup(g)
        } else if (NumberLineSection.POINTS == numberLine.settings.sectionType) {
            numberLine.initializePoints()
            makePointsCombo(g)
        }
        addBlankAndLabels(parent)
        return parent
    }

    private fun makePointsCombo(shell: Composite) {
        val g = EasySWT.makeGroup(shell, SWT.NONE, 1, true)
        g.text = NumberLineConstants.POINT
        for (i in numberLine.numberLineText.points.indices) {
            val point = numberLine.segmentPoints[i]
            val combo = Combo(g, SWT.DROP_DOWN or SWT.READ_ONLY)
            combo.addFocusListener(
                object : FocusListener {
                    override fun focusGained(e: FocusEvent) {
                        try {
                            numberLine.parse()
                            combo.setItems(*getPotentialPointsStringArray(numberLine))
                        } catch (ex: MathFormattingException) {
                            // TODO Auto-generated catch block
                            ex.printStackTrace()
                        }
                    }

                    override fun focusLost(e: FocusEvent) {
                        // TODO Auto-generated method stub
                    }
                })
            combo.data = GridData(SWT.FILL, SWT.FILL, true, true)
            try {
                combo.setItems(*getPotentialPointsStringArray(numberLine))
            } catch (_: MathFormattingException) {
            }
            FormUIUtils.addSelectionListener(
                combo
            ) {
                val newPoint: NumberLineSegmentPoint
                try {
                    newPoint = getPotentialPoints(numberLine)[combo.selectionIndex]
                    numberLine.numberLineText.points[i] = newPoint
                    SpatialMathDispatcher.dispatch()
                } catch (e1: MathFormattingException) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace()
                }
            }
            if (!point.point.isEmpty) {
                var index: Int
                try {
                    val fraction = point.point.fraction
                    val pointString = NumberLineMathUtils.getFractionString(numberLine, fraction)
                    index = combo.indexOf(pointString)
                    if (index == -1) {
                        index = 0
                    }
                } catch (_: MathFormattingException) {
                    index = 0
                }
                combo.select(index)
            }
        }
    }

    private fun makeStartSegmentGroup(startSegmentGroup: Composite) {
        EasySWT.makeLabel(startSegmentGroup, NumberLineConstants.SEGMENT_START_LABEL, 1)
        val whole = EasySWT.makeText(startSegmentGroup, 50, 1)
        FormUIUtils.addModifyListener(
            whole
        ) {
            if (!parseEntry(whole.text)) {
                notify(NumberLineConstants.NUMBER_LINE_ALLOWED_CHARS, Notify.ALERT_SHELL_NAME)
                return@addModifyListener
            }
            numberLine.settings.stringParser.segmentStart = whole.text
        }
        whole.text = numberLine.settings.stringParser.segmentStart
    }

    private fun parseEntry(s: String): Boolean {
        return s.matches("[0-9/.\\s\\-\\-]*".toRegex())
    }

    private fun makeEndSegmentGroup(endSegmentGroup: Composite) {
        EasySWT.makeLabel(endSegmentGroup, NumberLineConstants.SEGMENT_END_LABEL, 1)
        val whole = EasySWT.makeText(endSegmentGroup, 50, 1)
        FormUIUtils.addModifyListener(
            whole
        ) {
            if (!parseEntry(whole.text)) {
                notify(NumberLineConstants.NUMBER_LINE_ALLOWED_CHARS, Notify.ALERT_SHELL_NAME)
                return@addModifyListener
            }
            numberLine.settings.stringParser.segmentEnd = whole.text
        }
        whole.text = numberLine.settings.stringParser.segmentEnd
    }

    private fun makeStartLineGroup(shell: Composite) {
        EasySWT.makeLabel(shell, NumberLineConstants.LINE_START_LABEL, 1)
        val whole = EasySWT.makeText(shell, 50, 1)
        FormUIUtils.addModifyListener(
            whole
        ) {
            if (!parseEntry(whole.text)) {
                notify(NumberLineConstants.NUMBER_LINE_ALLOWED_CHARS, Notify.ALERT_SHELL_NAME)
                return@addModifyListener
            }
            numberLine.settings.stringParser.setLineStart(whole.text)
        }
        whole.text = numberLine.settings.stringParser.lineStartString
    }

    private fun makeEndLineGroup(shell: Composite) {
        EasySWT.makeLabel(shell, NumberLineConstants.LINE_END_LABEL, 1)
        val whole = EasySWT.makeText(shell, 50, 1)
        FormUIUtils.addModifyListener(
            whole
        ) {
            if (!parseEntry(whole.text)) {
                notify(NumberLineConstants.NUMBER_LINE_ALLOWED_CHARS, Notify.ALERT_SHELL_NAME)
                return@addModifyListener
            }
            numberLine.settings.stringParser.setLineEnd(whole.text)
        }
        whole.text = numberLine.settings.stringParser.lineEndString
    }

    private fun intervalGroup(shell: Composite) {
        EasySWT.makeLabel(shell, NumberLineConstants.INTERVAL_LABEL, 1)
        val whole = EasySWT.makeText(shell, 50, 1)
        FormUIUtils.addModifyListener(
            whole
        ) {
            if (!parseEntry(whole.text)) {
                notify(NumberLineConstants.NUMBER_LINE_ALLOWED_CHARS, Notify.ALERT_SHELL_NAME)
                return@addModifyListener
            }
            numberLine.settings.stringParser.intervalString = whole.text
        }
        whole.text = numberLine.settings.stringParser.intervalString
    }
}
