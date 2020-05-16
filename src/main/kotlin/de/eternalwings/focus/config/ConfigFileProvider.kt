package de.eternalwings.focus.config

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object ConfigFileProvider {
    fun getConfigurationFile(): Path {
        return getConfigFolder().resolve("config.toml")
    }

    fun doesConfigurationExist(): Boolean {
        return Files.exists(getConfigurationFile())
    }

    private fun getConfigFolder() : Path {
        val osName = System.getProperty("os.name")
        var relativeConfigDir = Paths.get(System.getProperty("user.home"), ".config")
        if(osName.toLowerCase().startsWith("windows")) {
            relativeConfigDir = Paths.get("APPDATA")
        } else if (!System.getenv("XDG_CONFIG_HOME").isNullOrEmpty()) {
            relativeConfigDir = Paths.get(System.getenv("XDG_CONFIG_HOME"))
        }

        return relativeConfigDir.resolve("focus")
    }

    fun createConfigurationFile() {
        val configFile = getConfigurationFile()
        val parentPath = configFile.parent
        if(!Files.exists(parentPath)) {
            Files.createDirectories(parentPath)
        }

        Files.createFile(configFile)
    }
}
