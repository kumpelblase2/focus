package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.mergeInto
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element
import java.time.ZonedDateTime

/**
 * Creation of modification information for an Alarm/Reminder.
 */
data class Alarm(
    override val id: String,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val task: Reference?,
    val kind: String?,
    val variant: String?,
    val fireAt: ZonedDateTime?,
    val repeatInterval: Long?
) : ChangesetElement, WithOperation, WithCreationTimestamp,
    Mergeable<Alarm> {

    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Alarm): Alarm {
        return Alarm(
            id,
            other.added ?: added,
            other.order ?: order,
            other.task.mergeInto(task),
            other.kind ?: kind,
            other.variant ?: variant,
            other.fireAt ?: fireAt,
            other.repeatInterval ?: repeatInterval
        )
    }

    override fun toXML(): Element {
        return Element(TAG_NAME, XmlConstants.NAMESPACE).also {
            if (operation != Operation.CREATE) {
                it.setAttribute("op", operation.name.toLowerCase())
            }
            it.setAttribute("id", id)

            check(added != null) { "An alarm changeset entry always has an added date." }
            val elem = dateElement("added", added)
            order?.let { elem.setAttribute("order", order.toString()) }
            it.addContent(elem)

            task?.let { task -> it.addContent(referenceElement("task", task)) }
            kind?.let { kind -> it.addContent(textElement("kind", kind)) }
            variant?.let { variant -> it.addContent(textElement("variant", variant)) }
            fireAt?.let { fireAt -> it.addContent(dateElement("fire-date", fireAt)) }
            repeatInterval?.let { interval -> it.addContent(longElement("repeat-interval", interval)) }
        }
    }

    companion object {
        const val TAG_NAME = "alarm"

        fun fromXML(element: Element): Alarm {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE

            val id = element.getAttribute("id").value
            val addedElement = element.child("added")
            val added = addedElement?.value?.date()
            val addedOrder = addedElement?.attr("order")?.toLong()
            val task = element.reference("task")
            val kind = element.text("kind")
            val variant = element.text("variant")?.ifEmpty { null }
            val fireDate = element.text("fire-date")?.date()
            val repeatInterval = element.long("repeat-interval") ?: 0
            return Alarm(id, added, addedOrder, task, kind, variant, fireDate, repeatInterval).also { it.operation = operation }
        }
    }
}
