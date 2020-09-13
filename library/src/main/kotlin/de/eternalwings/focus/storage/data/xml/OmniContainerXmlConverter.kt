package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.ChangesetElement
import de.eternalwings.focus.storage.data.ContentCreator
import de.eternalwings.focus.storage.data.OmniContainer
import de.eternalwings.focus.storage.xml.XmlConstants
import org.jdom2.Document
import org.jdom2.Element

object OmniContainerXmlConverter {
    const val ROOT_TAG_NAME = "omnifocus"

    fun read(document: Document): OmniContainer {
        val element = document.rootElement
        val appId = element.getAttributeValue("app-id")
        val appVersion = element.getAttributeValue("app-version")
        val osVersion = element.getAttributeValue("os-version")
        val machineModel = element.getAttributeValue("machine-model")
        val changes: MutableList<ChangesetElement> = arrayListOf()
        element.children.forEach { child ->
            if (child.namespace == XmlConstants.NAMESPACE) {
                val converter = XmlTypeMapper.xmlToConverterMapping[child.name] ?: TODO()
                val created = converter.read(child) as ChangesetElement
                changes.add(created)
            }
        }

        return OmniContainer(ContentCreator(appId, appVersion, osVersion, machineModel), changes)
    }

    fun write(container: OmniContainer): Document {
        return Document().also {
            val root = Element(ROOT_TAG_NAME, XmlConstants.NAMESPACE)
            root.setAttribute("app-id", container.creator.appId)
            root.setAttribute("app-version", container.creator.appVersion)
            root.setAttribute("os-version", container.creator.osVersion)
            root.setAttribute("machine-model", container.creator.machineModel)
            it.rootElement = root

            container.content.forEach { change ->
                val converter: XmlElementConverter<ChangesetElement> =
                    XmlTypeMapper.typeToConverterMapping[change.javaClass] as XmlElementConverter<ChangesetElement>?
                        ?: error("Found no xml converter for type ${change.javaClass}")
                root.addContent(converter.write(change))
            }
        }
    }
}
