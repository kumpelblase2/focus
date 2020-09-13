package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Alarm
import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element

object AlarmXmlConverter : BaseXmlElementConverter<Alarm>("alarm") {
    const val TAG_NAME = "alarm"

    override fun fillXmlElement(source: Alarm, container: Element) {
        source.task?.let { task -> container.addContent(referenceElement("task", task)) }
        source.kind?.let { kind -> container.addContent(textElement("kind", kind)) }
        source.variant?.let { variant -> container.addContent(textElement("variant", variant)) }
        source.fireAt?.let { fireAt -> container.addContent(dateElement("fire-date", fireAt)) }
        source.repeatInterval?.let { interval -> container.addContent(longElement("repeat-interval", interval)) }
    }

    override fun readValues(id: String, operation: Operation, container: Element): Alarm {
        val addedElement = container.child("added")
        val added = addedElement?.value?.date()
        val addedOrder = addedElement?.attr("order")?.toLong()
        val task = container.reference("task")
        val kind = container.text("kind")
        val variant = container.text("variant")?.ifEmpty { null }
        val fireDate = container.text("fire-date")?.date()
        val repeatInterval = container.long("repeat-interval") ?: 0
        return Alarm(id, added, addedOrder, task, kind, variant, fireDate, repeatInterval).also {
            it.operation = operation
        }
    }
}
