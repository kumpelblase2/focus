package de.eternalwings.focus.view

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

enum class RelativeDurationApplicableField {
    CURRENT_DATE,
    SET_DATE
}

data class RelativeDuration(
    val amount: Long,
    val unit: ChronoUnit,
    val applyTo: RelativeDurationApplicableField = RelativeDurationApplicableField.CURRENT_DATE
) {
    fun applyTo(date: LocalDateTime): LocalDateTime {
        return when(unit) {
            ChronoUnit.MONTHS -> date.plusMonths(amount)
            ChronoUnit.WEEKS -> date.plusWeeks(amount)
            ChronoUnit.YEARS -> date.plusYears(amount)
            else -> date.plus(unit.duration.multipliedBy(amount))
        }
    }

    override fun toString(): String {
        val applyTo = if(this.applyTo == RelativeDurationApplicableField.CURRENT_DATE) "~" else "@"
        val amount = this.amount.toString()
        val schedule = when(this.unit) {
            ChronoUnit.YEARS -> "y"
            ChronoUnit.MONTHS -> "m"
            ChronoUnit.WEEKS -> "w"
            ChronoUnit.DAYS -> "d"
            else -> throw IllegalStateException()
        }

        return applyTo + amount + schedule
    }
}

val INTERVAL_FORMAT = "([~@])(\\d+)([ymdw])".toRegex()

fun String.parseDuration(): RelativeDuration {
    val match = INTERVAL_FORMAT.matchEntire(this) ?: throw IllegalStateException("")
    val applyTo = when (match.groupValues[1]) {
        "~" -> RelativeDurationApplicableField.CURRENT_DATE
        "@" -> RelativeDurationApplicableField.SET_DATE
        else -> throw IllegalStateException()
    }
    val amount = match.groupValues[2].toLong()
    val schedule = when (match.groupValues[3]) {
        "y" -> ChronoUnit.YEARS
        "m" -> ChronoUnit.MONTHS
        "w" -> ChronoUnit.WEEKS
        "d" -> ChronoUnit.DAYS
        else -> throw IllegalStateException("Cannot understand schedule")
    }

    return RelativeDuration(amount, schedule, applyTo)
}
