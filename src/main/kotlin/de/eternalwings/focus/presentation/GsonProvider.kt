package de.eternalwings.focus.presentation

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.eternalwings.focus.Reference
import de.eternalwings.focus.view.OmniTask
import de.eternalwings.focus.view.OmniTasklike
import net.dongliu.gson.GsonJava8TypeAdapterFactory

object GsonProvider {
    val INSTANCE: Gson =
        GsonBuilder().setPrettyPrinting()
            .registerTypeAdapterFactory(GsonJava8TypeAdapterFactory())
            .registerTypeAdapter(Reference::class.java, ReferenceJsonSerializer)
            .registerTypeAdapterFactory(ReferenceTypeAdapterFactory(OmniTask::class.java, setOf("parent")))
            .registerTypeAdapterFactory(ReferenceTypeAdapterFactory(OmniTasklike::class.java, setOf("parent")))
            .addSerializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipClass(clazz: Class<*>?) = false

                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    if (f == null) return true
                    return f.name.contains("\$delegate")
                }
            })
            .create()
}
