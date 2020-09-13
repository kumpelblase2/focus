package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.plist.PlistObject
import java.time.ZonedDateTime

data class Perspective(
    override val id: String,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val content: PlistObject<*>?
    // TODO missing icon attachment
) : ChangesetElement, WithCreationTimestamp, WithOperation, Mergeable<Perspective> {

    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Perspective): Perspective {
        return Perspective(
            id,
            other.added ?: added,
            other.order ?: order,
            other.content ?: content
        )
    }
}
