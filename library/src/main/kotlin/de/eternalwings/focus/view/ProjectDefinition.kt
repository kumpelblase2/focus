package de.eternalwings.focus.view

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.data.Mergeable
import de.eternalwings.focus.storage.data.Project
import java.time.ZonedDateTime

data class ProjectDefinition(
    val folder: Reference?,
    val singleton: Boolean?,
    val lastReview: ZonedDateTime?,
    val nextReview: ZonedDateTime?,
    val reviewInterval: RelativeDuration?,
    val status: Status
) : Mergeable<ProjectDefinition, Project> {

    constructor(project: Project) : this(
        project.folder,
        project.singleton,
        project.lastReview,
        project.nextReview,
        project.reviewInterval?.parseDuration(),
        project.status?.let { Status.valueOf(it.toUpperCase()) } ?: Status.ACTIVE
    )

    override fun mergeFrom(other: Project): ProjectDefinition {
        return ProjectDefinition(
            other.folder ?: folder,
            other.singleton ?: singleton,
            other.lastReview ?: lastReview,
            other.nextReview ?: nextReview,
            other.reviewInterval?.parseDuration() ?: reviewInterval,
            other.status?.let { Status.valueOf(it.toUpperCase()) } ?: status
        )
    }
}
