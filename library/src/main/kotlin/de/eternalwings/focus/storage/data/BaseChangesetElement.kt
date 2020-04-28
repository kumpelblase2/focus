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
            if (this is WithOperation && operation != Operation.CREATE) {
                it.setAttribute("op", operation.name.toLowerCase())
            }

            if(this is WithCreationTimestamp) {
                val added = added
                check(added != null) { "A ${javaClass.simpleName} changeset entry always has an added date." }
                val elem = dateElement("added", added)
                order?.let { elem.setAttribute("order", order.toString()) }
                it.addContent(elem)
            }

            fillXmlElement(it)

            if(this is WithModificationTimestamp) {
                modified?.let { modified -> it.addContent(dateElement("modified", modified)) }
            }
        }
    }
}
