package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.storage.data.*
import java.util.*

class OmniFocusState(private val storage: OmniStorage) {
    var contexts: List<OmniContext> = emptyList()
        private set
    var folder: List<OmniFolder> = emptyList()
        private set
    var tasks: List<OmniTasklike> = emptyList()
        private set

    val byId: Map<String, Referencable> by lazy {
        val allElements: List<Referencable> = contexts + folder + tasks
        allElements.map { it.id to it }.toMap()
    }

    init {
        val mergedTasks = mutableMapOf<String, Task>()
        val mergedContexts = mutableMapOf<String, Context>()
        val mergedFolders = mutableMapOf<String, Folder>()

        storage.changeSets.forEach { changeset ->
            changeset.container.content.forEach { item ->
                when (item) {
                    is Context -> applyToMap(mergedContexts, item)
                    is Folder -> applyToMap(mergedFolders, item)
                    is Task -> applyToMap(mergedTasks, item)
                    is TaskToTag -> {
                        when (item.operation) {
                            Operation.CREATE -> {
                                val id = item.task!!.id!!
                                val task = mergedTasks[id] ?: TODO()
                                mergedTasks[id] = task.copyWithContext(item.context!!)
                            }
                            Operation.REFERENCE -> {
                            }
                            else -> throw NotImplementedError("Operation ${item.operation} not implemented for ${item::class.java}.")
                        }
                    }
                }
            }
        }

        val dependenciesForFolders = createDependencyTree(mergedFolders) { it.parent?.id }
        this.folder = buildFromTree(dependenciesForFolders, mergedFolders) { folder, previous ->
            OmniFolder(folder) { previous[it]!! }
        }

        val dependenciesForContexts = createDependencyTree(mergedContexts) { it.parentContext?.id }
        this.contexts = buildFromTree(dependenciesForContexts, mergedContexts) { context, previous ->
            OmniContext(context) { previous[it]!! }
        }

        val dependenciesForTasks = createDependencyTree(mergedTasks) { it.parent?.id }
        this.tasks = buildFromTree(dependenciesForTasks, mergedTasks) { task, previous ->
            val resolveContext: (Reference) -> OmniContext = { context -> this.contexts.first { it.id == context.id } }
            if (task.project != null) {
                val resolveFolder: (String) -> OmniFolder = { folder -> this.folder.find { it.id == folder }!! }
                OmniProject(task, resolveContext, { previous[it] as OmniProject }, resolveFolder)
            } else {
                OmniTask(task, resolveContext) { previous[it] as OmniTasklike }
            }
        }
    }

    private fun <T, R> buildFromTree(
        dependencyTree: RootNode,
        references: Map<String, T>,
        creator: (T, MutableMap<String, R>) -> R
    ): List<R> {
        val buildQueue: Queue<DependencyTreeNode> = LinkedList(dependencyTree.elements)
        val built = mutableMapOf<String, R>()

        while (buildQueue.isNotEmpty()) {
            val current = buildQueue.poll()
            val rawData = references[current.id] ?: error("Data to convert not in original map")
            val created = creator(rawData, built)
            built[current.id] = created
            current.requirements.forEach { buildQueue.add(it) }
        }

        return built.values.toList()
    }

    private fun <T> createDependencyTree(
        mergedFolders: MutableMap<String, T>,
        dependentResolver: (T) -> String?
    ): RootNode {
        val dependants = mutableMapOf<String?, MutableSet<String>>()
        mergedFolders.forEach { (id, element) ->
            val existing = dependants.computeIfAbsent(dependentResolver(element)) { mutableSetOf() }
            existing.add(id)
        }

        return buildTreeFromMap(dependants)
    }

    private fun buildTreeFromMap(dependentMap: MutableMap<String?, MutableSet<String>>): RootNode {
        val root = dependentMap[null] ?: mutableSetOf()
        val noDependents = root.map { buildTreeFromMap(it, dependentMap) }
        return RootNode(noDependents)
    }

    private fun buildTreeFromMap(
        current: String,
        dependentMap: MutableMap<String?, MutableSet<String>>
    ): DependencyTreeNode {
        val dependents = dependentMap[current]
        return if (dependents == null) {
            DependencyTreeNode(current, emptyList())
        } else {
            DependencyTreeNode(current, dependents.map { buildTreeFromMap(it, dependentMap) })
        }
    }

    private fun <T> applyToMap(
        map: MutableMap<String, T>,
        item: T
    ) where T : Mergeable<T>, T : Referencable, T : WithOperation {
        when (item.operation) {
            Operation.DELETE -> map.remove(item.id)
            Operation.CREATE -> map[item.id] = item
            Operation.UPDATE -> {
                val existing = map[item.id] ?: TODO()
                map[item.id] = existing.mergeFrom(item)
            }
            Operation.REFERENCE -> {
            } // technically we should handle this, but for now it's irrelevant
            else -> throw NotImplementedError("Operation ${item.operation} not implemented for ${item::class.java}.")
        }
    }

    private data class RootNode(val elements: List<DependencyTreeNode>)
    private data class DependencyTreeNode(val id: String, val requirements: List<DependencyTreeNode>)
}
