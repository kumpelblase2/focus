package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.plist.DateObject
import de.eternalwings.focus.storage.plist.DictionaryObject
import de.eternalwings.focus.storage.plist.StringObject
import java.time.OffsetDateTime

data class OmniCapability(
    val enabledOn: OffsetDateTime,
    val enabledBy: String,
    val name: String,
    val origin: String
) {

    fun toPlist(): DictionaryObject {
        return DictionaryObject(mapOf(
            "dateEnabled" to DateObject(enabledOn),
            "enablingClientIdentifier" to StringObject(enabledBy),
            "name" to StringObject(name),
            "origin" to StringObject(origin)
        ))
    }

    companion object {
        fun fromPlist(plist: DictionaryObject): OmniCapability {
            return with(plist.content) {
                OmniCapability(
                    this["dateEnabled"]!!.get(),
                    this["enablingClientIdentifier"]!!.get(),
                    this["name"]!!.get(),
                    this["origin"]!!.get()
                )
            }
        }
    }
}
