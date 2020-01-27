package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.xml.*
import de.eternalwings.focus.storage.xml.XmlConstants.TIME_FORMAT
import org.jdom2.Element
import java.time.LocalDateTime

data class Context(
    override val id: String,
    val parentContext: Reference?,
    override val added: LocalDateTime?,
    override val order: Long?,
    val name: String?,
    val note: String?,
    override val rank: Long?,
    override val hidden: Boolean?,
    val prohibitsNextAction: Boolean?,
    val location: Location?,
    override val modified: LocalDateTime?,
    val tasksUserOrdered: Boolean?,
    override val operation: Operation = Operation.CREATE
) : Referencable, WithCreationTimestamp, WithModificationTimestamp, WithRank, CanHide, WithOperation,
    Mergeable<Context, Context> {

    override fun mergeFrom(other: Context): Context {
        return Context(
            id,
            other.parentContext ?: parentContext,
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

    companion object {
        fun fromXML(element: Element): Context {
            val id = element.attr("id")!!
            val parent = element.reference("context")
            val addedElement = element.child("added")
            val added = addedElement?.value?.asDateTime()
            val addedOrder = addedElement?.attr("order")?.toLong()
            val name = element.text("name")
            val note = element.htmlText("note")
            val rank = element.long("rank")
            val hidden = element.boolean("hidden")
            val prohibitsNextAction = element.boolean("prohibits-next-action")
            val location = element.child("location")?.toLocation()
            val modified = element.date("modified")
            val tasksUserOrdered = element.boolean("tasks-user-ordered")
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE
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
                tasksUserOrdered,
                operation
            )
        }

        private fun Element.toLocation(): Location? {
            if (!this.hasAttributes()) return null
            return Location.fromXML(this)
        }

        private fun String.asDateTime(): LocalDateTime? {
            if (this.isEmpty()) return null
            return LocalDateTime.parse(this, TIME_FORMAT)
        }
    }
}
