package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.mergeInto
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element
import java.time.ZonedDateTime

data class Folder(
    override val id: String,
    val parent: Reference?,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val name: String?,
    val note: String?,
    override val rank: Long?,
    override val hidden: Boolean?,
    override val modified: ZonedDateTime?
) : BaseChangesetElement(), WithOperation, WithCreationTimestamp, WithModificationTimestamp, WithRank, CanHide,
    Mergeable<Folder> {

    override val tagName = TAG_NAME
    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Folder): Folder {
        return Folder(
            id,
            other.parent.mergeInto(parent),
            other.added ?: added,
            other.order ?: order,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.modified ?: modified
        )
    }

    override fun fillXmlElement(element: Element) {
        parent?.let { element.addContent(referenceElement("folder", it)) }
        name?.let { element.addContent(textElement("name", it)) }
        note?.let { element.addContent(textElement("note", it)) }
        rank?.let { element.addContent(longElement("rank", it)) }
        hidden?.let { element.addContent(booleanElement("hidden", it)) }
    }

    companion object {
        const val TAG_NAME = "folder"

        fun fromXML(element: Element): Folder {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE
            val id = element.attr("id")!!
            val parent = element.reference("folder")
            val added = element.date("added")
            val order = element.child("added")?.attr("order")?.toLong()
            val name = element.text("name")
            val note = element.htmlText("note")
            val rank = element.long("rank")
            val hidden = element.boolean("hidden")
            val modified = element.date("modified")
            return Folder(id, parent, added, order, name, note, rank, hidden, modified).also { it.operation = operation }
        }
    }
}
