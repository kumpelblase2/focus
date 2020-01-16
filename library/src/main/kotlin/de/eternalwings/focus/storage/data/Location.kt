package de.eternalwings.focus.storage.data

import org.jdom2.Element

data class Location(
    val name: String,
    val latitude: String,
    val longitude: String,
    val radius: Int,
    val notificationFlags: Short
) {
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
