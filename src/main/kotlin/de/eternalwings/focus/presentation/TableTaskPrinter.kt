package de.eternalwings.focus.presentation

import de.eternalwings.focus.view.OmniTasklike

object TableTaskPrinter : DataPrinter<OmniTasklike> {
    private val table: Table<OmniTasklike> = Table() {
        header("ID", 11, 13) { it.id }
        header("Name", 40) { it.name }
        header("Parent", 6, 40) { task -> task.parents.joinToString("->") { it.name } }
        header("Contexts", 7, 15) { it.contexts.joinToString { context -> context.name } }
        header("Note", 10) { it.note.replace("\n", "").trim() }
        header("Due", 18) { it.due }
        header("Deferred", 18) { it.deferred }
        header("Completed", 18) { it.completed }
    }

    override fun print(data: List<OmniTasklike>) {
        print(table.print(data))
    }

}
