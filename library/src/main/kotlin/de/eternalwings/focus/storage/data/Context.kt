package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.mergeInto
import java.time.ZonedDateTime

data class Context(
    override val id: String,
    val parentContext: Reference?,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val name: String?,
    val note: String?,
    override val rank: Long?,
    override val hidden: Boolean?,
    val prohibitsNextAction: Boolean?,
    val location: Location?,
    override val modified: ZonedDateTime?,
    val tasksUserOrdered: Boolean?
) : ChangesetElement, WithCreationTimestamp, WithModificationTimestamp, WithRank, CanHide, WithOperation,
    Mergeable<Context> {

    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Context): Context {
        return Context(
            id,
            other.parentContext.mergeInto(parentContext),
            other.added ?: added,
            other.order ?: order,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.prohibitsNextAction ?: prohibitsNextAction,
            other.location ?: location,
            other.modified ?: modified,
            other.tasksUserOrdered ?: tasksUserOrdered
        )
    }
}
