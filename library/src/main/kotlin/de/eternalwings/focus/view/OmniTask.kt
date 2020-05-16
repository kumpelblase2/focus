package de.eternalwings.focus.view

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.data.Task
import java.time.ZonedDateTime

data class OmniTask(
    override val id: String,
    val inbox: Boolean,
    override val parent: OmniTasklike?,
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

    val parentProject: OmniProject?
        get() {
            return when (parent) {
                is OmniProject -> parent
                is OmniTask -> parent.parentProject
                else -> null
            }
        }

    constructor(other: Task, resolveContext: (Reference) -> OmniContext, resolveParent: (String) -> OmniTasklike) : this(
        other.id,
        other.inbox ?: false,
        other.parent?.id?.let(resolveParent),
        other.toCreation()!!,
        other.name!!,
        other.note ?: "",
        other.rank,
        other.hidden,
        other.allContexts.filter { it.id != null }.map(resolveContext).toSet(),
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

    constructor(builder: Builder) : this(
        builder.id,
        builder.inbox,
        builder.parent,
        builder.creation,
        builder.name,
        builder.note,
        builder.rank,
        builder.dropped,
        builder.contexts,
        builder.deferred,
        builder.due,
        builder.completed,
        builder.estimatedMinutes,
        builder.actionOrder,
        builder.flagged,
        builder.completedByChildren,
        builder.repetitionRule,
        builder.repeat,
        builder.repetitionMethod,
        builder.modified
    )

    override val isCompleted: Boolean
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
            Operation.CREATE
        )
    }

    fun toTask(previousVersion: OmniTask): Task {
        return Task(
            id,
            null,
            if(previousVersion.inbox != inbox) inbox else null,
            if(previousVersion.parent?.id != parent?.id) parent?.let { Reference(id) } else null,
            if(previousVersion.name != name) name else null,
            if(previousVersion.note != note) note else null,
            if(previousVersion.rank != rank) rank else null,
            if(previousVersion.dropped != dropped) dropped else null,
            null,// TODO
            emptySet(),
            if(previousVersion.deferred != deferred) deferred else null,
            if(previousVersion.due != due) due else null,
            if(previousVersion.completed != completed) completed else null,
            if(previousVersion.estimatedMinutes != estimatedMinutes) estimatedMinutes else null,
            creation.creationTime,
            creation.order,
            if(previousVersion.actionOrder != actionOrder) actionOrder else null,
            if(previousVersion.flagged != flagged) flagged else null,
            if(previousVersion.completedByChildren != completedByChildren) completedByChildren else null,
            if(previousVersion.repetitionRule != repetitionRule) repetitionRule else null,
            if(previousVersion.repeat != repeat) repeat else null,
            if(previousVersion.repetitionMethod != repetitionMethod) repetitionMethod.toString() else null,
            if(previousVersion.modified != modified) modified else null,
            Operation.UPDATE
        )
    }

    companion object {
        fun create(id: String, name: String, setup: Builder.() -> Unit = {}): OmniTask {
            val builder = Builder(id, name)
            builder.setup()
            return OmniTask(builder)
        }
    }

    class Builder(var id: String, var name: String) {
        var inbox: Boolean = false
        var parent: OmniTasklike? = null
        var creation: Creation = Creation(ZonedDateTime.now(), null)
        var note: String = ""
        var rank: Long? = null
        var dropped: ZonedDateTime? = null
        var contexts: Set<OmniContext> = emptySet()
        var deferred: ZonedDateTime? = null
        var due: ZonedDateTime? = null
        var completed: ZonedDateTime? = null
        var estimatedMinutes: Long? = null
        var actionOrder: String = "sequential"
        var flagged: Boolean = false
        var completedByChildren: Boolean = false
        var repetitionRule: String? = null
        var repeat: String? = null
        var repetitionMethod: RepetitionMethod = RepetitionMethod.FIXED
        var modified: ZonedDateTime? = null
    }
}
