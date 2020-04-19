package de.eternalwings.focus.storage.xml

import org.jdom2.Namespace
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object XmlConstants {
    val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val LOCAL_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZone(ZoneId.systemDefault())
    val NAMESPACE: Namespace = Namespace.getNamespace("http://www.omnigroup.com/namespace/OmniFocus/v2")
}
