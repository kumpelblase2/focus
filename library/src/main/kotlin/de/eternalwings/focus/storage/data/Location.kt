package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.xml.XmlConstants
import de.eternalwings.focus.storage.xml.attr
import org.jdom2.Element

data class Location(
    val name: String,
    val latitude: String,
    val longitude: String,
    val radius: Int,
    val notificationFlags: Short
) {

    fun toXML(): Element {
        return Element("location", XmlConstants.NAMESPACE).also {
            it.setAttribute("name", name)
            it.setAttribute("latitude", latitude)
            it.setAttribute("longitude", longitude)
            it.setAttribute("radius", radius.toString())
            it.setAttribute("notificationFlags", notificationFlags.toString())
        }
    }

    companion object {
        fun fromXML(element: Element): Location {
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
    }
}
