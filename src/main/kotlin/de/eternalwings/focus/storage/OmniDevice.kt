package de.eternalwings.focus.storage

import de.eternalwings.plist.ArrayObject
import de.eternalwings.plist.DictionaryObject
import de.eternalwings.plist.StringObject
import java.time.OffsetDateTime

data class OmniDevice(
    val marketingVersion: String,
    val frameworkVersion: String,
    val cpuCount: String,
    val cpuType: String,
    val cpuName: String,
    val arch: String,
    val model: String,
    val syncVersion: String,
    val supportedCapabilities: Collection<String>,
    val osVersion: String,
    val osVersionNumber: String,
    val xmlSupportedCapabilities: Collection<String>,
    val bundleId: String,
    val bundleVersion: String,
    val clientId: String,
    val hostId: String,
    val lastSync: OffsetDateTime,
    val name: String,
    val registerDate: OffsetDateTime,
    val tailIds: Collection<String>
) {
    companion object {
        fun fromPlist(plist: DictionaryObject): OmniDevice {
            return with(plist.content) {
                OmniDevice(
                    this["ApplicationMarketingVersion"]!!.get(),
                    this["CurrentFrameworkVersion"]!!.get(),
                    this["HardwareCPUCount"]!!.get(),
                    this["HardwareCPUType"]!!.get(),
                    this["HardwareCPUTypeDescription"]!!.get(),
                    this["HardwareCPUTypeName"]!!.get(),
                    this["HardwareModel"]!!.get(),
                    this["OFMSyncClientModelVersion"]!!.get(),
                    (this["OFMSyncClientSupportedCapabilities"] as ArrayObject).content.map { (it as StringObject).content },
                    this["OSVersion"]!!.get(),
                    this["OSVersionNumber"]!!.get(),
                    (this["XMLSyncClientSupportedCapabilities"] as ArrayObject).content.map { (it as StringObject).content },
                    this["bundleIdentifier"]!!.get(),
                    this["bundleVersion"]!!.get(),
                    this["clientIdentifier"]!!.get(),
                    this["hostID"]!!.get(),
                    this["lastSyncDate"]!!.get(),
                    this["name"]!!.get(),
                    this["registrationDate"]!!.get(),
                    (this["tailIdentifiers"] as ArrayObject).content.map { (it as StringObject).content }
                )
            }
        }
    }
}
