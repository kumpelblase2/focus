package de.eternalwings.focus.presentation

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.eternalwings.focus.Reference
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
