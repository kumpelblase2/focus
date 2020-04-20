package de.eternalwings.focus

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.MissingRequiredPositionalArgumentException
import com.xenomachina.argparser.ShowHelpException
import de.eternalwings.focus.presentation.TaskListPrinter
import de.eternalwings.focus.query.QueryParser
import de.eternalwings.focus.storage.EncryptedOmniStorage
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.view.OmniFocusState
import de.eternalwings.focus.view.OmniTask
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(vararg args: String) {

    val prologue = """
        This program can read and filter elements from an omnifocus database. If it's encrypted, 
        providing a password also allows the program to decrypt the database before and thus continue
        like normal.
        
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
                readPassword()
            } else {
                if (parsed.password.isEmpty()) {
                    System.err.println("The provided omnifocus storage is encrypted, but no password was given. Please provide the password using -p or -P")
                    exitProcess(1)
                }
                parsed.password.toCharArray()
            }
            storage.providePassword(password)
        }
        val view = OmniFocusState(storage)
        val taskInstances = view.tasks.filterIsInstance<OmniTask>()
        val tasks = if (!parsed.includeCompleted) taskInstances.filter { !it.isCompleted } else taskInstances

        val query = QueryParser.tryParseToEnd(parsed.query)
        val result = when (query) {
            is Parsed -> query.value.eval(tasks)
            is ErrorResult -> {
                System.err.println("Error parsing query: $query")
                exitProcess(1)
            }
        }

        if (result.isNotEmpty()) {
            if (parsed.json) {
                TaskListPrinter.printJson(result)
            } else {
                TaskListPrinter.print(result)
            }
        }

        if (parsed.total) {
            println("Total: ${result.size}/${taskInstances.size} Tasks.")
        }
    } catch (showHelp: ShowHelpException) {
        showHelp.printAndExit("focus")
    } catch (missingParam: MissingRequiredPositionalArgumentException) {
        missingParam.printAndExit("focus")
    }
}

fun readPassword(): CharArray {
    println("The provided omnifocus storage is encrypted, please provide the password below.")
    val console = System.console()
    return if (console != null) {
        console.readPassword("Password: ")
    } else {
        println("Couldn't get access to a console! The input cannot be masked!")
        print("Password: ")
        // We don't need to close the input stream here
        System.`in`.bufferedReader().readLine().toCharArray()
    }
}
