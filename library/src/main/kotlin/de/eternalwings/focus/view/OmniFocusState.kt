package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.storage.data.*

class OmniFocusState(private val storage: OmniStorage) {
    var contexts: List<OmniContext> = emptyList()
    var folder: List<OmniFolder> = emptyList()
    var tasks: List<OmniTasklike> = emptyList()

    val byId: Map<String,Referencable> by lazy {
        val allElements: List<Referencable> = (contexts as List<Referencable> + folder + tasks)
        allElements.map { it.id to it }.toMap()
    }

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
                    is TaskToTag -> {
                        when(item.operation) {
                            Operation.CREATE -> {
                                tasks.update({it.id == item.task!!.id}) {
                                    it.copyWithContexts(setOf(item.context!!))
                                }
                            }
                            Operation.REFERENCE -> {}
                            else -> throw NotImplementedError("Operation ${item.operation} not implemented for ${item::class.java}.")
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
                list.update({ it.id == item.id }) {
                    it.mergeFrom(item)
                }
            }
            Operation.REFERENCE -> {} // technically we should handle this, but for now it's irrelevant
            else -> throw NotImplementedError("Operation ${item.operation} not implemented for ${item::class.java}.")
        }
    }

    private fun <T> MutableList<T>.update(finder: (T) -> Boolean, update: (T) -> T) {
        val index = this.indexOfFirst(finder)
        if(index >= 0) {
            val element = this[index]
            val newElement = update(element)
            this[index] = newElement
        } else {
            throw IllegalStateException("Couldn't update element")
        }
    }
}
