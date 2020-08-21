package de.eternalwings.focus.storage.data

enum class ProjectStatus {
    ACTIVE,
    INACTIVE,
    DONE,
    DROPPED;

    companion object {
        fun fromValue(name: String?) : ProjectStatus {
            if(name == null) {
                return ACTIVE
            }

            return try {
                valueOf(name.toUpperCase())
            } catch (ex: Exception) {
                ACTIVE
            }
        }
    }
}
