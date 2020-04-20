package de.eternalwings.focus.presentation

import com.google.gson.*
import de.eternalwings.focus.Reference
import java.lang.reflect.Type

object ReferenceJsonSerializer : JsonSerializer<Reference> {
    override fun serialize(src: Reference?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val ref = src ?: return JsonNull.INSTANCE
        return JsonPrimitive(ref.id)
    }

}
