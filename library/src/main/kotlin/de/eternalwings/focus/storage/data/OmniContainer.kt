package de.eternalwings.focus.storage.data

import org.jdom2.Document
import org.jdom2.Namespace
import java.time.format.DateTimeFormatter

data class OmniContainer(
    val creator: ContentCreator,
    val content: List<Referencable>
) {
    companion object {
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val NAMESPACE: Namespace = Namespace.getNamespace("http://www.omnigroup.com/namespace/OmniFocus/v2")
        fun fromXML(document: Document): OmniContainer {
            val element = document.rootElement
            val appId = element.getAttributeValue("app-id")
            val appVersion = element.getAttributeValue("app-version")
            val osVersion = element.getAttributeValue("os-version")
            val machineModel = element.getAttributeValue("machine-model")
            val changes: MutableList<Referencable> = arrayListOf()
            changes += element.getChildren("alarm", NAMESPACE).map { Alarm.fromXML(it) }
            changes += element.getChildren("task", NAMESPACE).map { Task.fromXML(it) }
            changes += element.getChildren("task-to-tag", NAMESPACE).map { TaskToTag.fromXML(it) }
            changes += element.getChildren("folder", NAMESPACE).map { Folder.fromXML(it) }
            changes += element.getChildren("context", NAMESPACE).map { Context.fromXML(it) }
            changes += element.getChildren("setting", NAMESPACE).map { Setting.fromXML(it) }
            changes += element.getChildren("perspective", NAMESPACE).map { Perspective.fromXML(it) }

            return OmniContainer(ContentCreator(appId, appVersion, osVersion, machineModel), changes)
        }
    }
}
