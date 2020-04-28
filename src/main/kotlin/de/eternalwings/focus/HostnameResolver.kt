package de.eternalwings.focus

import java.net.InetAddress
import java.net.UnknownHostException

object HostnameResolver {
    fun getCurrentHostName(fallback: String = "focus-cli"): String {
        return try {
            InetAddress.getLocalHost().hostName
        } catch (ex: UnknownHostException) {
            fallback;
        }
    }
}
