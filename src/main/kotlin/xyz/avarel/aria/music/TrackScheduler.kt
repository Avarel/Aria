package xyz.avarel.aria.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import org.slf4j.LoggerFactory

/**
 * Handles track scheduling, music queue, and repeat options.
 *
 * @param controller Main [MusicController] instance.
 * @author Avarel
 */
class TrackScheduler(private val controller: MusicController) : AudioEventAdapter() {
    companion object {
        val LOG = LoggerFactory.getLogger(TrackScheduler::class.java)!!
    }

    /**
     * @return List of audio tracks queued.
     */
    val queue: MutableList<AudioTrack> = mutableListOf()

    /**
     * @return The current repeat mode.
     * @see RepeatMode
     */
    var repeatMode: RepeatMode = RepeatMode.NONE

    /**
     * @return The total length of the queue in milliseconds.
     */
    val duration: Long get() = queue.fold(0L) { a, track ->
        if (track.duration == Long.MAX_VALUE) return Long.MAX_VALUE else a + track.duration
    }

    /**
     * Add the next track to offer or play right away if nothing is in the offer.
     *
     * @param track
     *        The track to play or add to offer.
     */
    fun offer(track: AudioTrack) {
        if (!controller.player.startTrack(track, true)) {
            queue.add(track)
        } else {
            LOG.debug("${track.info.title} playback started.")
        }
    }

    fun remove(index: Int): AudioTrack {
        if (index >= queue.size) throw IndexOutOfBoundsException("Index out of range: $index, size: ${queue.size}")
        queue.iterator().apply {
            withIndex().forEach { (i, item) ->
                if (i == index) {
                    remove()
                    return item
                }
            }
        }
        throw IllegalStateException()
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    fun nextTrack() {
        if (queue.isEmpty()) {
            controller.player.stopTrack()
            return
        }
        val track = queue.removeAt(0)
        controller.player.startTrack(track, false)
        LOG.debug("${track.info.title} playback started.")
    }

    /**
     * Handles starting the next track.
     *
     * Different [RepeatMode] will affect the track scheduler.
     * - [RepeatMode.NONE]: Start the next track.
     * - [RepeatMode.QUEUE]: Start the next track and add the previous track to the end of the offer.
     * - [RepeatMode.SONG]: Start the current track over again.
     *
     * @param player
     *        Audio player
     * @param track
     *        Audio track that ended
     * @param endReason
     *        The reason why the track stopped playing
     */
    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        LOG.debug("${track.info.title} playback ended.")

        if (endReason.mayStartNext) {
            when (repeatMode) {
                RepeatMode.SONG -> {
                    val newTrack = track.makeClone().also { it.userData = track.userData }
                    player.startTrack(newTrack, false)
                }
                RepeatMode.QUEUE -> {
                    val newTrack = track.makeClone().also { it.userData = track.userData }
                    queue.add(newTrack)
                    nextTrack()
                }
                RepeatMode.NONE -> nextTrack()
            }
        }
    }
}