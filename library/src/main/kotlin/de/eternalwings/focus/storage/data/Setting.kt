package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.storage.plist.Plist
import de.eternalwings.focus.storage.plist.PlistObject
import de.eternalwings.focus.storage.xml.XmlConstants.NAMESPACE
import de.eternalwings.focus.storage.xml.attr
import de.eternalwings.focus.storage.xml.child
import de.eternalwings.focus.storage.xml.date
import org.jdom2.Element
import java.time.ZonedDateTime

data class Setting(
    override val id: String,
    override val added: ZonedDateTime?,
    override val order: Long?,
    val content: PlistObject<*>?
) : Referencable, WithCreationTimestamp,
    Mergeable<Setting, Setting> {

    override fun mergeFrom(other: Setting): Setting {
        return Setting(
            id,
            other.added ?: added,
            other.order ?: order,
            other.content ?: content
        )
    }

    companion object {
        fun fromXML(element: Element): Setting {
            val id = element.attr("id")!!
            val addedElement = element.getChild("added", NAMESPACE)
            val added = addedElement.value?.date()
            val addedOrder = addedElement.getAttribute("order")?.longValue
            val plistContent =
                Plist.parsePlistElement(element.child("plist")!!.children.first()) as PlistObject<Map<String, PlistObject<*>>>?
            return Setting(
                id,
                added,
                addedOrder,
                plistContent
            )
        }
    }
}
