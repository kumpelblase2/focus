package de.eternalwings.focus.presentation

import com.google.gson.Gson

object JsonDataPrinter : DataPrinter<Any> {
    private val gson: Gson = GsonProvider.INSTANCE

    override fun print(data: List<Any>) {
        println(gson.toJson(data))
    }

    fun print(data: Any) {
        println(gson.toJson(data))
    }
}
