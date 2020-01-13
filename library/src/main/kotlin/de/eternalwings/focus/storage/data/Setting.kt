package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.plist.Plist
import de.eternalwings.focus.storage.plist.PlistObject
import org.jdom2.Element
import java.time.LocalDateTime

data class Setting(
    override val id: String,
    override val added: LocalDateTime?,
    override val order: Long?,
    val content: PlistObject<*>?
) : Referencable, WithCreationTimestamp {
    companion object {
        fun fromXML(element: Element): Setting {
            val id = element.attr("id")!!
            val addedElement = element.getChild("added", OmniContainer.NAMESPACE)
            val added = LocalDateTime.parse(addedElement.value, OmniContainer.TIME_FORMAT)
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
