package de.eternalwings.focus.storage.data

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
) : Referencable, WithCreationTimestamp, WithModificationTimestamp, WithRank, CanHide, WithOperation {
    companion object {
        fun fromXML(element: Element): Context {
            val id = element.attr("id")!!
            val parent = element.reference("context")
            val addedElement = element.child("added")
            val added = addedElement?.value?.asDateTime()
            val addedOrder = addedElement?.attr("order")?.toLong()
            val name = element.text("name")
            val note = element.text("note")
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
            return LocalDateTime.parse(this, OmniContainer.TIME_FORMAT)
        }
    }
}

data class Location(
    var name: String,
    var latitude: String,
    var longitude: String,
    val radius: Int,
    var notificationFlags: Short
) {
    companion object {
        fun fromXML(element: Element): Location {
            val address = element.attr("name")!!
            val latitude = element.attr("latitude")!!
            val longitude = element.attr("longitude")!!
            val radius = element.attr("radius")?.toInt() ?: 100
            val notificationFlags = element.attr("notificationFlags")!!.toShort()
            return Location(
                address,
                latitude,
                longitude,
                radius,
                notificationFlags
            )
        }
    }
}
