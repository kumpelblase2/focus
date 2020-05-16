package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.query.QueryParser

class PerspectiveCreateCommand : CliktCommand(name = "create", help = "Create a new perspective that can be referenced later.") {

    val name by argument(help = "The name of the perspective.")
    val query by argument(help = "Query for tasks to match to be included in this perspective.")

    override fun run() {
        val existing = Configuration.instance.perspectives
        if(existing.containsKey(name)) {
            failWith("A perspective with such a name already exists.", ErrorCodes.GENERIC_ARGUMENT_ERROR)
        }

        val parsed = QueryParser.tryParseToEnd(query)
        if(parsed is ErrorResult) {
            failWith("The provided query does not parse.", ErrorCodes.INVALID_QUERY)
        }

        Configuration.instance.perspectives = existing + (name to query)
        Configuration.save()
    }

}
