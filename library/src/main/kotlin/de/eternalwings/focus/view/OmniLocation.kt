package de.eternalwings.focus.view

import de.eternalwings.focus.storage.data.Location

data class OmniLocation(
    val name: String,
    val latitude: String,
    val longitude: String,
    val radius: Int,
    val notificationFlags: Short
)

fun Location.toOmniLocation(): OmniLocation {
    return OmniLocation(this.name, this.latitude, this.longitude, this.radius, this.notificationFlags)
}
