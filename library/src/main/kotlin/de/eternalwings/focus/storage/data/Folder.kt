package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.mergeInto
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element
import java.time.ZonedDateTime

data class Folder(
    override val id: String,
    val parent: Reference?,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val name: String?,
    val note: String?,
    override val rank: Long?,
    override val hidden: Boolean?,
    override val modified: ZonedDateTime?
) : ChangesetElement, WithOperation, WithCreationTimestamp, WithModificationTimestamp, WithRank, CanHide,
    Mergeable<Folder> {

    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Folder): Folder {
        return Folder(
            id,
            other.parent.mergeInto(parent),
            other.added ?: added,
            other.order ?: order,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.modified ?: modified
        )
    }
}
