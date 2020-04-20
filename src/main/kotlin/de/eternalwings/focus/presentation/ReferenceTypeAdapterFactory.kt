package de.eternalwings.focus.presentation

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import de.eternalwings.focus.Referencable

class ReferenceTypeAdapterFactory<S : Referencable>(
    private val type: Class<S>,
    private val fields: Set<String> = emptySet()
) : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        return if (type.rawType == this.type) {
            customizedTypeAdapter(gson, type as TypeToken<S>) as TypeAdapter<T>
        } else {
            null
        }
    }

    private fun customizedTypeAdapter(gson: Gson, type: TypeToken<S>): TypeAdapter<S> {
        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)
        return object : TypeAdapter<S>() {
            override fun write(out: JsonWriter, value: S?) {
                val jsonTree = delegate.toJsonTree(value)
                if(jsonTree.isJsonObject) {
                    removeParentReferences(jsonTree)
                }
                elementAdapter.write(out, jsonTree)
            }

            override fun read(input: JsonReader): S {
                throw NotImplementedError("Reading is not supported.")
            }
        }
    }

    private fun removeParentReferences(jsonTree: JsonElement) {
        fields.forEach { prop ->
            val propElement = jsonTree.asJsonObject.get(prop)
            if(propElement != null && !propElement.isJsonNull) {
                val idProperty = propElement.asJsonObject.get("id")
                jsonTree.asJsonObject.add(prop, idProperty)
            }
        }
    }
}
