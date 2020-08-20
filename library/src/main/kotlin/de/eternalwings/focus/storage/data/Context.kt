package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.mergeInto
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element
import java.time.ZonedDateTime

data class Context(
    override val id: String,
    val parentContext: Reference?,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val name: String?,
    val note: String?,
    override val rank: Long?,
    override val hidden: Boolean?,
    val prohibitsNextAction: Boolean?,
    val location: Location?,
    override val modified: ZonedDateTime?,
    val tasksUserOrdered: Boolean?
) : BaseChangesetElement(), WithCreationTimestamp, WithModificationTimestamp, WithRank, CanHide, WithOperation,
    Mergeable<Context> {

    override val tagName = TAG_NAME
    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Context): Context {
        return Context(
            id,
            other.parentContext.mergeInto(parentContext),
            other.added ?: added,
            other.order ?: order,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.prohibitsNextAction ?: prohibitsNextAction,
            other.location ?: location,
            other.modified ?: modified,
            other.tasksUserOrdered ?: tasksUserOrdered
        )
    }

    override fun fillXmlElement(element: Element) {
        parentContext?.let { element.addContent(referenceElement("context", it)) }
        name?.let { element.addContent(textElement("name", it)) }
        note?.let { element.addContent(textElement("note", it)) }
        rank?.let { element.addContent(longElement("rank", it)) }
        hidden?.let { element.addContent(booleanElement("hidden", it)) }
        prohibitsNextAction?.let { element.addContent(booleanElement("prohibits-next-action", it)) }
        location?.let { element.addContent(it.toXML()) }
        tasksUserOrdered?.let { element.addContent(booleanElement("tasks-user-ordered", it)) }
    }

    companion object {
        const val TAG_NAME = "context"

        fun fromXML(element: Element): Context {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE
            val id = element.attr("id")!!
            val container = if (operation == Operation.REFERENCE) element.child("reference-snapshot")!! else element
            val parent = container.reference("context")
            val addedElement = container.child("added")
            val added = addedElement?.value?.date()
            val addedOrder = addedElement?.attr("order")?.toLong()
            val name = container.text("name")
            val note = container.htmlText("note")
            val rank = container.long("rank")
            val hidden = container.boolean("hidden")
            val prohibitsNextAction = container.boolean("prohibits-next-action")
            val location = container.child("location")?.toLocation()
            val modified = container.date("modified")
            val tasksUserOrdered = container.boolean("tasks-user-ordered")
            return Context(
                id,
                parent,
                added,
                addedOrder,
                name,
                note,
                rank,
                hidden,
                prohibitsNextAction,
                location,
                modified,
                tasksUserOrdered
            ).also { it.operation = operation }
        }

        private fun Element.toLocation(): Location? {
            if (!this.hasAttributes()) return null
            return Location.fromXML(this)
        }
    }
}
