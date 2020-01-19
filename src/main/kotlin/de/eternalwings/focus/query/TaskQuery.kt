package de.eternalwings.focus.query

import de.eternalwings.focus.view.*
import java.lang.IllegalStateException

typealias TaskFilter = (OmniTask, OmniFocusState) -> Boolean

data class TaskQuery(
    val parts: List<TaskQueryPart>
) {
    private val queryFunction: TaskFilter

    init {
        queryFunction = parts.map { it.asFilter() }.reduce { acc, current ->
            { task: OmniTask, state: OmniFocusState -> acc(task, state) && current(task, state) }
        }
    }

    fun eval(view: OmniFocusState): List<OmniTask> {
        val tasks = view.tasks.filterIsInstance<OmniTask>()
        return tasks.filter { queryFunction(it, view) }
    }
}

sealed class TaskQueryPart {
    abstract fun asFilter(): TaskFilter

    data class ContextPart(val name: String) : TaskQueryPart() {
        override fun asFilter(): TaskFilter {
            return filter@{ task, state ->
                val contextIds = task.contexts
                val contexts = contextIds.map { state.byId[it.id] }.filterIsInstance<OmniContext>()
                contexts.any { it.name == name }
            }
        }
    }
    data class ProjectPart(val name: String) : TaskQueryPart() {
        override fun asFilter(): TaskFilter {
            return filter@{ task, state ->
                var current: OmniTasklike? = task
                while (current != null && current !is OmniProject) {
                    current = current.parent?.let { state.byId[it.id] as? OmniTasklike }
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
            "available" to { task, state ->
                val contextReferences = task.contexts
                val contexts = contextReferences.map { state.byId[it.id] }.filterIsInstance<OmniContext>()
                !task.isCompleted && contexts.none { it.prohibitsNextAction }
            },
            "inbox" to { task, state ->
                task.inbox
            }
        )
    }
}
