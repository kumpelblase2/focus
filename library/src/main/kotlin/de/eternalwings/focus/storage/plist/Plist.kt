package de.eternalwings.focus.storage.plist

import org.jdom2.DocType
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object Plist {
    private val PLIST_DOCTYPE
        get() = DocType("plist", "-//Apple//DTD PLIST 1.0//EN", "http://www.apple.com/DTDs/PropertyList-1.0.dtd")
    private val TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")

    fun parsePlist(file: Path): PlistObject<*> {
        val builder = SAXBuilder()
        val content = builder.build(file.toFile())
        val plistContainer = content.rootElement
        return parsePlistElement(plistContainer.children.first())
    }

    fun writePlist(plist: PlistObject<*>, location: Path) {
        writePlist(plist, FileOutputStream(location.toFile()))
    }

    fun writePlist(plist: PlistObject<*>, target: OutputStream) {
        val output = XMLOutputter(Format.getPrettyFormat())
        val root = createRootPlistElement()
        root.addContent(toElement(plist))
        val document = Document(root, PLIST_DOCTYPE)
        output.output(document, target)
    }

    private fun createRootPlistElement(): Element {
        return Element("plist").also { it.setAttribute("version", "1.0") }
    }

    private fun toElement(element: PlistObject<*>): Element {
        return when (element) {
            is StringObject -> Element("string").also { it.addContent(element.content) }
            is ArrayObject -> Element("array").also {
                element.content.map(::toElement).forEach { elem -> it.addContent(elem) }
            }
            is IntegerObject -> Element("integer").also { it.addContent(element.content.toString()) }
            is BooleanObject -> Element(element.content.toString())
            is DictionaryObject -> Element("dict").also {
                element.content.entries.forEach { content ->
                    it.addContent(Element("key").also { it.addContent(content.key) })
                    it.addContent(toElement(content.value))
                }
            }
            is DataObject -> Element("data").also { it.addContent(base64Encode(element.content)) }
            is DateObject -> Element("date").also {
                it.addContent(element.content.atZoneSameInstant(ZoneOffset.UTC).format(TIME_FORMAT))
            }
            else -> throw IllegalStateException("Unknown data type for plist: ${element.javaClass}")
        }
    }

    fun parsePlistElement(element: Element): PlistObject<*> {
        return when (element.name) {
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

    private fun base64Encode(byteData: ByteArray): String {
        val encoder = Base64.getEncoder()
        return String(encoder.encode(byteData))
    }
}
