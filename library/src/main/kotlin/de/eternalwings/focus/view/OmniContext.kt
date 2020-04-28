package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Context
import de.eternalwings.focus.storage.data.Operation
import java.time.ZonedDateTime

data class OmniContext(
    override val id: String,
    val parent: OmniContext?,
    val creation: Creation,
    val name: String,
    val note: String = "",
    val rank: Long? = null,
    val hidden: Boolean = false,
    val prohibitsNextAction: Boolean = false,
    val location: OmniLocation? = null,
    val modificationTime: ZonedDateTime? = null,
    val tasksUserOrdered: Boolean = true
) : Referencable {

    constructor(context: Context, resolveParent: (String) -> OmniContext) : this(
        context.id,
        context.parentContext?.id?.let(resolveParent),
        context.toCreation()!!,
        context.name!!,
        context.note ?: "",
        context.rank,
        context.hidden ?: false,
        context.prohibitsNextAction ?: false,
        context.location?.let { OmniLocation(it) },
        context.modified,
        context.tasksUserOrdered ?: true
    )

    fun toContext(): Context {
        return Context(
            this.id,
            this.parent?.let { Reference(it.id) },
            this.creation.creationTime,
            this.creation.order,
            this.name,
            this.note,
            this.rank,
            this.hidden,
            this.prohibitsNextAction,
            this.location?.toLocation(),
            this.modificationTime,
            this.tasksUserOrdered,
            Operation.UPDATE
        )
    }
}
