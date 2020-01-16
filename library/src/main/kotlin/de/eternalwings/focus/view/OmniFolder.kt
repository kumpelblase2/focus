package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Folder
import java.time.LocalDateTime

data class OmniFolder(
    override val id: String,
    val parent: Reference?,
    val creation: Creation,
    val name: String,
    val note: String = "",
    val rank: Long? = null,
    val hidden: Boolean = false,
    val modified: LocalDateTime? = null
): Referencable, Mergeable<OmniFolder,Folder> {

    constructor(folder: Folder) : this(
        folder.id,
        folder.parent,
        folder.toCreation()!!,
        folder.name!!,
        folder.note ?: "",
        folder.rank,
        folder.hidden ?: false,
        folder.modified
    )

    override fun mergeFrom(other: Folder): OmniFolder {
        return OmniFolder(
            id,
            other.parent ?: parent,
            other.toCreation() ?: creation,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.modified ?: modified
        )
    }
}
