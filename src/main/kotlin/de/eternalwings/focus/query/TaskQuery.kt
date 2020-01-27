package de.eternalwings.focus.query

import de.eternalwings.focus.view.*
import java.lang.IllegalStateException
import java.time.LocalDateTime

typealias TaskFilter = (OmniTask) -> Boolean

data class TaskQuery(
    val parts: List<TaskQueryPart>
) {
    private val queryFunction: TaskFilter

    init {
        val filters = parts.map { it.asFilter() }
        queryFunction = if(filters.isEmpty()) {
            { omniTask -> true }
        } else {
            filters.reduce { acc, current ->
                { task -> acc(task) && current(task) }
            }
        }
    }

    fun eval(tasks: List<OmniTask>): List<OmniTask> {
        return tasks.filter { queryFunction(it) }
    }
}

sealed class TaskQueryPart {
    abstract fun asFilter(): TaskFilter

    data class ContextPart(val name: String) : TaskQueryPart() {
        override fun asFilter(): TaskFilter {
            return filter@{ task ->
                task.contexts.any { it.name == name }
            }
        }
    }
    data class ProjectPart(val name: String) : TaskQueryPart() {
        override fun asFilter(): TaskFilter {
            return filter@{ task ->
                var current: OmniTasklike? = task
                while (current != null && current !is OmniProject) {
                    current = current.parent
                }

                if(current is OmniProject) {
                    current.name == name
                } else {
                    false
                }
            }
        }
    }
    data class ShortcutPart(val name: String) : TaskQueryPart() {
        override fun asFilter(): TaskFilter {
            return shortcuts[name] ?: throw IllegalStateException("Couldn't find shortcut $name")
        }
    }
    data class PropertyPart(val name: String, val value: String) : TaskQueryPart() {
        override fun asFilter(): TaskFilter {
            TODO()
        }
    }

    companion object {
        val shortcuts: Map<String,TaskFilter> = mapOf(
            "available" to { task ->
                !task.isCompleted && !task.blocked
            },
            "inbox" to { task ->
                task.inbox
            },
            "flagged" to { task ->
                task.flagged
            },
            "due" to { task ->
                task.due?.isBefore(LocalDateTime.now()) ?: false
            }
        )
    }
}
