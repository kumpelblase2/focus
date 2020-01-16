package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.storage.data.*

class OmniFocusState(private val storage: OmniStorage) {
    var contexts: List<OmniContext> = emptyList()
    var folder: List<OmniFolder> = emptyList()
    var tasks: List<OmniTasklike> = emptyList()

    init {
        val tasks = mutableListOf<OmniTasklike>()
        val contexts = mutableListOf<OmniContext>()
        val folders = mutableListOf<OmniFolder>()

        storage.changeSets.forEach { changeset ->
            changeset.container.content.forEach { item ->
                when (item) {
                    is Context -> applyToList(contexts, item) { OmniContext(it) }
                    is Folder -> applyToList(folders, item) { OmniFolder(it) }
                    is Task -> applyToList(tasks, item) {
                        if(it.project != null) {
                            OmniProject(it)
                        } else {
                            OmniTask(it)
                        }
                    }
                }
            }
        }

        this.folder = folders
        this.contexts = contexts
        this.tasks = tasks
    }

    private fun <T, R> applyToList(
        list: MutableList<T>,
        item: R,
        creator: (R) -> T
    ) where T : Mergeable<T, R>, T : Referencable, R : WithOperation, R : Referencable {
        when (item.operation) {
            Operation.DELETE -> list.removeIf { it.id == item.id }
            Operation.CREATE -> list.add(creator(item))
            Operation.UPDATE -> {
                val existing = list.find { it.id == item.id }
                    ?: throw IllegalStateException("Couldn't find existing ${item::class.java} to merge the update into.")
                existing.mergeFrom(item)
            }
            else -> throw NotImplementedError("Operation ${item.operation} not implemented for ${item::class.java}.")
        }
    }
}
