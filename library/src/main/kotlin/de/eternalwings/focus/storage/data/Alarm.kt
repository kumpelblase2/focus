package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.asReference
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element
import java.time.ZonedDateTime

data class Alarm(
    override val id: String,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val task: Reference?,
    val kind: String?,
    val variant: String?,
    val fireAt: ZonedDateTime?,
    val repeatInterval: Long?,
    override val operation: Operation = Operation.CREATE
) : Referencable, WithOperation, WithCreationTimestamp,
    Mergeable<Alarm, Alarm> {

    override fun mergeFrom(other: Alarm): Alarm {
        return Alarm(
            id,
            other.added ?: added,
            other.order ?: order,
            other.task ?: task,
            other.kind ?: kind,
            other.variant ?: variant,
            other.fireAt ?: fireAt,
            other.repeatInterval ?: repeatInterval
        )
    }

    companion object {
        fun fromXML(element: Element): Alarm {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE

            val id = element.getAttribute("id").value
            val addedElement = element.child("added")
            val added = addedElement?.value?.date()
            val addedOrder = addedElement?.attr("order")?.toLong()
            val task = element.child("task")?.attr("idref")?.asReference()
            val kind = element.text("kind")
            val variant = element.text("variant")?.ifEmpty { null }
            val fireDate = element.text("fire-date")?.date()
            val repeatInterval = element.long("repeat-interval") ?: 0
            return Alarm(id, added, addedOrder, task, kind, variant, fireDate, repeatInterval, operation)
        }
    }
}
