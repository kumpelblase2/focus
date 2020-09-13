package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Folder
import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.xml.*
import org.jdom2.Element

object FolderXmlConverter : BaseXmlElementConverter<Folder>("folder") {
    const val TAG_NAME = "folder"

    override fun fillXmlElement(source: Folder, container: Element) {
        source.parent?.let { container.addContent(referenceElement("folder", it)) }
        source.name?.let { container.addContent(textElement("name", it)) }
        source.note?.let { container.addContent(textElement("note", it)) }
        source.rank?.let { container.addContent(longElement("rank", it)) }
        source.hidden?.let { container.addContent(booleanElement("hidden", it)) }
    }

    override fun readValues(id: String, operation: Operation, container: Element): Folder {
        val parent = container.reference("folder")
        val added = container.date("added")
        val order = container.child("added")?.attr("order")?.toLong()
        val name = container.text("name")
        val note = container.htmlText("note")
        val rank = container.long("rank")
        val hidden = container.boolean("hidden")
        val modified = container.date("modified")
        return Folder(id, parent, added, order, name, note, rank, hidden, modified).also { it.operation = operation }
    }
}
