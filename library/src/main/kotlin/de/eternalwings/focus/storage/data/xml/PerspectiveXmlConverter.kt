package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.data.Perspective
import de.eternalwings.focus.storage.plist.Plist
import de.eternalwings.focus.storage.xml.XmlConstants
import de.eternalwings.focus.storage.xml.child
import de.eternalwings.focus.storage.xml.date
import org.jdom2.Element

object PerspectiveXmlConverter : BaseXmlElementConverter<Perspective>("perspective") {
    const val TAG_NAME = "perspective"

    override fun fillXmlElement(source: Perspective, container: Element) {
        source.content?.let { content ->
            val plistElement = Plist.toElement(content)
            container.addContent(Element("plist").also { it.addContent(plistElement) })
        }
    }

    override fun readValues(id: String, operation: Operation, container: Element): Perspective {
        val addedElement = container.getChild("added", XmlConstants.NAMESPACE)
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
