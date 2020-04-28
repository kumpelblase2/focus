package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element
import java.time.ZonedDateTime

data class Project(
    val folder: Reference?,
    val singleton: Boolean?,
    val lastReview: ZonedDateTime?,
    val nextReview: ZonedDateTime?,
    val reviewInterval: String?,
    val status: String?
) {

    fun toXML(): Element {
        return Element(TAG_NAME, XmlConstants.NAMESPACE).also {
            folder?.let { folder -> it.addContent(referenceElement("folder", folder)) }
            singleton?.let { singleton -> it.addContent(booleanElement("singleton", singleton)) }
            lastReview?.let { lastReview -> it.addContent(dateElement("last-review", lastReview)) }
            nextReview?.let { nextReview -> it.addContent(dateElement("next-review", nextReview)) }
            reviewInterval?.let { reviewInterval -> it.addContent(textElement("review-interval", reviewInterval)) }
            status?.let { status -> it.addContent(textElement("status", status)) }
        }
    }

    companion object {
        const val TAG_NAME = "project"

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
