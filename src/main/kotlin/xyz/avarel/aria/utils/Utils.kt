package xyz.avarel.aria.utils

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.avarel.aria.music.TrackContext
import java.time.Duration
import java.util.regex.Pattern
import kotlin.math.min

fun <E> List<E>.partition(size: Int): List<List<E>> {
    val list = mutableListOf<List<E>>()
    var start = 0
    while (start < this.size) {
        list.add(subList(start, min(start + size, this.size)))
        start += size
    }
    return list
}

fun Duration.formatDuration(): String {
    if (this.toMillis() == Long.MAX_VALUE) return "∞"

    val s = this.seconds
    val m = s / 60
    val h = m / 60

    return "%02d:%02d:%02d".format(h, m % 60, s % 60)
}

private val timePattern = Pattern.compile("(?:(?:(\\d+):)?(\\d{1,2}):)?(\\d{1,2})")

fun String.toTimeOrNull(): Duration? {
    val matcher = timePattern.matcher(this)
    if (!matcher.matches()) return null
    val h = matcher.group(1)?.toInt() ?: 0
    val m = matcher.group(2)?.toInt() ?: 0
    val s = matcher.group(3)?.toInt() ?: 0
    return Duration.ofSeconds(h * 3600L + m * 60L + s)
}

val AudioTrack.remainingDuration: Long
    get() {
        return this.duration - this.position
    }

val AudioTrack.thumbnail: String?
    get() {
        return when (this) {
            is YoutubeAudioTrack -> "https://img.youtube.com/vi/$identifier/0.jpg"
            else -> null
        }
    }

inline val AudioTrack.trackContext: TrackContext
    get() = this.userData as TrackContext

