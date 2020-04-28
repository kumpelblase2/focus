package de.eternalwings.focus.view

import de.eternalwings.focus.storage.data.Location

data class OmniLocation(
    val name: String,
    val latitude: String,
    val longitude: String,
    val radius: Int,
    val notificationFlags: Short
) {

    constructor(location: Location) : this(
        location.name,
        location.latitude,
        location.longitude,
        location.radius,
        location.notificationFlags
    )

    fun toLocation(): Location {
        return Location(
            this.name,
            this.latitude,
            this.longitude,
            this.radius,
            this.notificationFlags
        )
    }
}
