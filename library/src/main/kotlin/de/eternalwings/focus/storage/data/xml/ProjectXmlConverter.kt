package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Project
import de.eternalwings.focus.storage.data.ProjectStatus
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element

object ProjectXmlConverter : XmlElementConverter<Project> {
    const val TAG_NAME = "project"

    override fun read(element: Element): Project {
        val parentFolder = element.reference("folder")
        val singleton = element.boolean("singleton")
        val lastReview = element.date("last-review")
        val nextReview = element.date("next-review")
        val reviewInterval = element.text("review-interval")
        val status = element.text("status")?.let { ProjectStatus.fromValue(it) }
        return Project(parentFolder, singleton, lastReview, nextReview, reviewInterval, status)
    }

    override fun write(source: Project): Element {
        return Element(TAG_NAME, XmlConstants.NAMESPACE).also {
            source.folder?.let { folder -> it.addContent(referenceElement("folder", folder)) }
            source.singleton?.let { singleton -> it.addContent(booleanElement("singleton", singleton)) }
            source.lastReview?.let { lastReview -> it.addContent(dateElement("last-review", lastReview)) }
            source.nextReview?.let { nextReview -> it.addContent(dateElement("next-review", nextReview)) }
            source.reviewInterval?.let { reviewInterval ->
                it.addContent(
                    textElement(
                        "review-interval",
                        reviewInterval
                    )
                )
            }
            source.status?.let { status -> it.addContent(textElement("status", status.name)) }
        }
    }

}
