package de.eternalwings.focus.storage.plist

import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.nio.file.Path
import java.time.OffsetDateTime
import java.util.*

object Plist {
    fun parsePlist(file: Path): PlistObject<*> {
        val builder = SAXBuilder()
        val content = builder.build(file.toFile())
        val plistContainer = content.rootElement
        return parsePlistElement(plistContainer.children.first())
    }

    fun parsePlistElement(element: Element): PlistObject<*> {
        return when(element.name) {
            "dict" -> DictionaryObject(element.children.chunked(2).map(Plist::createDictEntry).toMap())
            "array" -> ArrayObject(element.children.map(Plist::parsePlistElement))
            "integer" -> IntegerObject(element.value.toInt())
            "data" -> DataObject(base64Decode(element.value))
            "date" -> DateObject(OffsetDateTime.parse(element.value))
            "string" -> StringObject(element.value)
            "true" -> BooleanObject(true)
            "false" -> BooleanObject(false)
            else -> throw IllegalStateException("Unknown data type ${element.name}")
        }
    }

    private fun createDictEntry(entry: List<Element>): Pair<String, PlistObject<*>> {
        val key = entry[0]
        check(key.name == "key")
        val value = parsePlistElement(entry[1])
        return key.value to value
    }

    private fun base64Decode(base64Value: String): ByteArray {
        val decoder = Base64.getDecoder()
        return decoder.decode(base64Value.replace("[\n\t\r]".toRegex(), ""))
    }
}
