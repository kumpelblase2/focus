package de.eternalwings.focus.view

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Project
import de.eternalwings.focus.storage.data.Task
import java.time.LocalDateTime

data class OmniProject(
    override val id: String,
    val project: Project,
    override val parent: Reference?,
    override val creation: Creation,
    override val name: String,
    override val note: String,
    override val rank: Long?,
    override val hidden: LocalDateTime?,
    override val contexts: Set<Reference>,
    override val start: LocalDateTime?,
    override val due: LocalDateTime?,
    override val completed: LocalDateTime?,
    override val estimatedMinutes: Long?,
    override val actionOrder: String,
    override val flagged: Boolean,
    override val completedByChildren: Boolean,
    override val repetitionRule: String?,
    override val repeat: String?,
    override val repetitionMethod: String?,
    override val modified: LocalDateTime?
) : OmniTasklike() {

    constructor(other: Task): this(
        other.id,
        other.project!!,
        other.parent,
        other.toCreation()!!,
        other.name!!,
        other.note ?: "",
        other.rank,
        other.hidden,
        other.context?.let { setOf(it) } ?: emptySet(),
        other.start,
        other.due,
        other.completed,
        other.estimatedMinutes,
        other.actionOrder ?: "sequential",
        other.flagged ?: false,
        other.completedByChildren ?: false,
        other.repetitionRule,
        other.repeat,
        other.repetitionMethod,
        other.modified
    )

    override fun mergeFrom(other: Task): OmniProject {
        return OmniProject(
            id,
            other.project ?: project,
            other.parent ?: parent,
            other.toCreation() ?: creation,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            contexts + (other.context?.let { setOf(it) } ?: emptySet()),
            other.start ?: start,
            other.due ?: due,
            other.completed ?: completed,
            other.estimatedMinutes ?: estimatedMinutes,
            other.actionOrder ?: actionOrder,
            other.flagged ?: flagged,
            other.completedByChildren ?: completedByChildren,
            other.repetitionRule ?: repetitionRule,
            other.repeat ?: repeat,
            other.repetitionMethod ?: repetitionMethod,
            other.modified ?: modified
        )
    }

    override fun copyWithContexts(newContexts: Collection<Reference>): OmniTasklike {
        return this.copy(contexts = this.contexts + newContexts)
    }
}
