package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.storage.data.Task
import java.time.ZonedDateTime

abstract class OmniTasklike : Referencable {
    abstract val parent: OmniTasklike?
    abstract val creation: Creation
    abstract val name: String
    abstract val note: String
    abstract val rank: Long?
    abstract val dropped: ZonedDateTime?
    abstract val contexts: Set<OmniContext>
    abstract val deferred: ZonedDateTime?
    abstract val due: ZonedDateTime?
    abstract val completed: ZonedDateTime?
    abstract val estimatedMinutes: Long?
    abstract val actionOrder: String
    abstract val flagged: Boolean
    abstract val completedByChildren: Boolean
    abstract val modified: ZonedDateTime?

    abstract val blocked: Boolean
    abstract val isCompleted: Boolean

    open val isAvailable: Boolean
        get() = !this.isCompleted && !isStillDeferred && !blocked

    val isStillDeferred: Boolean
        get() = (this.deferred?.isAfter(ZonedDateTime.now()) ?: false) || parent?.isStillDeferred ?: false

    val parents: List<OmniTasklike>
        get() {
            return if (parent == null) {
                emptyList()
            } else {
                val parentsParents = parent?.parents ?: emptyList()
                parentsParents + parent!!
            }
        }

    abstract fun toTask(): Task
}
