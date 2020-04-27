package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.plist.ArrayObject
import de.eternalwings.focus.storage.plist.DictionaryObject
import de.eternalwings.focus.storage.plist.StringObject
import oshi.SystemInfo
import java.time.OffsetDateTime
import java.util.*

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

        const val MARKETING_VERSION = "3.7"
        const val FRAMEWORK_VERSION = "2"
        const val SYNC_VERSION = "6.0.5"
        val SUPPORTED_CAPABILITIES = listOf("delta_transactions")
        val XML_SUPPORTED_CAPABILITIES =
            listOf(
                "stable_repeats",
                "external_attachments",
                "floating_time_zones",
                "unknown_element_import",
                "versioned_perspectives",
                "delta_transactions",
                "active_object_hidden_dates"
            )
        const val BUNDLE_IDENTIFIER = "de.eternalwings.focus.Cli"
        const val BUNDLE_VERSION = "1.0"

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

        fun create(name: String, id: String, model: String): OmniDevice {
            val registration = OffsetDateTime.now()
            val hostID = UUID.randomUUID().toString()
            val cpuCount = Runtime.getRuntime().availableProcessors()
            val systemInfo = SystemInfo()

            return OmniDevice(
                MARKETING_VERSION,
                FRAMEWORK_VERSION,
                cpuCount.toString(),
                systemInfo.hardware.processor.physicalProcessorCount.toString() + "," + systemInfo.hardware.processor.logicalProcessorCount.toString(),
                systemInfo.hardware.processor.processorIdentifier.name,
                System.getProperty("os.arch"),
                model,
                SYNC_VERSION,
                SUPPORTED_CAPABILITIES,
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                XML_SUPPORTED_CAPABILITIES,
                BUNDLE_IDENTIFIER,
                BUNDLE_VERSION,
                id,
                hostID,
                registration,
                name,
                registration,
                emptyList()
            )
        }
    }
}
