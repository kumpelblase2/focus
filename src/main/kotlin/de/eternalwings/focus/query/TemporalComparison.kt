package de.eternalwings.focus.query

import de.eternalwings.focus.config.Options
import de.eternalwings.focus.config.config
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

sealed class TemporalComparison {
    abstract fun compareTo(date: ZonedDateTime): Boolean

    class DateComparison(private val localDate: LocalDate) : TemporalComparison() {
        override fun compareTo(date: ZonedDateTime): Boolean {
            return localDate.year == date.year
                    && localDate.month == date.month
                    && localDate.dayOfMonth == date.dayOfMonth
        }
    }

    class WeekComparison(private val week: LocalDate) : TemporalComparison() {
        override fun compareTo(date: ZonedDateTime): Boolean {
            val firstDayOfWeek = config[Options.firstDayOfWeek]
            val weeks = WeekFields.of(firstDayOfWeek, 1)
            val week = week.get(weeks.weekOfYear())
            return week == date.get(weeks.weekOfYear())
        }
    }

    class MonthComparison(private val month: LocalDate) : TemporalComparison() {
        override fun compareTo(date: ZonedDateTime): Boolean {
            return month.year == date.year && month.month == date.month
        }
    }

    class TimeComparison(private val time: LocalTime) : TemporalComparison() {
        override fun compareTo(date: ZonedDateTime): Boolean {
            return date.hour == time.hour
                    && if (time.minute != 0) date.minute == time.minute else true
        }
    }

    class YearComparison(private val year: Year) : TemporalComparison() {
        override fun compareTo(date: ZonedDateTime): Boolean {
            return date.year == year.value
        }
    }

    companion object {
        private val localeBasedDateFormatter by lazy {
            DateTimeFormatter.ofPattern(config[Options.dateFormat])
        }

        private val localeBasedTimeFormatter =
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault())

        private val constants: MutableMap<String, () -> TemporalComparison> = mutableMapOf(
            "tomorrow" to { DateComparison(LocalDate.now().plusDays(1)) },
            "today" to { DateComparison(LocalDate.now()) },
            "yesterday" to { DateComparison(LocalDate.now().minusDays(1)) },
            "noon" to { TimeComparison(LocalTime.of(12, 0)) },
            "next week" to { WeekComparison(LocalDate.now().plusWeeks(1)) },
            "last week" to { WeekComparison(LocalDate.now().minusWeeks(1)) },
            "this week" to { WeekComparison(LocalDate.now()) },
            "next month" to { MonthComparison(LocalDate.now().plusMonths(1)) },
            "last month" to { MonthComparison(LocalDate.now().minusMonths(1)) },
            "this month" to { MonthComparison(LocalDate.now()) },
            "this year" to { YearComparison(Year.now()) },
            "last year" to { YearComparison(Year.now().minusYears(1)) },
            "next year" to { YearComparison(Year.now().plusYears(1)) }
        )

        init {
            val days = DayOfWeek.values()
            days.forEach { day ->
                val dayName = day.name.toLowerCase()
                constants[dayName] = { DateComparison(nextDayOf(day)) }
                constants["next $dayName"] = { DateComparison(LocalDate.now().with(TemporalAdjusters.next(day))) }
                constants["last $dayName"] = { DateComparison(nextDayOf(day).minusWeeks(1)) }
                constants["this $dayName"] = { DateComparison(thisDay(day)) }
            }
        }

        private fun nextDayOf(day: DayOfWeek): LocalDate {
            val today = LocalDate.now()
            return if (config[Options.todayIsNextOfDay] && today.dayOfWeek == day) {
                today
            } else {
                today.with(TemporalAdjusters.next(day))
            }
        }

        private fun thisDay(day: DayOfWeek): LocalDate {
            val today = LocalDate.now()
            return if (today.dayOfWeek == day) {
                today
            } else {
                today.with(TemporalAdjusters.next(day))
            }
        }

        fun fromString(value: String): TemporalComparison {
            val matchingConstant = constants[value]
            if (matchingConstant != null) {
                return matchingConstant()
            }

            try {
                val parsedDate = LocalDate.parse(value, localeBasedDateFormatter)
                return DateComparison(parsedDate)
            } catch (ex: DateTimeParseException) {
                // Ignore and just continue
            }

            try {
                val parsedTime = LocalTime.parse(value, localeBasedTimeFormatter)
                return TimeComparison(parsedTime)
            } catch (ex: DateTimeParseException) {
                // Ignore and just continue
            }

            throw IllegalStateException("Cannot parse '$value' as a date")
        }
    }
}
