package de.eternalwings.focus.storage.data

data class Location(
    val name: String,
    val latitude: String,
    val longitude: String,
    val radius: Int,
    val notificationFlags: Short
)
