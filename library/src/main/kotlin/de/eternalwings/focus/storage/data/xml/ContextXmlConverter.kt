package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Context
import de.eternalwings.focus.storage.data.Location
import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element

object ContextXmlConverter : BaseXmlElementConverter<Context>("context") {
    const val TAG_NAME = "context"

    override fun fillXmlElement(source: Context, container: Element) {
        source.parentContext?.let { container.addContent(referenceElement("context", it)) }
        source.name?.let { container.addContent(textElement("name", it)) }
        source.note?.let { container.addContent(textElement("note", it)) }
        source.rank?.let { container.addContent(longElement("rank", it)) }
        source.hidden?.let { container.addContent(booleanElement("hidden", it)) }
        source.prohibitsNextAction?.let { container.addContent(booleanElement("prohibits-next-action", it)) }
        source.location?.let { container.addContent(LocationXmlConverter.write(it)) }
        source.tasksUserOrdered?.let { container.addContent(booleanElement("tasks-user-ordered", it)) }
    }

    override fun readValues(id: String, operation: Operation, container: Element): Context {
        val parent = container.reference("context")
        val addedElement = container.child("added")
        val added = addedElement?.value?.date()
        val addedOrder = addedElement?.attr("order")?.toLong()
        val name = container.text("name")
        val note = container.htmlText("note")
        val rank = container.long("rank")
        val hidden = container.boolean("hidden")
        val prohibitsNextAction = container.boolean("prohibits-next-action")
        val location = container.child(LocationXmlConverter.TAG_NAME)?.toLocation()
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
        return LocationXmlConverter.read(this)
    }
}
