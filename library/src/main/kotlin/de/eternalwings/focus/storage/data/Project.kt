package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.xml.boolean
import de.eternalwings.focus.storage.xml.date
import de.eternalwings.focus.storage.xml.reference
import de.eternalwings.focus.storage.xml.text
import org.jdom2.Element
import java.time.Duration
import java.time.LocalDateTime

data class Project(
    val folder: Reference?,
    val singleton: Boolean?,
    val lastReview: LocalDateTime?,
    val nextReview: LocalDateTime?,
    val reviewInterval: String?,
    val status: String?
) {
    companion object {
        fun fromXML(element: Element): Project {
            val parentFolder = element.reference("folder")
            val singleton = element.boolean("singleton")
            val lastReview = element.date("last-review")
            val nextReview = element.date("next-review")
            val reviewInterval = element.text("review-interval")
            val status = element.text("status")
            return Project(parentFolder, singleton, lastReview, nextReview, reviewInterval, status)
        }
    }
}
