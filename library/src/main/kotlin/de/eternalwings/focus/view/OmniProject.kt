package de.eternalwings.focus.view

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Task
import java.time.ZonedDateTime

data class OmniProject(
    override val id: String,
    val project: ProjectDefinition,
    override val parent: OmniProject?,
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
    override val modified: ZonedDateTime?
) : OmniTasklike() {

    constructor(other: Task, resolveContext: (Reference) -> OmniContext, resolveParent: (String) -> OmniProject) : this(
        other.id,
        ProjectDefinition(other.project!!),
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
        other.modified
    )

    override val blocked: Boolean by lazy {
        contexts.any { it.prohibitsNextAction }
    }
}
