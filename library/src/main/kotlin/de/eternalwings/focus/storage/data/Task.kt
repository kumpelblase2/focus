package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element
import java.time.ZonedDateTime

data class Task(
    override val id: String,
    val project: Project?,
    val inbox: Boolean?,
    val parent: Reference?,
    val name: String?,
    val note: String?, // TODO not a string
    override val rank: Long?,
    val hidden: ZonedDateTime?,
    val context: Reference?,
    val additionalContexts: Set<Reference>,
    val start: ZonedDateTime?,
    val due: ZonedDateTime?,
    val completed: ZonedDateTime?,
    val estimatedMinutes: Long?,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val actionOrder: String?, // TODO this can be an enum of 'parallel','sequential',...
    val flagged: Boolean?,
    val completedByChildren: Boolean?,
    val repetitionRule: String?,
    val repeat: String?,
    val repetitionMethod: String?, // TODO can be an enum
    override val modified: ZonedDateTime?,
    override val operation: Operation = Operation.CREATE
) : Referencable, WithOperation, WithCreationTimestamp, WithModificationTimestamp, WithRank,
    Mergeable<Task, Task> {

    val allContexts: Set<Reference>
        get() = if (context == null) emptySet() else setOf(context) + additionalContexts

    override fun mergeFrom(other: Task): Task {
        return Task(
            id,
            other.project ?: project,
            other.inbox ?: inbox,
            other.parent ?: parent,
            other.name ?: name,
            other.note ?: note,
            other.rank ?: rank,
            other.hidden ?: hidden,
            other.context ?: context,
            additionalContexts,
            other.start ?: start,
            other.due ?: due,
            other.completed ?: completed,
            other.estimatedMinutes ?: estimatedMinutes,
            other.added ?: added,
            other.order ?: order,
            other.actionOrder ?: actionOrder,
            other.flagged ?: flagged,
            other.completedByChildren ?: completedByChildren,
            other.repetitionRule ?: repetitionRule,
            other.repeat ?: repeat,
            other.repetitionMethod ?: repetitionMethod,
            other.modified ?: modified
        )
    }

    fun copyWithContexts(newContexts: Collection<Reference>): Task {
        return this.copy(additionalContexts = additionalContexts + newContexts)
    }

    fun copyWithContext(newContext: Reference): Task = copyWithContexts(setOf(newContext))

    companion object {
        fun fromXML(element: Element): Task {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE

            val id = element.attr("id")!!
            val inbox = element.boolean("inbox")
            val project = element.child("project")?.asProject()
            val addedElement = element.child("added")
            val added = addedElement?.value?.date()
            val addedOrder = addedElement?.attr("order")?.toLong()
            val parent = element.reference("task")
            val name = element.text("name")
            val note = element.htmlText("note")
            val rank = element.long("rank")
            val contextReference = element.reference("context")
            val start = element.date("start")
            val actionOrder = element.text("order")
            val hidden = element.date("hidden")
            val due = element.date("due")
            val completed = element.date("completed")
            val flagged = element.boolean("flagged")
            val completedByChildren = element.boolean("completed-by-children")
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
                modified,
                operation
            )
        }

        private fun Element.asProject(): Project? {
            if (this.children.size == 0) return null
            return Project.fromXML(this)
        }
    }
}
