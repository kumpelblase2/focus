package de.eternalwings.focus

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.Parsed
import de.eternalwings.focus.query.QueryParser
import de.eternalwings.focus.storage.EncryptedOmniStorage
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.view.OmniFocusState
import java.nio.file.Paths

fun main(vararg args: String) {
    val path = args.firstOrNull() ?: System.getProperty("user.dir")
    val storage = OmniStorage.fromPath(Paths.get(path)) as EncryptedOmniStorage
    storage.providePassword(args[1].toCharArray())
    val view = OmniFocusState(storage)

    val query = QueryParser.tryParseToEnd(args[2])
    val result = when(query) {
        is Parsed -> query.value.eval(view)
        else -> emptyList()
    }

    if(result.isEmpty()) {
        println("No tasks found")
    } else {
        result.forEach { println(it) }
    }
}
