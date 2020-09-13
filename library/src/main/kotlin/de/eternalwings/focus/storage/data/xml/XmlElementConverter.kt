package de.eternalwings.focus.storage.data.xml

import org.jdom2.Element

/**
 * Converter for converting from/to XML for (mostly) changeset elements.
 */
interface XmlElementConverter<T> {

    /**
     * Writes the changeset element into an XML element
     */
    fun write(source: T) : Element

    /**
     * Reads the XML element into the internal representation
     */
    fun read(source: Element) : T
}
