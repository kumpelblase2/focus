package de.eternalwings.focus

import de.eternalwings.focus.storage.EncryptedOmniStorage
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.view.OmniFocusState
import java.nio.file.Paths

fun main(vararg args: String) {
    val path = args.firstOrNull() ?: System.getProperty("user.dir")
    val storage = OmniStorage.fromPath(Paths.get(path)) as EncryptedOmniStorage
    val devices = storage.devices
    devices.forEach { println(it) }
    val capabilities = storage.capabilities
    capabilities.forEach { println(it) }
    storage.providePassword(args[1].toCharArray())
    val content = storage.changeSets
    content.forEach {
        val references = it.container.content
        println("Elements: " + references.size)
    }

    val view = OmniFocusState(storage)
    println(view.contexts)
    println(view.folder)
    view.tasks.forEach { task ->
        println(task)
    }
}
