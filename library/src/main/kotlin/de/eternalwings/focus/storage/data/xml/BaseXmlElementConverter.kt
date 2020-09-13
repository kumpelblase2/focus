package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.*
import de.eternalwings.focus.storage.xml.XmlConstants
import de.eternalwings.focus.storage.xml.attr
import de.eternalwings.focus.storage.xml.child
import de.eternalwings.focus.storage.xml.dateElement
import org.jdom2.Element

abstract class BaseXmlElementConverter<T : ChangesetElement>(private val tagName: String) : XmlElementConverter<T> {

    abstract fun fillXmlElement(source: T, container: Element)

    override fun write(source: T): Element {
        return Element(tagName, XmlConstants.NAMESPACE).also {
            it.setAttribute("id", source.id)
            val operation = if (source is WithOperation) source.operation else Operation.CREATE
            if (operation != Operation.CREATE) {
                it.setAttribute("op", operation.name.toLowerCase())
            }

            val container = if (operation == Operation.REFERENCE) {
                val inner = Element("snapshot-reference", XmlConstants.NAMESPACE)
                it.addContent(inner)
                inner
            } else it

            if (source is WithCreationTimestamp) {
                val added = source.added
                check(added != null) { "A ${javaClass.simpleName} changeset entry always has an added date." }
                val elem = dateElement("added", added)
                source.order?.let { elem.setAttribute("order", it.toString()) }
                container.addContent(elem)
            }

            if (this is WithModificationTimestamp) {
                modified?.let { modified -> container.addContent(dateElement("modified", modified)) }
            }

            fillXmlElement(source, container)
        }
    }

    override fun read(source: Element): T {
        val operation = source.attr("op")?.toOperation() ?: Operation.CREATE
        val id = source.getAttribute("id").value
        val container = if (operation == Operation.REFERENCE) source.child("reference-snapshot")!! else source

        return readValues(id, operation, container)
    }

    abstract fun readValues(id: String, operation: Operation, container: Element): T
}
