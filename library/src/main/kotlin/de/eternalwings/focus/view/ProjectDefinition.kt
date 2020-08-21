package de.eternalwings.focus.view

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Project
import de.eternalwings.focus.storage.data.ProjectStatus
import java.time.ZonedDateTime

data class ProjectDefinition(
    val folder: OmniFolder?,
    val singleton: Boolean?,
    val lastReview: ZonedDateTime?,
    val nextReview: ZonedDateTime?,
    val reviewInterval: RelativeDuration?,
    val status: ProjectStatus
) {
    constructor(project: Project, folderResolver: (String) -> OmniFolder) : this(
        project.folder?.id?.let(folderResolver),
        project.singleton,
        project.lastReview,
        project.nextReview,
        project.reviewInterval?.parseDuration(),
        project.status ?: ProjectStatus.ACTIVE
    )

    fun toProject(): Project {
        return Project(
            this.folder?.let { Reference(it.id) },
            this.singleton,
            this.lastReview,
            this.nextReview,
            this.reviewInterval.toString(),
            this.status
        )
    }
}
