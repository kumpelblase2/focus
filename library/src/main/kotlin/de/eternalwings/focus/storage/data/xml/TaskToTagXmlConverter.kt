package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.data.TaskToTag
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element

object TaskToTagXmlConverter : BaseXmlElementConverter<TaskToTag>("task-to-tag") {
    const val TAG_NAME = "task-to-tag"

    override fun fillXmlElement(source: TaskToTag, container: Element) {
        source.task?.let { container.addContent(referenceElement("task", it)) }
        source.context?.let { container.addContent(referenceElement("context", it)) }
        source.rankInTask?.let { container.addContent(textElement("rank-in-task", it)) }
        source.rankInTag?.let { container.addContent(textElement("rank-in-tag", it)) }
    }

    override fun readValues(id: String, operation: Operation, container: Element): TaskToTag {
        val added = container.date("added")
        val order = container.child("added")?.attr("order")?.toLong()
        val task = container.reference("task")
        val context = container.reference("context")
        val rankInTask = container.text("rank-in-task")
        val rankInTag = container.text("rank-in-tag")

        return TaskToTag(
            id,
            added,
            order,
            task,
            context,
            rankInTask,
            rankInTag
        ).also { it.operation = operation }
    }
}
