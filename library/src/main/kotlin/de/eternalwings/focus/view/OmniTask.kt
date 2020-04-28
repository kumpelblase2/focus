package de.eternalwings.focus.view

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.data.Task
import java.time.ZonedDateTime

data class OmniTask(
    override val id: String,
    val inbox: Boolean,
    override val parent: OmniTasklike?, // TODO is this really always a project?
    override val creation: Creation,
    override val name: String,
    override val note: String,
    override val rank: Long?,
    override val dropped: ZonedDateTime?,
    override val contexts: Set<OmniContext>,
    override val deferred: ZonedDateTime?,
    override val due: ZonedDateTime?,
    override val completed: ZonedDateTime?,
    override val estimatedMinutes: Long?,
    override val actionOrder: String,
    override val flagged: Boolean,
    override val completedByChildren: Boolean,
    val repetitionRule: String?,
    val repeat: String?,
    val repetitionMethod: RepetitionMethod,
    override val modified: ZonedDateTime?
) : OmniTasklike() {

    constructor(other: Task, resolveContext: (Reference) -> OmniContext, resolveParent: (String) -> OmniTasklike) : this(
        other.id,
        other.inbox ?: false,
        other.parent?.id?.let(resolveParent),
        other.toCreation()!!,
        other.name!!,
        other.note ?: "",
        other.rank,
        other.hidden,
        other.allContexts.map(resolveContext).toSet(),
        other.start,
        other.due,
        other.completed,
        other.estimatedMinutes,
        other.actionOrder ?: "sequential",
        other.flagged ?: false,
        other.completedByChildren ?: false,
        other.repetitionRule,
        other.repeat,
        other.repetitionMethod?.let { RepetitionMethod.fromString(it) } ?: RepetitionMethod.FIXED,
        other.modified
    )

    val isCompleted: Boolean
        get() = this.completed != null || this.dropped != null

    override val blocked: Boolean by lazy {
        contexts.any { it.prohibitsNextAction } || parent?.blocked ?: false
    }

    override fun toTask(): Task {
        return Task(
            this.id,
            null,
            this.inbox,
            this.parent?.let { Reference(it.id) },
            this.name,
            this.note,
            this.rank,
            this.dropped,
            null, // TODO
            emptySet(),
            this.deferred,
            this.due,
            this.completed,
            this.estimatedMinutes,
            this.creation.creationTime,
            this.creation.order,
            this.actionOrder,
            this.flagged,
            this.completedByChildren,
            this.repetitionRule,
            this.repeat,
            this.repetitionMethod.name,
            this.modified,
            Operation.UPDATE
        )
    }

    companion object {
        fun create() {

        }
    }
}
