package de.eternalwings.focus.config

import com.github.ajalt.clikt.output.TermUi
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
        val relativeConfigDir = getConfigDirectory()
        return relativeConfigDir.resolve("focus")
    }

    private fun getConfigDirectory(): Path {
        return if (TermUi.isWindows) {
            Paths.get("APPDATA")
        } else if (!System.getenv("XDG_CONFIG_HOME").isNullOrEmpty()) {
            Paths.get(System.getenv("XDG_CONFIG_HOME"))
        } else {
            Paths.get(System.getProperty("user.home"), ".config")
        }
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
