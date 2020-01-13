package de.eternalwings.focus.storage.data

import org.jdom2.Element
import java.time.LocalDateTime

data class Folder(
    override val id: String,
    val parent: Reference?,
    override val added: LocalDateTime?,
    override val order: Long?,
    val name: String?,
    val note: String?,
    override val rank: Long?,
    override val hidden: Boolean?,
    override val modified: LocalDateTime?,
    override val operation: Operation = Operation.CREATE
) : Referencable, WithOperation, WithCreationTimestamp, WithModificationTimestamp, WithRank, CanHide {
    companion object {
        fun fromXML(element: Element): Folder {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE
            val id = element.attr("id")!!
            val parent = element.reference("folder")
            val added = element.date("added")
            val order = element.child("added")?.attr("order")?.toLong()
            val name = element.text("name")
            val note = element.text("note")
            val rank = element.long("rank")
            val hidden = element.boolean("hidden")
            val modified = element.date("modified")
            return Folder(id, parent, added, order, name, note, rank, hidden, modified, operation)
        }
    }
}
