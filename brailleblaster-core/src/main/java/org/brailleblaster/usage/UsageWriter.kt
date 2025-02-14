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
package org.brailleblaster.usage

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.Writer
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

interface UsageWriter {
    fun write(record: UsageRecord, writer: Writer)
    fun write(records: Iterable<UsageRecord>, writer: Writer)
}

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss.SSS", Locale.US).withZone(ZoneOffset.ofHours(0))

class JsonUsageWriter : UsageWriter {
    private fun write(record: UsageRecord): JsonElement {
        return buildJsonObject {
            put("time", formatter.format(record.time))
            put("tool", record.tool)
            put("event", record.event)
            put("msg", record.message)
        }
    }

    override fun write(record: UsageRecord, writer: Writer) {
        writer.write(write(record).toString())
    }

    override fun write(records: Iterable<UsageRecord>, writer: Writer) {
        writer.write(buildJsonArray {
            for (record in records) {
                add(write(record))
            }
        }.toString())
    }
}