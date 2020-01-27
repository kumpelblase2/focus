package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.storage.plist.Plist
import de.eternalwings.focus.storage.plist.PlistObject
import de.eternalwings.focus.storage.xml.XmlConstants.NAMESPACE
import de.eternalwings.focus.storage.xml.XmlConstants.TIME_FORMAT
import de.eternalwings.focus.storage.xml.attr
import de.eternalwings.focus.storage.xml.child
import org.jdom2.Element
import java.time.LocalDateTime

data class Setting(
    override val id: String,
    override val added: LocalDateTime?,
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
            val added = LocalDateTime.parse(addedElement.value, TIME_FORMAT)
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
