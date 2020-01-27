package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Context
import java.time.LocalDateTime

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
    val modificationTime: LocalDateTime? = null,
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
        context.location?.toOmniLocation(),
        context.modified,
        context.tasksUserOrdered ?: true
    )
}