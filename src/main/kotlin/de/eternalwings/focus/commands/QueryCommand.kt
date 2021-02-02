package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.presentation.DataPrinter
import de.eternalwings.focus.presentation.JsonDataPrinter
import de.eternalwings.focus.presentation.TableTaskPrinter
import de.eternalwings.focus.query.QueryParser
import de.eternalwings.focus.view.OmniFocusState
import de.eternalwings.focus.view.OmniTask
import de.eternalwings.focus.view.OmniTasklike

class QueryCommand : UnlockedStorageBasedCommand(
    name = "query", help = "Query the tasks in the omnifocus store", epilog = """
    Querying allows searching the whole omnifocus database for task-like elements.
    This includes not just tasks, but also projects, because they're basically the
    same thing to omnifocus.
    
    This uses a simple query language to allow filtering for contexts, projects 
    and properties of tasks. All tasks that match the criteria in the query will
    be displayed.
    
    Select a specific context:
        `@context` or `@{context with spaces}`
    Select a specific project:
        `#project` or `#{project with spaces}`
    Select with a specific property:
        `name:TaskName` or `name:{value with space}`
    Select with a predefined shortcut:
        `available`

    You can then chain multiple together by separating them with a space. A full
    query could thus look like this:
        `#{Migrate to new Setup} @Home available`
        -> Select all tasks in the project "Migrate to new Setup" which have the
           context "Home" and are available
""".trimIndent()
) {
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
            val printer: DataPrinter<OmniTasklike> = when {
                json -> JsonDataPrinter as DataPrinter<OmniTasklike>
                else -> TableTaskPrinter
            }
            printer.print(result)
        }

        if (total) {
            println("Total: ${result.size}/${taskInstances.size} Tasks.")
        }

        if (Configuration.instance.options.updateLastIds) {
            val deviceId = Configuration.instance.device ?: return
            val device = storage.findDeviceById(deviceId) ?: return
            storage.updateDevice(device)
        }
    }
}
