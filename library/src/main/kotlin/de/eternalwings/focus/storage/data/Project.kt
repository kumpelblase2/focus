package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.mergeInto
import java.time.ZonedDateTime

data class Project(
    val folder: Reference?,
    val singleton: Boolean?,
    val lastReview: ZonedDateTime?,
    val nextReview: ZonedDateTime?,
    val reviewInterval: String?,
    val status: ProjectStatus?
) : Mergeable<Project> {

    override fun mergeFrom(other: Project): Project {
        return Project(
            other.folder.mergeInto(folder),
            other.singleton ?: singleton,
            other.lastReview ?: lastReview,
            other.nextReview ?: nextReview,
            other.reviewInterval ?: reviewInterval,
            other.status ?: status
        )
    }
}
