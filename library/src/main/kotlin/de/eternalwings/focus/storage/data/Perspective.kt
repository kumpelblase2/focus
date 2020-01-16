package de.eternalwings.focus.storage.data

import de.eternalwings.focus.Referencable
import de.eternalwings.focus.storage.plist.Plist
import de.eternalwings.focus.storage.plist.PlistObject
import org.jdom2.Element
import java.time.LocalDateTime

data class Perspective(
    override val id: String,
    override val added: LocalDateTime?,
    override val order: Long?,
    val content: PlistObject<*>?,
    override val operation: Operation = Operation.CREATE
    // TODO missing icon attachment
) : Referencable, WithCreationTimestamp, WithOperation {
    companion object {
        fun fromXML(element: Element): Perspective {
            val id = element.attr("id")!!
            val addedElement = element.getChild("added", OmniContainer.NAMESPACE)
            val added = LocalDateTime.parse(addedElement.value, OmniContainer.TIME_FORMAT)
            val addedOrder = addedElement.getAttribute("order")?.longValue
            val plistContent = Plist.parsePlistElement(element.child("plist")!!.children.first())
            return Perspective(
                id,
                added,
                addedOrder,
                plistContent
            )
        }
    }
}
