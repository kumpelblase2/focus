package de.eternalwings.focus.storage.data.xml

import de.eternalwings.focus.storage.data.*

object XmlTypeMapper {
    val xmlToConverterMapping: Map<String, XmlElementConverter<*>> = mapOf(
        AlarmXmlConverter.TAG_NAME to AlarmXmlConverter,
        ContextXmlConverter.TAG_NAME to ContextXmlConverter,
        FolderXmlConverter.TAG_NAME to FolderXmlConverter,
        PerspectiveXmlConverter.TAG_NAME to PerspectiveXmlConverter,
        SettingXmlConverter.TAG_NAME to SettingXmlConverter,
        TaskToTagXmlConverter.TAG_NAME to TaskToTagXmlConverter,
        TaskXmlConverter.TAG_NAME to TaskXmlConverter
    )

    val typeToConverterMapping: Map<Class<*>, XmlElementConverter<*>> = mapOf(
        Alarm::class.java to AlarmXmlConverter,
        Context::class.java to ContextXmlConverter,
        Folder::class.java to FolderXmlConverter,
        Perspective::class.java to PerspectiveXmlConverter,
        SettingXmlConverter::class.java to SettingXmlConverter,
        TaskToTag::class.java to TaskToTagXmlConverter,
        Task::class.java to TaskXmlConverter
    )
}
