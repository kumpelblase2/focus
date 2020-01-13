package de.eternalwings.focus.storage.xml

import de.eternalwings.focus.storage.data.OmniContainer
import org.jdom2.input.SAXBuilder
import java.io.InputStream

fun parseXml(changes: InputStream): OmniContainer {
    val builder = SAXBuilder()
    val content = builder.build(changes)
    return OmniContainer.fromXML(content)
}
