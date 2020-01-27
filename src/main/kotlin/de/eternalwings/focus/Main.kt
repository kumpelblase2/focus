package de.eternalwings.focus

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.Parsed
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.MissingRequiredPositionalArgumentException
import com.xenomachina.argparser.ShowHelpException
import de.eternalwings.focus.query.QueryParser
import de.eternalwings.focus.storage.EncryptedOmniStorage
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.view.OmniFocusState
import de.eternalwings.focus.view.OmniTask
import java.nio.file.Paths

fun main(vararg args: String) {

    val prologue = """
        This program can read and filter elements from an omnifocus database. If it's encrypted, 
        a password needs to be provided by the user.
        
        The QUERY allows filtering the found tasks to show only the ones interested in. You can 
        find more information about the query language at BLA.
    """.trimIndent()

    try {
        val parsed =
            ArgParser(args, helpFormatter = DefaultHelpFormatter(prologue = prologue)).parseInto(::CliArguments)

        val path = parsed.location
        val storage = OmniStorage.fromPath(Paths.get(path))
        if (storage is EncryptedOmniStorage) {
            val password = if (parsed.readPassword) {
                System.`in`.bufferedReader().readLine().toCharArray()
            } else {
                parsed.password.toCharArray()
            }
            storage.providePassword(password)
        }
        val view = OmniFocusState(storage)
        val taskInstances = view.tasks.filterIsInstance<OmniTask>()
        val tasks = if(!parsed.includeCompleted) taskInstances.filter { !it.isCompleted } else taskInstances

        val query = QueryParser.tryParseToEnd(parsed.query)
        val result = when (query) {
            is Parsed -> query.value.eval(tasks)
            else -> emptyList()
        }

        if (result.isEmpty()) {
            println("No tasks found")
        } else {
            result.forEach { println(it) }
        }
    } catch (showHelp: ShowHelpException) {
        showHelp.printAndExit("focus")
    } catch (missingParam: MissingRequiredPositionalArgumentException) {
        missingParam.printAndExit("focus")
    }
}
