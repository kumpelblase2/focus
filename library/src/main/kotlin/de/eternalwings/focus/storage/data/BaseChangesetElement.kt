package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.xml.XmlConstants
import de.eternalwings.focus.storage.xml.dateElement
import org.jdom2.Element

abstract class BaseChangesetElement : ChangesetElement {

    protected abstract val tagName: String

    protected abstract fun fillXmlElement(element: Element)

    override fun toXML(): Element {
        return Element(tagName, XmlConstants.NAMESPACE).also {
            it.setAttribute("id", id)
            val operation = if(this is WithOperation) this.operation else Operation.CREATE
            if (operation != Operation.CREATE) {
                it.setAttribute("op", operation.name.toLowerCase())
            }

            val container = if(operation == Operation.REFERENCE) {
                val inner = Element("snapshot-reference", XmlConstants.NAMESPACE)
                it.addContent(inner)
                inner
            } else it

            if(this is WithCreationTimestamp) {
                val added = added
                check(added != null) { "A ${javaClass.simpleName} changeset entry always has an added date." }
                val elem = dateElement("added", added)
                order?.let { elem.setAttribute("order", order.toString()) }
                container.addContent(elem)
            }

            if (this is WithModificationTimestamp) {
                modified?.let { modified -> container.addContent(dateElement("modified", modified)) }
            }

            fillXmlElement(container)
        }
    }
}
