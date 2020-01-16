package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Context
import java.time.LocalDateTime

data class OmniContext(
    override val id: String,
    val parent: Reference?,
    val creation: Creation,
    val name: String,
    val note: String = "",
    val rank: Long? = null,
    val hidden: Boolean = false,
    val prohibitsNextAction: Boolean = false,
    val location: OmniLocation? = null,
    val modificationTime: LocalDateTime? = null,
    val tasksUserOrdered: Boolean = true
) : Referencable, Mergeable<OmniContext,Context> {

    constructor(context: Context) : this(
        context.id,
        context.parentContext,
        context.toCreation()!!,
        context.name!!,
        context.note ?: "",
        context.rank,
        context.hidden ?: false,
        context.prohibitsNextAction ?: false,
        context.location?.toOmniLocation(),
        context.modified,
        context.tasksUserOrdered ?: true
    )

    override fun mergeFrom(other: Context): OmniContext {
        return OmniContext(
            id,
            other.parentContext ?: parent,
            other.toCreation() ?: creation,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.prohibitsNextAction ?: prohibitsNextAction,
            other.location?.toOmniLocation() ?: location,
            other.modified ?: modificationTime,
            other.tasksUserOrdered ?: tasksUserOrdered
        )
    }
}
