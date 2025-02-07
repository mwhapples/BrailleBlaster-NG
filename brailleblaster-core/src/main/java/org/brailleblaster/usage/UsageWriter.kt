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

import com.google.gson.Gson
import com.google.gson.stream.JsonWriter
import java.io.Writer
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

interface UsageWriter {
    fun write(record: UsageRecord, writer: Writer)
    fun write(records: Iterable<UsageRecord>, writer: Writer)
}
class JsonUsageWriter : UsageWriter {
    private val gson = Gson()
    private fun write(record: UsageRecord, writer: JsonWriter) {
        writer.beginObject()
        writer.name("time").value(formatter.format(record.time))
        writer.name("tool").value(record.tool)
        writer.name("event").value(record.event)
        writer.name("msg").value(record.message)
        writer.endObject()
    }

    override fun write(record: UsageRecord, writer: Writer) {
        write(record, gson.newJsonWriter(writer))
    }

    override fun write(records: Iterable<UsageRecord>, writer: Writer) {
            val jsonWriter = gson.newJsonWriter(writer)
            jsonWriter.beginArray()
            for (record in records) {
                write(record, jsonWriter)
            }
            jsonWriter.endArray()
    }
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss.SSS", Locale.US).withZone(ZoneOffset.ofHours(0))
    }
}