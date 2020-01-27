package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Task
import java.time.LocalDateTime

abstract class OmniTasklike : Referencable {
    abstract val parent: OmniTasklike?
    abstract val creation: Creation
    abstract val name: String
    abstract val note: String
    abstract val rank: Long?
    abstract val hidden: LocalDateTime?
    abstract val contexts: Set<OmniContext>
    abstract val start: LocalDateTime?
    abstract val due: LocalDateTime?
    abstract val completed: LocalDateTime?
    abstract val estimatedMinutes: Long?
    abstract val actionOrder: String
    abstract val flagged: Boolean
    abstract val completedByChildren: Boolean
    abstract val modified: LocalDateTime?

    abstract val blocked: Boolean
}
