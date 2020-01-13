package de.eternalwings.focus.storage.data

import org.jdom2.Element
import java.time.LocalDateTime

data class TaskToTag(
    override val id: String,
    override val added: LocalDateTime?,
    override val order: Long?,
    val task: Reference?,
    val context: Reference?,
    val rankInTask: String?, // is a hex though
    val rankInTag: String?,
    override val operation: Operation = Operation.CREATE
) : Referencable, WithOperation, WithCreationTimestamp {
    companion object {
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
                rankInTag,
                operation
            )
        }
    }
}
