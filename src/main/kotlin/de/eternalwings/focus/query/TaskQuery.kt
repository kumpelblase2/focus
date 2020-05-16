package de.eternalwings.focus.query

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.view.OmniProject
import de.eternalwings.focus.view.OmniTask
import de.eternalwings.focus.view.OmniTasklike
import java.time.ZonedDateTime

typealias TaskFilter = (OmniTasklike) -> Boolean

data class TaskQuery(
    private val parts: List<TaskQueryPart>
) {
    private val queryFunction: TaskFilter

    init {
        val filters = parts.map { it.asFilter() }
        queryFunction = if (filters.isEmpty()) {
            { true }
        } else {
            filters.reduce { acc, current ->
                { task -> acc(task) && current(task) }
            }
        }
    }

    fun <T : OmniTasklike> eval(tasks: List<T>): List<T> {
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

                if (current is OmniProject) {
                    current.name == name
                } else {
                    false
                }
            }
        }
    }

    data class ShortcutPart(val name: String) : TaskQueryPart() {
        override fun asFilter(): TaskFilter {
            val perspectives = Configuration.instance.perspectives
            val query = perspectives[name]
            if (query != null) {
                val innerQuery = QueryParser.tryParseToEnd(query)
                when (innerQuery) {
                    is Parsed -> {
                        val filter = innerQuery.value
                        return {
                            val result = filter.eval(listOf(it))
                            result.size == 1
                        }
                    }
                    is ErrorResult -> throw IllegalStateException("Query for perspective was invalid.")
                }
            } else {
                return shortcuts[name] ?: throw IllegalStateException("Couldn't find shortcut $name")
            }
        }
    }

    data class PropertyPart(val name: String, val value: String) : TaskQueryPart() {
        override fun asFilter(): TaskFilter {
            val filterFunction =
                properties[name] ?: throw IllegalStateException("Property $name does not exist on a task!")

            return { task -> filterFunction(task, value) }
        }
    }

    companion object {
        val shortcuts: Map<String, TaskFilter> = mapOf(
            "available" to { task ->
                !task.isCompleted && !task.blocked && !task.isStillDeferred
            },
            "inbox" to { task -> task is OmniTask && task.inbox },
            "flagged" to { task -> task.flagged },
            "due" to { task ->
                task.due?.isBefore(ZonedDateTime.now()) ?: false
            },
            "dropped" to { task ->
                task.dropped != null
            },
            "review" to { task ->
                val project = when(task) {
                    is OmniProject -> task
                    is OmniTask -> task.parentProject
                    else -> null
                }
                project?.needsReview ?: false
            }
        )

        val properties: Map<String, (OmniTasklike, String) -> Boolean> = mapOf(
            "name" to { task, value -> task.name == value },
            "note" to { task, value -> task.note.contains(value, true) },
            "due" to { task, value ->
                val due = task.due
                if(due != null) {
                    val date = TemporalComparison.fromString(value)
                    date.compareTo(due)
                } else {
                    false
                }
            },
            "created" to { task, value ->
                val date = TemporalComparison.fromString(value)
                date.compareTo(task.creation.creationTime)
            },
            "modified" to { task, value ->
                val modified = task.modified
                if(modified != null) {
                    val date = TemporalComparison.fromString(value)
                    date.compareTo(modified)
                } else {
                    false
                }
            },
            "context" to { task, value ->
                task.contexts.any { it.name == value }
            },
            "deferred" to { task, value ->
                val deferred = task.deferred
                if (deferred != null) {
                    val date = TemporalComparison.fromString(value)
                    date.compareTo(deferred)
                } else {
                    false
                }
            }
        )
    }
}
