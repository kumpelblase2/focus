package de.eternalwings.focus.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.hocon
import com.uchuhimo.konf.source.hocon.toHocon

object Configuration : ConfigSpec() {
    val location by optional<String?>(null)
    val password by optional<String?>(null)
    val device by optional<String?>(null)
    val debug by optional(true)
    val perspectives by optional(emptyList<CustomPerspective>())
}

val config by lazy {
    var configLoader = Config {
        addSpec(Configuration)
        addSpec(Options)
    }
    if (ConfigFileProvider.doesConfigurationExist()) {
        configLoader = configLoader.from.hocon.file(ConfigFileProvider.getConfigurationFile().toFile())
    }
    configLoader.from.env().from.systemProperties()
}

fun Config.save() {
    if (!ConfigFileProvider.doesConfigurationExist()) {
        ConfigFileProvider.createConfigurationFile()
    }
    this.toHocon.toFile(ConfigFileProvider.getConfigurationFile().toFile())
}
