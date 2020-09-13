package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import java.time.ZonedDateTime

data class TaskToTag(
    override val id: String, // id is always in the format "task-id.context-id"
    override val added: ZonedDateTime?,
    override val order: Long?,
    val task: Reference?,
    val context: Reference?,
    val rankInTask: String?, // is a hex though
    val rankInTag: String?
) : ChangesetElement, WithOperation, WithCreationTimestamp {

    override var operation: Operation = Operation.CREATE

}
