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
            val addedElement = element.getChild("added", OmniContainer.NAMESPACE)
            val added = LocalDateTime.parse(addedElement.value, OmniContainer.TIME_FORMAT)
            val addedOrder = addedElement.getAttribute("order")?.longValue
            val name = element.text("name")
            val note = element.text("note")
            val rank = element.long("rank")
            val hidden = element.boolean("hidden")
            val modified = element.date("modified")
            return Folder(id, parent, added, addedOrder, name, note, rank, hidden, modified, operation)
        }
    }
}
