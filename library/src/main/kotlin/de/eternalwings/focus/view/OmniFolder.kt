package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Folder
import de.eternalwings.focus.storage.data.Operation
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

    fun toFolder(): Folder {
        return Folder(
            id,
            parent?.let { Reference(it.id) },
            creation.creationTime,
            creation.order,
            name,
            note,
            rank,
            hidden,
            modified,
            Operation.CREATE
        )
    }

    fun toFolder(olderVersion: OmniFolder): Folder {
        return Folder(
            id,
            diffForReference(parent, olderVersion.parent),
            olderVersion.creation.creationTime,
            olderVersion.creation.order,
            if (name != olderVersion.name) name else null,
            if (note != olderVersion.note) note else null,
            if (rank != olderVersion.rank) rank else null,
            if (hidden != olderVersion.hidden) hidden else null,
            if (modified != olderVersion.modified) modified else null,
            Operation.UPDATE
        )
    }

    fun diffForReference(myReference: Referencable?, reference: Referencable?): Reference? {
        return if (myReference == null) {
            reference?.let { Reference() }
        } else {
            if (reference != myReference) {
                Reference(myReference.id)
            } else {
                null
            }
        }
    }
}
