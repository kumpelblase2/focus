package de.eternalwings.focus

import de.eternalwings.focus.config.Configuration
import java.util.*
import kotlin.system.exitProcess

fun failWith(message: String, code: Int = 1): Nothing {
    System.err.println(message)
    exitProcess(code)
}

fun warning(message: String) {
    System.err.println(message)
}

fun debug(message: String) {
    if(Configuration.instance.debug) {
        println("[debug] $message")
    }
}

fun debug(message: () -> String) {
    if (Configuration.instance.debug) {
        println("[debug] ${message()}")
    }
}

fun prompt(message: String): Boolean {
    return Scanner(System.`in`).use {
        var response: Boolean? = null
        while(response == null) {
            print(message)
            when(it.nextLine()) {
                "yes", "y" -> response = true
                "no", "n" -> response = false
            }
        }

        response
    }
}
