package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element
import java.time.ZonedDateTime

data class TaskToTag(
    override val id: String, // id is always in the format "task-id.context-id"
    override val added: ZonedDateTime?,
    override val order: Long?,
    val task: Reference?,
    val context: Reference?,
    val rankInTask: String?, // is a hex though
    val rankInTag: String?
) : BaseChangesetElement(), WithOperation, WithCreationTimestamp {

    override val tagName = TAG_NAME
    override var operation: Operation = Operation.CREATE

    override fun fillXmlElement(element: Element) {
        task?.let { element.addContent(referenceElement("task", it)) }
        context?.let { element.addContent(referenceElement("context", it)) }
        rankInTask?.let { element.addContent(textElement("rank-in-task", it)) }
        rankInTag?.let { element.addContent(textElement("rank-in-tag", it)) }
    }

    companion object {
        const val TAG_NAME = "task-to-tag"

        fun fromXML(element: Element): TaskToTag {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE
            val id = element.attr("id")!!
            val added = element.date("added")
            val order = element.child("added")?.attr("order")?.toLong()
            val task = element.reference("task")
            val context = element.reference("context")
            val rankInTask = element.text("rank-in-task")
            val rankInTag = element.text("rank-in-tag")

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
}
