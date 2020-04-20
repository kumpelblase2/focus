package de.eternalwings.focus

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class CliArguments(parser: ArgParser) {

    val password by parser.storing("-p", "--password", help = "The password for the omnifocus storage").default("")

    val readPassword by parser.flagging("-P", "--ask-password", help = "Provide password secretly")

    val includeCompleted by parser.flagging("-C", "--show-completed", help = "Include completed tasks")

    val total by parser.flagging("-t", "--total", help = "Display total tasks")

    val location by parser.positional("The location for the omnifocus file storage")

    val query by parser.positional("The query to select tasks").default("")

    val json by parser.flagging("-j", "--json", help = "Print as json")

}
