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

import com.google.gson.*
import java.lang.reflect.Type

/*
 * I don't know why GSON doesn't have this as an API, the same code is all over StackOverflow.  Oh well adding it to our code base
 */
class GsonInterfaceAdapter<T : Any> : JsonSerializer<T>, JsonDeserializer<T> {
    override fun serialize(
        jsonElement: T,
        type: Type,
        jsonSerializationContext: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty(CLASSNAME, jsonElement.javaClass.getName())
        jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement))
        return jsonObject
    }

    fun getObjectClass(className: String?): Type {
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            // e.printStackTrace();
            throw JsonParseException(e.message)
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement,
        arg1: Type,
        jsonDeserializationContext: JsonDeserializationContext
    ): T {
        val jsonObject = jsonElement.asJsonObject
        val prim = jsonObject[CLASSNAME] as JsonPrimitive
        val className = prim.asString
        val klass = getObjectClass(className)
        return jsonDeserializationContext.deserialize(jsonObject[DATA], klass)
    }

    companion object {
        private const val CLASSNAME = "CLASSNAME"
        private const val DATA = "DATA"
    }
}
