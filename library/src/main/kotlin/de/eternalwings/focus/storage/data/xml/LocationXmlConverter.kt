package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Location
import de.eternalwings.focus.storage.xml.XmlConstants
import de.eternalwings.focus.storage.xml.attr
import org.jdom2.Element

object LocationXmlConverter : XmlElementConverter<Location> {
    const val TAG_NAME = "location"

    override fun read(element: Element) : Location {
        val address = element.attr("name")!!
        val latitude = element.attr("latitude")!!
        val longitude = element.attr("longitude")!!
        val radius = element.attr("radius")?.toInt() ?: 100
        val notificationFlags = element.attr("notificationFlags")!!.toShort()
        return Location(
            address,
            latitude,
            longitude,
            radius,
            notificationFlags
        )
    }

    override fun write(source: Location): Element {
        return Element(TAG_NAME, XmlConstants.NAMESPACE).also {
            it.setAttribute("name", source.name)
            it.setAttribute("latitude", source.latitude)
            it.setAttribute("longitude", source.longitude)
            it.setAttribute("radius", source.radius.toString())
            it.setAttribute("notificationFlags", source.notificationFlags.toString())
        }
    }

}
