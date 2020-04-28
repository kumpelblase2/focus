package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.failWith
import de.eternalwings.focus.presentation.TaskListPrinter
import de.eternalwings.focus.query.QueryParser
import de.eternalwings.focus.view.OmniFocusState
import de.eternalwings.focus.view.OmniTask

class QueryCommand : UnlockedStorageBasedCommand(name = "query", help = "Query the tasks in the omnifocus store") {
    val includeCompleted by option("-C", "--show-completed", help = "Include completed tasks").flag()
    val total by option("-t", "--total", help = "Display the total amount of tasks").flag()
    val query by argument("query", help = "The query to select tasks").default("")
    val json by option("-j", "--json", help = "Print as json").flag()

    override fun run() {
        val storage = getUnlockedStorage()
        val view = OmniFocusState(storage)
        val taskInstances = view.tasks.filterIsInstance<OmniTask>()
        val tasks = if (!includeCompleted) taskInstances.filter { !it.isCompleted } else taskInstances

        val query = QueryParser.tryParseToEnd(query)
        val result = when (query) {
            is Parsed -> query.value.eval(tasks)
            is ErrorResult -> {
                failWith(
                    "The provided query for tasks is invalid.",
                    ErrorCodes.INVALID_QUERY
                )
            }
        }

        if (result.isNotEmpty()) {
            if (json) {
                TaskListPrinter.printJson(result)
            } else {
                TaskListPrinter.print(result)
            }
        }

        if (total) {
            println("Total: ${result.size}/${taskInstances.size} Tasks.")
        }
    }
}
