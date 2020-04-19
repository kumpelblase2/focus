package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.storage.data.Folder
import java.time.ZonedDateTime

data class OmniFolder(
    override val id: String,
    val parent: OmniFolder?,
    val creation: Creation,
    val name: String,
    val note: String = "",
    val rank: Long? = null,
    val hidden: Boolean = false,
    val modified: ZonedDateTime? = null
) : Referencable {

    constructor(folder: Folder, resolveParent: (String) -> OmniFolder) : this(
        folder.id,
        folder.parent?.id?.let(resolveParent),
        folder.toCreation()!!,
        folder.name!!,
        folder.note ?: "",
        folder.rank,
        folder.hidden ?: false,
        folder.modified
    )
}
