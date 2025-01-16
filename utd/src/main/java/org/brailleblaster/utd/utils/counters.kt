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

interface Counter {
    val index: Int
    operator fun inc(): Counter
    operator fun dec(): Counter
    override fun toString(): String
}

@JvmInline
value class Numbers(override val index: Int = 0) : Counter {
    init {
        require(index in MIN_VALUE..MAX_VALUE)
    }

    override fun inc(): Counter = if (index == MAX_VALUE) this else Numbers(index.inc())
    override fun dec(): Counter = if (index == MIN_VALUE) this else Numbers(index.dec())
    override fun toString(): String = index.toString()
    companion object {
        const val MIN_VALUE = 0
        const val MAX_VALUE = Int.MAX_VALUE
    }
}

fun String.toNumbers(): Numbers = Numbers(this.toInt())

private val letters = arrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')

@JvmInline
value class Letters(override val index: Int = 0) : Counter {

    init {
        require(index in MIN_VALUE..MAX_VALUE)
    }

    override fun toString(): String = if (index == MIN_VALUE) "" else {
        var i = index - 1
        val resultBuild = StringBuilder()
        while (i >= letters.size) {
            resultBuild.insert(0, letters[i % letters.size])
            i = (i / letters.size) - 1
        }
        resultBuild.insert(0, letters[i])
        resultBuild.toString()
    }

    override operator fun inc() = if (index == MAX_VALUE) this else Letters(index + 1)
    override operator fun dec() = if (index == MIN_VALUE) this else Letters(index - 1)
    companion object {
        const val MIN_VALUE = 0
        const val MAX_VALUE = Int.MAX_VALUE
    }
}

fun String.toLetters() = Letters(if (this.isEmpty()) 0 else this.fold(0) { acc, c ->
    val index = letters.indexOf(c)
    require(index >= 0)
    index + 1 + acc * letters.size
})

@JvmInline
value class RepeatingLetters(override val index: Int = 0) : Counter {
    init {
        require(index in MIN_VALUE..MAX_VALUE)
    }

    override operator fun inc() = if (index == MAX_VALUE) this else RepeatingLetters(index + 1)
    override operator fun dec() = if (index == MIN_VALUE) this else RepeatingLetters(index - 1)
    override fun toString(): String = if (index == MIN_VALUE) "" else {
        val i = index - 1
        val char = letters[i % letters.size]
        val reps = (i / letters.size) + 1
        char.toString().repeat(reps)
    }
    companion object {
        const val MIN_VALUE = 0
        const val MAX_VALUE = Int.MAX_VALUE
    }
}

fun String.toRepeatingLetters(): RepeatingLetters = if (this.isEmpty()) RepeatingLetters(0) else {
    val char = this[0]
    require(char in letters && this.all { it == char })
    RepeatingLetters(letters.size * (this.length - 1) + letters.indexOf(char) + 1)
}

private val romanDigits = arrayOf(
        arrayOf("MMM" to 3000, "MM" to 2000, "M" to 1000),
        arrayOf("CM" to 900, "DCCC" to 800, "DCC" to 700, "DC" to 600, "D" to 500, "CD" to 400, "CCC" to 300, "CC" to 200, "C" to 100),
        arrayOf("XC" to 90, "LXXX" to 80, "LXX" to 70, "LX" to 60, "L" to 50, "XL" to 40, "XXX" to 30, "XX" to 20, "X" to 10),
        arrayOf("IX" to 9, "VIII" to 8, "VII" to 7, "VI" to 6, "V" to 5, "IV" to 4, "III" to 3, "II" to 2, "I" to 1),
)

@JvmInline
value class RomanNumerals(override val index: Int) : Counter {
    init {
        require(index in MIN_VALUE..MAX_VALUE)
    }

    override operator fun dec() = if (index == MIN_VALUE) this else RomanNumerals(index - 1)
    override operator fun inc() = if (index == MAX_VALUE) this else RomanNumerals(index + 1)
    override fun toString(): String {
        val result = StringBuilder()
        var workingIndex = index
        for (row in romanDigits) {
            if (workingIndex == 0) break
            val rtn = row.firstOrNull { (_, n) -> workingIndex >= n }
            if (rtn != null) {
                result.append(rtn.first)
                workingIndex -= rtn.second
            }
        }
        return result.toString()
    }
    companion object {
        const val MIN_VALUE = 0
        val MAX_VALUE = romanDigits.sumOf { it.firstOrNull()?.second?:0 }
    }
}
fun String.toRomanNumerals(): RomanNumerals {
    var result = 0
    var workingStr = this
    for (row in romanDigits) {
        if (workingStr.isEmpty()) break
        val rtn = row.firstOrNull { (r, _) -> workingStr.startsWith(r) }
        if (rtn != null) {
            workingStr = workingStr.substring(rtn.first.length)
            result += rtn.second
        }
    }
    return if (workingStr.isEmpty()) RomanNumerals(result) else throw IllegalArgumentException("Not a valid roman numeral \"$this\"")
}