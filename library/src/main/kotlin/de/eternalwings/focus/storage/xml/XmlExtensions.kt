package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Reference
import de.eternalwings.focus.asReference
import org.jdom2.Element
import java.time.LocalDateTime


fun Element.boolean(name: String): Boolean? {
    return this.getChildText(name, OmniContainer.NAMESPACE)?.toBoolean()
}

fun Element.text(name: String): String? {
    return this.getChildText(name, OmniContainer.NAMESPACE)
}

fun Element.htmlText(name: String): String? {
    return this.getChild(name, OmniContainer.NAMESPACE)?.value
}

fun Element.long(name: String): Long? {
    return this.getChildText(name, OmniContainer.NAMESPACE)?.toLong()
}

fun Element.child(name: String): Element? {
    return this.getChild(name, OmniContainer.NAMESPACE)
}

fun Element.attr(name: String): String? {
    return this.getAttributeValue(name)
}

fun Element.date(name: String): LocalDateTime? {
    val text = this.text(name)
    if(text.isNullOrEmpty()) return null
    return LocalDateTime.parse(text, OmniContainer.TIME_FORMAT)
}

fun Element.reference(name: String): Reference? {
    return this.getChild(name, OmniContainer.NAMESPACE)?.getAttributeValue("idref")?.asReference()
}
