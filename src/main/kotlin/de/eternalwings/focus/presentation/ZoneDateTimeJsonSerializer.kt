package de.eternalwings.focus.presentation

import com.google.gson.*
import de.eternalwings.focus.storage.xml.XmlConstants
import java.lang.reflect.Type
import java.time.ZonedDateTime

object ZoneDateTimeJsonSerializer : JsonSerializer<ZonedDateTime> {
    override fun serialize(src: ZonedDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val dateTime = src ?: return JsonNull.INSTANCE
        return JsonPrimitive(XmlConstants.TIME_FORMAT.format(dateTime))
    }

}
