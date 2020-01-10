package de.eternalwings.focus.storage.data

import org.jdom2.Element
import java.time.LocalDateTime

data class Task(
    override val id: String,
    val project: Project?,
    val inbox: Boolean?,
    val parent: Reference?,
    val name: String?,
    val note: String?, // TODO not a string
    override val rank: Long?,
    val hidden: LocalDateTime?,
    val context: Reference?,
    val start: LocalDateTime?,
    val due: LocalDateTime?,
    val completed: LocalDateTime?,
    val estimatedMinutes: Long?,
    override val added: LocalDateTime?,
    override val order: Long?,
    val actionOrder: String?, // TODO this can be an enum of 'parallel','sequential',...
    val flagged: Boolean?,
    val completedByChildren: Boolean?,
    val repetitionRule: String?,
    val repeat: String?,
    val repetitionMethod: String?, // TODO can be an enum
    override val modified: LocalDateTime?,
    override val operation: Operation = Operation.CREATE
) : Referencable, WithOperation, WithCreationTimestamp, WithModificationTimestamp, WithRank {
    companion object {
        fun fromXML(element: Element): Task {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE

            val id = element.attr("id")!!
            val inbox = element.boolean("inbox")
            val project = element.child("project")?.asProject()
            val addedElement = element.child("added")
            val added = addedElement?.value?.asDateTime()
            val addedOrder = addedElement?.attr("order")?.toLong()
            val parent = element.reference("task")
            val name = element.text("name")
            val note = element.text("note")
            val rank = element.long("rank")
            val contextReference = element.reference("context")
            val start = element.date("start")
            val actionOrder = element.text("order")
            val hidden = element.date("hidden")
            val due = element.date("due")
            val completed = element.date("completed")
            val flagged = element.boolean("flagged")
            val completedByChildren = element.boolean("completed-by-children")!!
            val repetitionRule = element.text("repetition-rule")
            val repetitionMethod = element.text("repetition-method")
            val repeat = element.text("repeat")
            val modified = element.date("modified")
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
                modified,
                operation
            )
        }

        private fun Element.asProject(): Project? {
            if(this.children.size == 0) return null
            return Project.fromXML(this)
        }

        private fun String.asDateTime(): LocalDateTime? {
            if (this.isEmpty()) return null
            return LocalDateTime.parse(this, OmniContainer.TIME_FORMAT)
        }
    }
}
