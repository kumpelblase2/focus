package de.eternalwings.focus.view

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Project
import de.eternalwings.focus.storage.data.Task
import java.lang.IllegalStateException
import java.time.LocalDateTime

data class OmniTask(
    override val id: String,
    val project: Project?,
    val inbox: Boolean,
    override val parent: Reference?,
    override val creation: Creation,
    override val name: String,
    override val note: String,
    override val rank: Long?,
    override val hidden: LocalDateTime?,
    override val context: Reference?,
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

    constructor(other: Task) : this(
        other.id,
        other.project,
        other.inbox ?: false,
        other.parent,
        other.toCreation()!!,
        other.name!!,
        other.note ?: "",
        other.rank,
        other.hidden,
        other.context,
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

    override fun mergeFrom(other: Task): OmniTask {
        if(other.project != null) {
            // TODO can a task be converted to a project?
        }

        return OmniTask(
            id,
            other.project ?: project,
            other.inbox ?: inbox,
            other.parent ?: parent,
            other.toCreation() ?: creation,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.context ?: context,
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
}
