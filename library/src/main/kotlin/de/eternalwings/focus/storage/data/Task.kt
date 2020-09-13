package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.mergeInto
import java.time.ZonedDateTime

data class Task(
    override val id: String,
    val project: Project?,
    val inbox: Boolean?,
    val parent: Reference?,
    val name: String?,
    val note: String?, // TODO not a string but actually html
    override val rank: Long?,
    val hidden: ZonedDateTime?,
    val context: Reference?,
    val additionalContexts: Set<Reference>,
    val start: ZonedDateTime?,
    val due: ZonedDateTime?,
    val completed: ZonedDateTime?,
    val estimatedMinutes: Long?,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val actionOrder: String?, // TODO this can be an enum of 'parallel','sequential',...
    val flagged: Boolean?,
    val completedByChildren: Boolean?,
    val repetitionRule: String?,
    val repeat: String?,
    val repetitionMethod: String?, // TODO can be an enum
    override val modified: ZonedDateTime?
) : ChangesetElement, WithOperation, WithCreationTimestamp, WithModificationTimestamp, WithRank, Mergeable<Task> {

    val allContexts: Set<Reference>
        get() = if (context == null) emptySet() else setOf(context) + additionalContexts

    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Task): Task {
        return Task(
            id,
            if (project != null && other.project != null) project.mergeFrom(other.project) else other.project
                ?: project,
            other.inbox ?: inbox,
            other.parent.mergeInto(parent),
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.context.mergeInto(context),
            additionalContexts,
            other.start ?: start,
            other.due ?: due,
            other.completed ?: completed,
            other.estimatedMinutes ?: estimatedMinutes,
            other.added ?: added,
            other.order ?: order,
            other.actionOrder ?: actionOrder,
            other.flagged ?: flagged,
            other.completedByChildren ?: completedByChildren,
            other.repetitionRule ?: repetitionRule,
            other.repeat ?: repeat,
            other.repetitionMethod ?: repetitionMethod,
            other.modified ?: modified
        )
    }

    fun copyWithContexts(newContexts: Collection<Reference>): Task {
        return this.copy(additionalContexts = additionalContexts + newContexts)
    }

    fun copyWithContext(newContext: Reference): Task = copyWithContexts(setOf(newContext))
}
