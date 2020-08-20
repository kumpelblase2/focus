package de.eternalwings.focus.view

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.IdGenerator
import de.eternalwings.focus.storage.OmniDevice
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.storage.data.*
import java.util.*

internal fun changeSetContentOrder(element: ChangesetElement): Int {
    return when (element) {
        is Setting -> 1
        is WithOperation -> if (element.operation == Operation.REFERENCE) 2 else 10
        else -> 10
    }
}

class OmniFocusState(private val storage: OmniStorage, val autoPersists: Boolean = false) {
    private var contextById: Map<String, OmniContext> = emptyMap()
    private var tasksById: Map<String, OmniTasklike> = emptyMap()
    private var foldersById: Map<String, OmniFolder> = emptyMap()

    val contexts: Collection<OmniContext>
        get() = contextById.values
    val folders: Collection<OmniFolder>
        get() = foldersById.values
    val tasks: Collection<OmniTasklike>
        get() = tasksById.values

    val byId: Map<String, Referencable> by lazy {
        contextById + tasksById + foldersById
    }

    init {
        val mergedTasks = mutableMapOf<String, Task>()
        val mergedContexts = mutableMapOf<String, Context>()
        val mergedFolders = mutableMapOf<String, Folder>()

        storage.changeSets.forEach { changeset ->
            changeset.container.content.asSequence().sortedBy(::changeSetContentOrder).forEach { item ->
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
        this.foldersById = buildFromTree(dependenciesForFolders, mergedFolders) { folder, previous ->
            OmniFolder(folder) { previous[it]!! }
        }

        val dependenciesForContexts = createDependencyTree(mergedContexts) { it.parentContext?.id }
        this.contextById = buildFromTree(dependenciesForContexts, mergedContexts) { context, previous ->
            OmniContext(context) { previous[it]!! }
        }

        val dependenciesForTasks = createDependencyTree(mergedTasks) { it.parent?.id }
        this.tasksById = buildFromTree(dependenciesForTasks, mergedTasks) { task, previous ->
            val resolveContext: (Reference) -> OmniContext = { context -> this.contexts.first { it.id == context.id } }
            if (task.project != null) {
                val resolveFolder: (String) -> OmniFolder = { folder -> foldersById[folder]!! }
                OmniProject(task, resolveContext, { previous[it] as OmniProject }, resolveFolder)
            } else {
                OmniTask(task, resolveContext) { previous[it] as OmniTasklike }
            }
        }
    }

    fun getTaskById(id: String): OmniTask? {
        return tasksById[id] as? OmniTask
    }

    fun getProjectById(id: String): OmniProject? {
        return tasksById[id] as? OmniProject
    }

    fun getContextById(id: String): OmniContext? {
        return contextById[id]
    }

    fun getFolderById(id: String): OmniFolder? {
        return foldersById[id]
    }

    fun generateUnusedId(): String {
        return IdGenerator.generate(byId.keys)
    }

    fun createTask(task: OmniTask, creator: OmniDevice) {
        tasksById = tasksById + (task.id to task)
        storage.appendChangeset(storage.prepareChangeset(creator, task.toTask()), autoPersists)
        if (autoPersists) {
            storage.updateDevice(creator)
        }
    }

    fun updateTask(task: OmniTask, creator: OmniDevice) {
        val existing = tasks.find { it.id == task.id } as? OmniTask
        if (existing == null) {
            this.createTask(task, creator)
        } else {
            val change = task.toTask(existing)
            storage.appendChangeset(storage.prepareChangeset(creator, change), autoPersists)
            tasksById = tasksById + (task.id to task)
        }

        if (autoPersists) {
            storage.updateDevice(creator)
        }
    }

    fun createProject(project: OmniProject, creator: OmniDevice) {
        tasksById = tasksById + (project.id to project)
        storage.appendChangeset(storage.prepareChangeset(creator, project.toTask()), autoPersists)
        if (autoPersists) {
            storage.updateDevice(creator)
        }
    }

    fun createContext(context: OmniContext, creator: OmniDevice) {
        contextById = contextById + (context.id to context)
        storage.appendChangeset(storage.prepareChangeset(creator, context.toContext()), autoPersists)
        if (autoPersists) {
            storage.updateDevice(creator)
        }
    }

    fun createFolder(folder: OmniFolder, creator: OmniDevice) {
        foldersById = foldersById + (folder.id to folder)
        storage.appendChangeset(storage.prepareChangeset(creator, folder.toFolder()), autoPersists)
        if (autoPersists) {
            storage.updateDevice(creator)
        }
    }

    private fun <T, R> buildFromTree(
        dependencyTree: RootNode,
        references: Map<String, T>,
        creator: (T, MutableMap<String, R>) -> R
    ): Map<String, R> {
        val buildQueue: Queue<DependencyTreeNode> = LinkedList(dependencyTree.elements)
        val built = mutableMapOf<String, R>()

        while (buildQueue.isNotEmpty()) {
            val current = buildQueue.poll()
            val rawData = references[current.id] ?: error("Data to convert not in original map")
            val created = creator(rawData, built)
            built[current.id] = created
            current.requirements.forEach { buildQueue.add(it) }
        }

        return built
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
                val existing = map[item.id]
                    ?: error("Trying to update an element that is not yet in the store. Item ID: ${item.id}")
                map[item.id] = existing.mergeFrom(item)
            }
            Operation.REFERENCE -> {
                val existing = map[item.id]
                if (existing == null) {
                    map[item.id] = item
                } else {
                    if (existing != item) {
                        System.err.println("Reference for item with ID ${item.id} does not match the current state.")
                    }
                }
            }
        }
    }

    private data class RootNode(val elements: List<DependencyTreeNode>)
    private data class DependencyTreeNode(val id: String, val requirements: List<DependencyTreeNode>)
}
