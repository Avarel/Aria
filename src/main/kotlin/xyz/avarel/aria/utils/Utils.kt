package xyz.avarel.aria.utils

import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.*
import org.jsoup.Jsoup
import xyz.avarel.aria.music.TrackContext

val okHttp = OkHttpClient()

fun <E> List<E>.partition(size: Int): List<List<E>> {
    val list = mutableListOf<List<E>>()
    var start = 0
    while (start < this.size) {
        list.add(subList(start, Math.min(start + size, this.size)))
        start += size
    }
    return list
}

val AudioTrack.remainingDuration: Long
    get() = this.duration - this.position

suspend fun AudioTrack.getThumbnail(): String? {
    return when (this) {
        is YoutubeAudioTrack -> "https://img.youtube.com/vi/$identifier/0.jpg"
        is SoundCloudAudioTrack -> {
            val xml = withTimeoutOrNull(2000) {
                okHttp.newCall(Request.Builder().url(info.uri).build()).await()
            } ?: return null
            val html = Jsoup.parse(xml)
            return html.head().selectFirst("[property=og:image]").attr("content").also(::println)
        }
        else -> null
    }
}

suspend fun extractSoundCloudThumbnail(track: AudioTrack) {

}

inline val AudioTrack.trackContext: TrackContext
    get() = this.userData as TrackContext

/**
 * Returns the sum of all values produced by [selector] function applied to each element in the collection.
 */
inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum: Long = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
