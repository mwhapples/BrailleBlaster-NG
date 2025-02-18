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

import org.brailleblaster.math.spatial.ISpatialMathContainer
import org.brailleblaster.math.spatial.ISpatialMathContainerJson
import org.brailleblaster.math.spatial.SpatialMathEnum.Fill
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineViews
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineType
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineOptions
import org.brailleblaster.math.spatial.SpatialMathEnum.LabelPosition
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineSection
import org.brailleblaster.math.spatial.SpatialMathEnum.Translation
import org.brailleblaster.math.spatial.SpatialMathEnum.Passage
import org.brailleblaster.math.spatial.SpatialMathEnum.IntervalType
import java.util.*

class NumberLineJson() : ISpatialMathContainerJson {
  private var intervalType: IntervalType = IntervalType.WHOLE
  var arrow = false
  var stretch = false
  var reduce = false
  private var leadingZeros = false
  var passage: Passage = Passage.NONE
  private var numberLineType: NumberLineType? = null
  private var viewType: NumberLineViews = NumberLineViews.AUTOMATIC_MATH
  private lateinit var translationUserDefined: Translation
  private lateinit var translationLabel: Translation
  var sectionType: NumberLineSection? = null
  var segment: Segment? = null
  var points: List<Point> = listOf()
  var interval: NumberLineComponent? = null
  var line: Line? = null
  private var userIntervals: MutableList<NumberLineInterval> = mutableListOf()
  private var labelPosition: LabelPosition? = null
  private var options: EnumSet<NumberLineOptions> = EnumSet.noneOf(NumberLineOptions::class.java)

  class Line(var start: NumberLineComponent, var end: NumberLineComponent, var startFill: Fill, var endFill: Fill) {
    constructor(numberLine: NumberLine) : this(
      start = numberLine.numberLineText.lineStart,
      end = numberLine.numberLineText.lineEnd,
      startFill = numberLine.settings.startLineCircle,
      endFill = numberLine.settings.endLineCircle
    )
  }

  class Point(var interval: Int, var circle: Fill, var point: NumberLineComponent) {
    constructor(numberLineSegment: NumberLineSegmentPoint) : this(
      interval = numberLineSegment.interval,
      circle = numberLineSegment.circle,
      point = numberLineSegment.point
      )
  }

  class Segment(var startInterval: Int, var endInterval: Int, var startFill: Fill, var endFill: Fill, var startComponent: NumberLineComponent, var endComponent: NumberLineComponent) {
    constructor(numberLineSegment: NumberLineSegment) : this(
      startInterval = numberLineSegment.startInterval,
      endInterval = numberLineSegment.endInterval,
      startFill = numberLineSegment.startSegmentCircle,
      endFill = numberLineSegment.endSegmentCircle,
      startComponent = numberLineSegment.segmentStart,
      endComponent = numberLineSegment.segmentEnd
      )
  }

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
    numberLine.numberLineText.points.addAll(points.map { NumberLineSegmentPoint(point = it.point, circle = it.circle, interval = it.interval) })
    numberLine
      .numberLineText.segment = NumberLineSegment(
      endSegmentCircle=segment!!.endFill,
      startSegmentCircle=segment!!.startFill,
      segmentEnd=segment!!.endComponent,
      segmentStart=segment!!.startComponent,
      startInterval=segment!!.startInterval,
      endInterval=segment!!.endInterval)
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

  fun containerToJson(container: ISpatialMathContainer): NumberLineJson {
    val numberLine = container as NumberLine
    intervalType = numberLine.settings.intervalType
    arrow = numberLine.settings.isArrow
    stretch = numberLine.settings.isStretch
    reduce = numberLine.settings.isReduceFraction
    leadingZeros = numberLine.settings.isRemoveLeadingZeros
    passage = numberLine.settings.passage
    numberLineType = numberLine.settings.type
    viewType = numberLine.settings.view
    translationUserDefined = numberLine.settings.translationUserDefined
    translationLabel = numberLine.settings.translationLabel
    interval = numberLine.numberLineText.interval
    line = Line(numberLine)
    points = numberLine.segmentPoints.map { Point(it) }
    segment = Segment(numberLine.segment)
    sectionType = numberLine.settings.sectionType
    userIntervals = numberLine.settings.userDefinedArray
    labelPosition = numberLine.settings.labelPosition
    options = numberLine.settings.options
    return this
  }
}

fun NumberLine.createNumberLineJson(): NumberLineJson = NumberLineJson().containerToJson(this)