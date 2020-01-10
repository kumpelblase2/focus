package de.eternalwings.plist

import java.time.OffsetDateTime

interface PlistObject<T> {
    val content: T

    fun <S> get(): S {
        return content as S
    }
}

data class StringObject(override val content: String) : PlistObject<String>
data class DateObject(override val content: OffsetDateTime) : PlistObject<OffsetDateTime>
data class DictionaryObject(override val content: Map<String, PlistObject<*>>) :
    PlistObject<Map<String, PlistObject<*>>>

data class ArrayObject(override val content: List<PlistObject<*>>) :
    PlistObject<List<PlistObject<*>>>

data class DataObject(override val content: ByteArray) : PlistObject<ByteArray>

data class IntegerObject(override val content: Int) : PlistObject<Int>
