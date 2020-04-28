package de.eternalwings.focus.storage.xml

import de.eternalwings.focus.Reference
import de.eternalwings.focus.asReference
import de.eternalwings.focus.storage.xml.XmlConstants.LOCAL_TIME_FORMAT
import de.eternalwings.focus.storage.xml.XmlConstants.NAMESPACE
import de.eternalwings.focus.storage.xml.XmlConstants.TIME_FORMAT
import org.jdom2.Element
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException


fun Element.boolean(name: String): Boolean? {
    return this.getChildText(name, NAMESPACE)?.toBoolean()
}

fun Element.text(name: String): String? {
    return this.getChildText(name, NAMESPACE)
}

fun Element.htmlText(name: String): String? {
    return this.getChild(name, NAMESPACE)?.value
}

fun Element.long(name: String): Long? {
    return this.getChildText(name, NAMESPACE)?.toLong()
}

fun Element.child(name: String): Element? {
    return this.getChild(name, NAMESPACE)
}

fun Element.attr(name: String): String? {
    return this.getAttributeValue(name)
}

fun Element.date(name: String): ZonedDateTime? {
    val text = this.text(name)
    return text?.date()
}

fun String.date(): ZonedDateTime? {
    if (this.isEmpty()) return null
    return try {
        ZonedDateTime.parse(this, TIME_FORMAT)
    } catch (ex: DateTimeParseException) {
        // We assume this is now in _local time_
        ZonedDateTime.parse(this, LOCAL_TIME_FORMAT)
    }
}

fun Element.reference(name: String): Reference? {
    return this.getChild(name, NAMESPACE)?.getAttributeValue("idref")?.asReference()
}

fun referenceElement(name: String, reference: Reference): Element {
    return Element(name, NAMESPACE).also {
        it.setAttribute("idref", reference.id)
    }
}

fun textElement(name: String, content: String): Element {
    return Element(name, NAMESPACE).also {
        it.addContent(content)
    }
}

fun dateElement(name: String, date: ZonedDateTime): Element {
    return Element(name, NAMESPACE).also {
        it.addContent(TIME_FORMAT.format(date))
    }
}

fun longElement(name: String, value: Long): Element {
    return Element(name, NAMESPACE).also {
        it.addContent(value.toString())
    }
}

fun booleanElement(name: String, value: Boolean): Element {
    return Element(name, NAMESPACE).also {
        it.addContent(value.toString())
    }
}
