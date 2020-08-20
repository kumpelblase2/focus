package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.plist.Plist
import de.eternalwings.focus.storage.plist.PlistObject
import de.eternalwings.focus.storage.xml.XmlConstants.NAMESPACE
import de.eternalwings.focus.storage.xml.attr
import de.eternalwings.focus.storage.xml.child
import de.eternalwings.focus.storage.xml.date
import org.jdom2.Element
import java.time.ZonedDateTime

data class Perspective(
    override val id: String,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val content: PlistObject<*>?
    // TODO missing icon attachment
) : BaseChangesetElement(), WithCreationTimestamp, WithOperation,
    Mergeable<Perspective> {

    override val tagName = TAG_NAME
    override var operation: Operation = Operation.CREATE

    override fun mergeFrom(other: Perspective): Perspective {
        return Perspective(
            id,
            other.added ?: added,
            other.order ?: order,
            other.content ?: content
        )
    }

    override fun fillXmlElement(element: Element) {
        content?.let { content ->
            val plistElement = Plist.toElement(content)
            element.addContent(Element("plist").also { it.addContent(plistElement) })
        }
    }

    companion object {
        const val TAG_NAME = "perspective"

        fun fromXML(element: Element): Perspective {
            val operation = element.attr("op")?.toOperation() ?: Operation.CREATE
            val id = element.attr("id")!!
            val container = if (operation == Operation.REFERENCE) element.child("reference-snapshot")!! else element
            val addedElement = container.getChild("added", NAMESPACE)
            val added = addedElement?.value?.date()
            val addedOrder = addedElement.getAttribute("order")?.longValue
            val plistContent = Plist.parsePlistElement(container.child("plist")!!.children.first())
            return Perspective(
                id,
                added,
                addedOrder,
                plistContent
            ).also { it.operation = operation }
        }
    }
}
