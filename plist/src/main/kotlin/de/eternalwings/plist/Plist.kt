package de.eternalwings.plist

import com.sun.org.apache.xml.internal.security.utils.Base64
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.nio.file.Path
import java.time.OffsetDateTime

object Plist {
    fun parsePlist(file: Path): PlistObject<*> {
        val builder = SAXBuilder()
        val content = builder.build(file.toFile())
        val plistContainer = content.rootElement
        return parseChild(plistContainer.children.first())
    }

    private fun parseChild(element: Element): PlistObject<*> {
        return when(element.name) {
            "dict" -> DictionaryObject(element.children.chunked(2).map(Plist::createDictEntry).toMap())
            "array" -> ArrayObject(element.children.map(Plist::parseChild))
            "integer" -> IntegerObject(element.value.toInt())
            "data" -> DataObject(Base64.decode(element.value))
            "date" -> DateObject(OffsetDateTime.parse(element.value))
            "string" -> StringObject(element.value)
            else -> throw IllegalStateException("Unknown data type ${element.name}")
        }
    }

    private fun createDictEntry(entry: List<Element>): Pair<String, PlistObject<*>> {
        val key = entry[0]
        check(key.name == "key")
        val value = parseChild(entry[1])
        return key.value to value
    }
}
