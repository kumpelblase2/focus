package de.eternalwings.focus.presentation

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.eternalwings.focus.Reference
import de.eternalwings.focus.view.OmniFolder
import de.eternalwings.focus.view.OmniProject
import de.eternalwings.focus.view.OmniTask
import de.eternalwings.focus.view.OmniTasklike
import java.time.ZonedDateTime

object TaskListPrinter {
    private val table: Table<OmniTasklike>
    private val gson: Gson =
        GsonBuilder().setPrettyPrinting().registerTypeAdapter(ZonedDateTime::class.java, ZoneDateTimeJsonSerializer)
            .registerTypeAdapter(Reference::class.java, ReferenceJsonSerializer)
            .registerTypeAdapterFactory(ReferenceTypeAdapterFactory(OmniTask::class.java, setOf("parent")))
            .registerTypeAdapterFactory(ReferenceTypeAdapterFactory(OmniTasklike::class.java, setOf("parent")))
            .addSerializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipClass(clazz: Class<*>?) = false

                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    if(f == null) return true
                    return f.name.contains("\$delegate")
                }
            })
            .create()

    init {
        table =
            Table() {
                header("ID", 11, 13) { it.id }
                header("Name", 40) { it.name }
                header("Parent", 6, 40) { task -> task.parents.joinToString("->") { it.name } }
                header("Contexts", 7, 15) { it.contexts.joinToString { context -> context.name } }
                header("Note", 10) { it.note.replace("\n", "").trim() }
                header("Due", 18) { it.due }
                header("Deferred", 18) { it.deferred }
                header("Completed", 18) { it.completed }
            }
    }

    fun print(tasks: List<OmniTasklike>) {
        print(table.print(tasks))
    }

    fun printJson(tasks: List<OmniTasklike>) {
        println(gson.toJson(tasks))
    }

}

data class Header<T>(val name: String, val minWidth: Int, val maxWidth: Int, val resolver: (T) -> Any?)
data class HeaderData(val content: List<String>, val width: Int)

class Table<T>(init: Table<T>.() -> Unit = {}) {
    private val headers: MutableList<Header<T>> = mutableListOf()
    var nullFormatter: () -> String = { "" }
    var shortenFunction: (String, Int) -> String = { string, max -> string.substring(0, max - 3) + "..." }

    init {
        this.init()
    }

    fun header(name: String, minWidth: Int = 10, maxWidth: Int = minWidth, resolver: (T) -> Any?) {
        headers.add(Header(name, minWidth, maxWidth, resolver))
    }

    fun print(data: List<T>): String {
        val buffer = StringBuilder()
        val headerContent = headers.map { header ->
            val dataForColumn = data.map(header.resolver).map { value ->
                value?.toString() ?: nullFormatter()
            }

            val maxWidth = dataForColumn.map { it.length }.max() ?: 0
            val selectedWidth = maxWidth.coerceIn(header.minWidth, header.maxWidth)

            val trimmedToMaxLength = dataForColumn.map {
                when {
                    it.length > selectedWidth -> shortenFunction(it, selectedWidth)
                    it.length < selectedWidth -> it.padEnd(selectedWidth, ' ')
                    else -> it
                }
            }

            HeaderData(trimmedToMaxLength, selectedWidth)
        }

        buffer.append("|")
        for (i in headers.indices) {
            val width = headerContent[i].width
            buffer.append(' ')
            buffer.append(headers[i].name.padEnd(width + 1, ' '))
            buffer.append("|")
        }
        buffer.append("\n")

        buffer.append("+")
        for (i in headers.indices) {
            val width = headerContent[i].width
            buffer.append("-".repeat(width + 2))
            buffer.append("+")
        }
        buffer.append("\n")

        for (i in data.indices) {
            buffer.append("| ")
            for (j in headers.indices) {
                val value = headerContent[j].content[i]
                buffer.append(value)
                buffer.append(" | ")
            }
            buffer.setLength(buffer.length - 1)
            buffer.append("\n")
        }

        return buffer.toString()
    }
}
