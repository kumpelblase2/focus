package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.Operation
import de.eternalwings.focus.storage.data.Setting
import de.eternalwings.focus.storage.plist.Plist
import de.eternalwings.focus.storage.plist.PlistObject
import de.eternalwings.focus.storage.xml.XmlConstants
import de.eternalwings.focus.storage.xml.child
import de.eternalwings.focus.storage.xml.date
import org.jdom2.Element

object SettingXmlConverter : BaseXmlElementConverter<Setting>("setting") {
    const val TAG_NAME = "setting"

    override fun fillXmlElement(source: Setting, container: Element) {
        source.content?.let { content ->
            val contentPlist = Plist.toElement(content)
            container.addContent(Element("plist").also { it.addContent(contentPlist) })
        }
    }

    override fun readValues(id: String, operation: Operation, container: Element): Setting {
        // A settings does not have an operation
        val addedElement = container.getChild("added", XmlConstants.NAMESPACE)
        val added = addedElement.value?.date()
        val addedOrder = addedElement.getAttribute("order")?.longValue
        val plistContent =
            Plist.parsePlistElement(container.child("plist")!!.children.first()) as PlistObject<Map<String, PlistObject<*>>>?
        return Setting(
            id,
            added,
            addedOrder,
            plistContent
        )
    }
}
