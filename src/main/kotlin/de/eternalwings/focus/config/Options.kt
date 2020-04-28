package de.eternalwings.focus.config

import com.uchuhimo.konf.ConfigSpec
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*

object Options : ConfigSpec() {

    val todayIsNextOfDay by optional(
        false,
        name = "today-is-next-of-day",
        description = "If today is the closest next day of the same weekday."
    )

    val firstDayOfWeek by optional(
        DayOfWeek.MONDAY,
        name = "first-day-of-week",
        description = "The first day of the week"
    )

    val dateFormat by optional(
        (DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()) as SimpleDateFormat).toPattern(),
        name = "date-format",
        description = "Format of time inputs"
    )
}
