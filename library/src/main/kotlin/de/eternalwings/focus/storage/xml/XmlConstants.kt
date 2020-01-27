package de.eternalwings.focus.storage.xml

import org.jdom2.Namespace
import java.time.format.DateTimeFormatter

object XmlConstants {
    val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val NAMESPACE: Namespace = Namespace.getNamespace("http://www.omnigroup.com/namespace/OmniFocus/v2")
}
