package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.mergeInto
import java.time.ZonedDateTime

/**
 * Creation of modification information for an Alarm/Reminder.
 */
data class Alarm(
    override val id: String,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val task: Reference?,
    val kind: String?,
    val variant: String?,
    val fireAt: ZonedDateTime?,
    val repeatInterval: Long?
) : ChangesetElement, WithOperation, WithCreationTimestamp, Mergeable<Alarm> {

    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Alarm): Alarm {
        return Alarm(
            id,
            other.added ?: added,
            other.order ?: order,
            other.task.mergeInto(task),
            other.kind ?: kind,
            other.variant ?: variant,
            other.fireAt ?: fireAt,
            other.repeatInterval ?: repeatInterval
        )
    }

}
