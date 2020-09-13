package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.data.Project
import de.eternalwings.focus.storage.data.Task
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element

object TaskXmlConverter : BaseXmlElementConverter<Task>("task") {
    const val TAG_NAME = "task"

    override fun fillXmlElement(source: Task, container: Element) {
        source.inbox?.let { container.addContent(booleanElement("inbox", it)) }
        source.project?.let { container.addContent(ProjectXmlConverter.write(it)) }
        source.parent?.let { container.addContent(referenceElement("task", it)) }
        source.name?.let { container.addContent(textElement("name", it)) }
        source.note?.let { container.addContent(textElement("note", it)) }
        source.rank?.let { container.addContent(longElement("rank", it)) }
        source.context?.let { container.addContent(referenceElement("context", it)) }
        source.start?.let { container.addContent(dateElement("start", it)) }
        source.actionOrder?.let { container.addContent(textElement("order", it)) }
        source.hidden?.let { container.addContent(dateElement("hidden", it)) }
        source.due?.let { container.addContent(dateElement("due", it)) }
        source.completed?.let { container.addContent(dateElement("completed", it)) }
        source.flagged?.let { container.addContent(booleanElement("flagged", it)) }
        source.completedByChildren?.let { container.addContent(booleanElement("completed-by-children", it)) }
        source.repetitionRule?.let { container.addContent(textElement("repetition-rule", it)) }
        source.repetitionMethod?.let { container.addContent(textElement("repetition-method", it)) }
        source.repeat?.let { container.addContent(textElement("repeat", it)) }
    }

    override fun readValues(id: String, operation: Operation, container: Element): Task {
        val inbox = container.boolean("inbox")
        val project = container.child(ProjectXmlConverter.TAG_NAME)?.asProject()
        val addedElement = container.child("added")
        val added = addedElement?.value?.date()
        val addedOrder = addedElement?.attr("order")?.toLong()
        val parent = container.reference("task")
        val name = container.text("name")
        val note = container.htmlText("note")
        val rank = container.long("rank")
        val contextReference = container.reference("context")
        val start = container.date("start")
        val actionOrder = container.text("order")
        val hidden = container.date("hidden")
        val due = container.date("due")
        val completed = container.date("completed")
        val flagged = container.boolean("flagged")
        val completedByChildren = container.boolean("completed-by-children")
        val repetitionRule = container.text("repetition-rule")
        val repetitionMethod = container.text("repetition-method")
        val repeat = container.text("repeat")
        val modified = container.date("modified")
        return Task(
            id,
            project,
            inbox,
            parent,
            name,
            note,
            rank,
            hidden,
            contextReference,
            emptySet(),
            start,
            due,
            completed,
            null,
            added,
            addedOrder,
            actionOrder,
            flagged,
            completedByChildren,
            repetitionRule,
            repeat,
            repetitionMethod,
            modified
        ).also { it.operation = operation }
    }

    private fun Element.asProject(): Project? {
        if (this.children.size == 0) return null
        return ProjectXmlConverter.read(this)
    }
}
