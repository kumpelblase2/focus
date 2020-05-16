package de.eternalwings.focus.config

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*

class Options {
    var todayIsNextOfDay: Boolean = false
    var firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
    var dateFormat: String =
        (DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()) as SimpleDateFormat).toPattern()
}

class Configuration {
    var location: String? = null
    var password: String? = null
    var device: String? = null
    var debug: Boolean = false
    var perspectives: Map<String, String> = emptyMap()
    var options: Options = Options()


    companion object {
        val instance: Configuration by lazy {
            loadConfiguration()
        }

        private fun loadConfiguration(): Configuration {
            if(!ConfigFileProvider.doesConfigurationExist()) {
                ConfigFileProvider.createConfigurationFile()
            }

            return Toml().read(ConfigFileProvider.getConfigurationFile().toFile()).to(Configuration::class.java);
        }

        fun save() {
            TomlWriter().write(instance, ConfigFileProvider.getConfigurationFile().toFile())
        }
    }
}
