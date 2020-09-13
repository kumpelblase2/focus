package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.plist.PlistObject
import java.time.ZonedDateTime

data class Setting(
    override val id: String,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val content: PlistObject<*>?
) : ChangesetElement, WithCreationTimestamp, Mergeable<Setting> {

    override fun mergeFrom(other: Setting): Setting {
        return Setting(
            id,
            other.added ?: added,
            other.order ?: order,
            other.content ?: content
        )
    }
}
