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

import org.brailleblaster.math.spatial.ISpatialMathContainerJson
import org.brailleblaster.math.spatial.SpatialMathEnum.Fill
import org.brailleblaster.math.spatial.SpatialMathEnum.IntervalType
import org.brailleblaster.math.spatial.SpatialMathEnum.LabelPosition
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineOptions
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineSection
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineType
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineViews
import org.brailleblaster.math.spatial.SpatialMathEnum.Passage
import org.brailleblaster.math.spatial.SpatialMathEnum.Translation
import java.util.EnumSet

class NumberLineJson @JvmOverloads constructor(
    private var intervalType: IntervalType = IntervalType.WHOLE,
    var arrow: Boolean = false,
    var stretch: Boolean = false,
    var reduce: Boolean = false,
    private var leadingZeros: Boolean = false,
    var passage: Passage = Passage.NONE,
    private var numberLineType: NumberLineType? = null,
    private var viewType: NumberLineViews = NumberLineViews.AUTOMATIC_MATH,
    private var translationUserDefined: Translation = Translation.DIRECT,
    private var translationLabel: Translation = Translation.LITERARY,
    var interval: NumberLineComponent? = null,
    var line: Line? = null,
    var points: List<Point> = listOf(),
    var segment: Segment? = null,
    var sectionType: NumberLineSection? = null,
    private var userIntervals: MutableList<NumberLineInterval> = mutableListOf(),
    private var labelPosition: LabelPosition? = null,
    private var options: EnumSet<NumberLineOptions> = EnumSet.noneOf(NumberLineOptions::class.java)
) : ISpatialMathContainerJson {

    class Line(var start: NumberLineComponent, var end: NumberLineComponent, var startFill: Fill, var endFill: Fill)

    class Point(var interval: Int, var circle: Fill, var point: NumberLineComponent)

    class Segment(
        var startInterval: Int,
        var endInterval: Int,
        var startFill: Fill,
        var endFill: Fill,
        var startComponent: NumberLineComponent,
        var endComponent: NumberLineComponent
    )

    override fun jsonToContainer(): NumberLine {
        val numberLine = NumberLine()
        numberLine.settings.intervalType = intervalType
        numberLine.settings.isArrow = arrow
        numberLine.settings.isStretch = stretch
        numberLine.settings.isReduceFraction = reduce
        numberLine.settings.isRemoveLeadingZeros = leadingZeros
        numberLine.settings.passage = passage
        numberLine.settings.type = numberLineType
        numberLine.settings.translationUserDefined = translationUserDefined
        numberLine.settings.translationLabel = translationLabel
        numberLine.numberLineText.interval = interval!!
        numberLine.settings.endLineCircle = line!!.endFill
        numberLine.settings.startLineCircle = line!!.startFill
        numberLine.numberLineText.lineEnd = line!!.end
        numberLine.numberLineText.lineStart = line!!.start
        numberLine.numberLineText.points.addAll(points.map {
            NumberLineSegmentPoint(
                point = it.point,
                circle = it.circle,
                interval = it.interval
            )
        })
        numberLine
            .numberLineText.segment = NumberLineSegment(
            endSegmentCircle = segment!!.endFill,
            startSegmentCircle = segment!!.startFill,
            segmentEnd = segment!!.endComponent,
            segmentStart = segment!!.startComponent,
            startInterval = segment!!.startInterval,
            endInterval = segment!!.endInterval
        )
        numberLine.settings.sectionType = sectionType
        numberLine.settings.userDefinedArray = userIntervals
        numberLine.settings.numUserDefinedIntervals = userIntervals.size
        numberLine.settings.segmentEndInterval = segment!!.endInterval
        numberLine.settings.segmentStartInterval = segment!!.startInterval
        numberLine.settings.labelPosition = labelPosition
        numberLine.settings.options = options
        numberLine.settings.view = viewType
        numberLine.loadStringParser()
        return numberLine
    }
}

fun NumberLine.createNumberLineJson(): NumberLineJson = NumberLineJson(
    intervalType = settings.intervalType,
    arrow = settings.isArrow,
    stretch = settings.isStretch,
    reduce = settings.isReduceFraction,
    leadingZeros = settings.isRemoveLeadingZeros,
    passage = settings.passage,
    numberLineType = settings.type,
    viewType = settings.view,
    translationUserDefined = settings.translationUserDefined,
    translationLabel = settings.translationLabel,
    interval = numberLineText.interval,
    line = NumberLineJson.Line(
        start = numberLineText.lineStart,
        end = numberLineText.lineEnd,
        startFill = settings.startLineCircle,
        endFill = settings.endLineCircle
    ),
    points = segmentPoints.map {
        NumberLineJson.Point(
            interval = it.interval,
            circle = it.circle,
            point = it.point
        )
    }, segment = NumberLineJson.Segment(
        startInterval = segment.startInterval,
        endInterval = segment.endInterval,
        startFill = segment.startSegmentCircle,
        endFill = segment.endSegmentCircle,
        startComponent = segment.segmentStart,
        endComponent = segment.segmentEnd
    ), sectionType = settings.sectionType, userIntervals = settings.userDefinedArray, labelPosition = settings.labelPosition, options = settings.options)