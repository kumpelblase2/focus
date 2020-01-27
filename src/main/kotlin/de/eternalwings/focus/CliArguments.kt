package de.eternalwings.focus

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class CliArguments(parser: ArgParser) {

    val password by parser.storing("-p", "--password", help = "The password for the omnifocus storage").default("")

    val readPassword by parser.flagging("-P", help = "Provide password secretly")

    val includeCompleted by parser.flagging("-C", "--show-completed", help = "Include completed tasks")

    val location by parser.positional("The location for the omnifocus file storage")

    val query by parser.positional("The query to select tasks").default("")

}
