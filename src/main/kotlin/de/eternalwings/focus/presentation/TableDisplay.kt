package de.eternalwings.focus.presentation

data class Header<T>(val name: String, val minWidth: Int, val maxWidth: Int, val resolver: (T) -> Any?)
data class HeaderData(val content: List<String>, val width: Int)

class Table<T>(init: Table<T>.() -> Unit = {}) {
    private val headers: MutableList<Header<T>> = mutableListOf()
    var nullFormatter: () -> String = { "" }
    var shortenFunction: (String, Int) -> String = { string, max -> string.substring(0, max - 3) + "..." }

    init {
        this.init()
    }

    fun header(name: String, minWidth: Int = 10, maxWidth: Int = minWidth, resolver: (T) -> Any?) {
        headers.add(Header(name, minWidth, maxWidth, resolver))
    }

    fun print(data: List<T>): String {
        val buffer = StringBuilder()
        val headerContent = headers.map { header ->
            val dataForColumn = data.map(header.resolver).map { value ->
                value?.toString() ?: nullFormatter()
            }

            val maxWidth = dataForColumn.map { it.length }.maxOrNull() ?: 0
            val selectedWidth = maxWidth.coerceIn(header.minWidth, header.maxWidth)

            val trimmedToMaxLength = dataForColumn.map {
                when {
                    it.length > selectedWidth -> shortenFunction(it, selectedWidth)
                    it.length < selectedWidth -> it.padEnd(selectedWidth, ' ')
                    else -> it
                }
            }

            HeaderData(trimmedToMaxLength, selectedWidth)
        }

        buffer.append("|")
        for (i in headers.indices) {
            val width = headerContent[i].width
            buffer.append(' ')
            buffer.append(headers[i].name.padEnd(width + 1, ' '))
            buffer.append("|")
        }
        buffer.append("\n")

        buffer.append("+")
        for (i in headers.indices) {
            val width = headerContent[i].width
            buffer.append("-".repeat(width + 2))
            buffer.append("+")
        }
        buffer.append("\n")

        for (i in data.indices) {
            buffer.append("| ")
            for (j in headers.indices) {
                val value = headerContent[j].content[i]
                buffer.append(value)
                buffer.append(" | ")
            }
            buffer.setLength(buffer.length - 1)
            buffer.append("\n")
        }

        return buffer.toString()
    }
}
