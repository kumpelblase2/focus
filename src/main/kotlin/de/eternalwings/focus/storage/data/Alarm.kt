package de.eternalwings.focus.storage.data

import org.jdom2.Element
import java.time.LocalDateTime

data class Alarm(
    override val id: String,
    override val added: LocalDateTime,
    override val order: Long?,
    val task: Reference,
    val kind: String,
    val variant: String?,
    val fireAt: LocalDateTime,
    val repeatInterval: Long,
    override val operation: Operation = Operation.CREATE
) : Referencable, WithOperation, WithCreationTimestamp {
    companion object {
        fun fromXML(element: Element): Alarm {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE

            val id = element.getAttribute("id").value
            val addedElement = element.getChild("added", OmniContainer.NAMESPACE)
            val added = LocalDateTime.parse(addedElement.value, OmniContainer.TIME_FORMAT)
            val addedOrder = addedElement.getAttribute("order")?.longValue
            val task = element.getChild("task", OmniContainer.NAMESPACE).getAttribute("idref").value.asReference()
            val kind = element.getChildText("kind", OmniContainer.NAMESPACE)
            val variant = element.getChildText("variant")?.ifEmpty { null }
            val fireDate = LocalDateTime.parse(
                element.getChildText("fire-date", OmniContainer.NAMESPACE),
                OmniContainer.TIME_FORMAT
            )
            val repeatInterval = element.getChild("repeat-interval", OmniContainer.NAMESPACE)?.value?.toLong() ?: 0
            return Alarm(id, added, addedOrder, task, kind, variant, fireDate, repeatInterval, operation)
        }
    }
}
