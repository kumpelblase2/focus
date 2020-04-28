package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.xml.XmlConstants.NAMESPACE
import org.jdom2.Document
import org.jdom2.Element

data class OmniContainer(
    val creator: ContentCreator,
    val content: List<ChangesetElement>
) {

    fun toXML(): Document {
        return Document().also {
            val root = Element(ROOT_TAG_NAME, NAMESPACE)
            root.setAttribute("app-id", creator.appId)
            root.setAttribute("app-version", creator.appVersion)
            root.setAttribute("os-version", creator.osVersion)
            root.setAttribute("machine-model", creator.machineModel)
            it.rootElement = root

            content.forEach { change ->
                root.addContent(change.toXML())
            }
        }
    }

    companion object {
        const val ROOT_TAG_NAME = "omnifocus"

        fun fromXML(document: Document): OmniContainer {
            val element = document.rootElement
            val appId = element.getAttributeValue("app-id")
            val appVersion = element.getAttributeValue("app-version")
            val osVersion = element.getAttributeValue("os-version")
            val machineModel = element.getAttributeValue("machine-model")
            val changes: MutableList<ChangesetElement> = arrayListOf()
            element.children.forEach { child ->
                if (child.namespace == NAMESPACE) {
                    val created: ChangesetElement = when (child.name) {
                        Alarm.TAG_NAME -> Alarm.fromXML(child)
                        Folder.TAG_NAME -> Folder.fromXML(child)
                        Setting.TAG_NAME -> Setting.fromXML(child)
                        Context.TAG_NAME -> Context.fromXML(child)
                        Task.TAG_NAME -> Task.fromXML(child)
                        TaskToTag.TAG_NAME -> TaskToTag.fromXML(child)
                        Perspective.TAG_NAME -> Perspective.fromXML(child)
                        else -> TODO()
                    }

                    changes.add(created)
                }
            }

            return OmniContainer(ContentCreator(appId, appVersion, osVersion, machineModel), changes)
        }
    }
}
