package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.plist.DictionaryObject
import java.time.OffsetDateTime

data class OmniCapability(
    val enabledOn: OffsetDateTime,
    val enabledBy: String,
    val name: String,
    val origin: String
) {
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
