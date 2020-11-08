package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Folder
import de.eternalwings.focus.storage.data.Operation
import java.time.ZonedDateTime

class OmniFolder(
    override val id: String,
    parent: OmniFolder?,
    creation: Creation,
    name: String,
    note: String = "",
    rank: Long? = null,
    hidden: Boolean = false,
    modified: ZonedDateTime? = null
) : Referencable {

    var parent: OmniFolder? = parent
        private set
    var creation: Creation = creation
        private set
    var name: String = name
        private set
    var note: String = note
        private set
    var rank: Long? = rank
        private set
    var hidden: Boolean = hidden
        private set
    var modified: ZonedDateTime? = modified
        private set

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
            modified
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
            if (modified != olderVersion.modified) modified else null
        ).apply { operation = Operation.UPDATE }
    }

    private fun diffForReference(myReference: Referencable?, reference: Referencable?): Reference? {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OmniFolder) return false

        if (id != other.id) return false
        if (parent != other.parent) return false
        if (creation != other.creation) return false
        if (name != other.name) return false
        if (note != other.note) return false
        if (rank != other.rank) return false
        if (hidden != other.hidden) return false
        if (modified != other.modified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + creation.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + (rank?.hashCode() ?: 0)
        result = 31 * result + hidden.hashCode()
        result = 31 * result + (modified?.hashCode() ?: 0)
        return result
    }
}
