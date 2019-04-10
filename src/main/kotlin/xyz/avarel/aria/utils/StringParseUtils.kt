package xyz.avarel.aria.utils

import java.time.Duration
import java.util.regex.Pattern

fun Duration.formatDuration(): String {
    if (this.toMillis() == Long.MAX_VALUE) return "∞"

    val s = this.seconds
    val m = s / 60
    val h = m / 60

    return "%02d:%02d:%02d".format(h, m % 60, s % 60)
}

private val durationPattern = Pattern.compile("(?:(?:(\\d+):)?(\\d{1,2}):)?(\\d{1,2})")
fun String.toDurationOrNull(): Duration? {
    val matcher = durationPattern.matcher(this)
    if (!matcher.matches()) return null
    val h = matcher.group(1)?.toInt() ?: 0
    val m = matcher.group(2)?.toInt() ?: 0
    val s = matcher.group(3)?.toInt() ?: 0
    return Duration.ofSeconds(h * 3600L + m * 60L + s)
}

private val rangePattern = Pattern.compile("(\\d+)?(?:\\.\\.|-)(\\d+)?")
fun String.toRangeOrNull(): IntRange? {
    val matcher = rangePattern.matcher(this)
    if (!matcher.matches()) return null
    val low = matcher.group(1)?.toInt() ?: Integer.MIN_VALUE
    val high = matcher.group(2)?.toInt() ?: Integer.MAX_VALUE
    return low..high
}