package de.eternalwings.focus.storage.plist

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class PlistTest {

    @Test
    fun parsePlist() {
        val read = Plist.parsePlist(READ_PLIST_LOCATION)
        assertEquals(createTestPlistObject(), read)
    }

    @Test
    fun writePlist() {
        val root = createTestPlistObject()
        val output = ByteArrayOutputStream()
        Plist.writePlist(root, output)
        val content = String(output.toByteArray())
        val equalTo = Files.readAllLines(READ_PLIST_LOCATION).joinToString("\r\n", "", "\r\n")
        assertEquals(equalTo, content)
    }

    private fun createTestPlistObject(): DictionaryObject {
        return DictionaryObject(
            mapOf(
                "test-name" to StringObject("name"),
                "test-array" to ArrayObject(listOf(StringObject("some-value"))),
                "some-number" to IntegerObject(1),
                "test-dict" to DictionaryObject(mapOf("123" to StringObject("value"))),
                "test-date" to DateObject(OffsetDateTime.of(2019, 8, 17, 16, 59, 54, 0, ZoneOffset.UTC)),
                "test-data" to DataObject("0".toByteArray())
            )
        )
    }

    companion object {
        private val READ_PLIST_LOCATION = Paths.get("src/test/resources/plist-test", "test-read.plist")
    }
}
