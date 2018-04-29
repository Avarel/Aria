package xyz.avarel.aria.music

/**
 * Track repeat options for [TrackScheduler].
 *
 * @author Avarel
 */
enum class RepeatMode {
    /**
     * No repeat.
     */
    NONE,

    /**
     * Repeat the queue.
     */
    QUEUE,

    /**
     * Repeat the current song.
     */
    SONG;

    override fun toString() = name.toLowerCase().capitalize()
}