package xyz.avarel.aria.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.audio.AudioSendHandler
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel
import org.slf4j.LoggerFactory
import xyz.avarel.aria.Bot
import java.util.*

/**
 * Handles music playback for a specific guild.
 *
 * @param bot     [Bot] instance.
 * @param manager [MusicManager] instance.
 * @param player  An audio player that is capable of playing audio tracks
 *                and provides audio frames from the currently playing track.
 * @param guild   [Guild] instance.
 * @author        Avarel
 */
class MusicController(
        val bot: Bot,
        val manager: MusicManager,
        val player: AudioPlayer,
        val guild: Guild
) {
    companion object {
        val LOG = LoggerFactory.getLogger(MusicController::class.java)!!
//        val defaultTimescale = TimescaleSettings()
    }

    init {
        LOG.debug("Created a music controller for $guild.")

        bot.store[guild.id, "music", "volume"].getInt()?.let { volume ->
            player.volume = volume
        }
    }

    /**
     * @return Interface that handles track scheduling, music queue, and repeat options.
     */
    val scheduler: TrackScheduler = TrackScheduler(this).also(player::addListener)

    /**
     * @return Interface used to send audio to Discord through JDA.
     */
    private val sendHandler: AudioSendHandler = AudioPlayerSendHandler(player)

    /**
     * @return Voice channel the controller is connected to.
     */
    var channel: VoiceChannel? = null

    /**
     * @return True or false, depending on if the bot is alone in a voice channel.
     *         null if the bot isn't in a voice channel.
     */
    val isAlone: Boolean? get() {
        return channel?.members?.let { it.size == 1 && it[0] == guild.selfMember }
    }

    /**
     * @return True if this controller is destroyed.
     */
    private var destroyed: Boolean = false

    /**
     * @return The delayed task that destroys this controller.
     */
    private var leaveJob: Job? = null

    /**
     * Destroy and remove from music registry.
     */
    fun destroy() {
        if (!destroyed) {
            LOG.debug("Destroying the music manager of $guild.")

            leaveJob = null
            close()
            scheduler.queue.clear()
            player.destroy()

            destroyed = true
            manager.destroy(guild.idLong)
        }
    }

    /**
     * Activate or deactivate the delayed task that destroys this controller.
     */
    fun autoDestroy(activate: Boolean) {
        if (activate) {
            if (leaveJob == null) {
                LOG.debug("Activate auto-destroy music controller for $guild.")
                leaveJob = GlobalScope.launch {
                    delay(30)
                    destroy()
                }
            }
        } else {
            if (leaveJob != null) {
                LOG.debug("Cancelled auto-destroy music controller for $guild.")
                leaveJob?.cancel()
                leaveJob = null
            }
        }
    }

    /**
     * Result of trying to [connect] to a voice channel.
     */
    enum class ConnectResult {
        /**
         * Successfully connected to the voice channel.
         */
        SUCCESS,

        /**
         * Could not connect to the voice channel because of the user
         * limit and lacked the [Permission.VOICE_MOVE_OTHERS].
         */
        USER_LIMIT,

        /**
         * Insufficient permission ([Permission.VOICE_CONNECT]) to join the channel.
         */
        NO_PERMISSION
    }

    /**
     * Connect to a [VoiceChannel].
     *
     * @return [ConnectResult] Result of the connection attempt.
     */
    fun connect(channel: VoiceChannel): ConnectResult {
        LOG.debug("Attempting to connect to $guild :: $channel.")
        return when {
            destroyed -> throw IllegalStateException("Music manager is destroyed")
            !guild.selfMember.hasPermission(channel, Permission.VOICE_CONNECT) -> {
                LOG.debug("Can not connect to $guild :: $channel because no permission.")
                ConnectResult.NO_PERMISSION
            }
            channel.userLimit != 0
                    && guild.selfMember.hasPermission(channel, Permission.VOICE_MOVE_OTHERS)
                    && channel.members.size >= channel.userLimit -> {
                LOG.debug("Can not connect to $guild :: $channel because it is full.")
                ConnectResult.USER_LIMIT
            }
            else -> {
                guild.audioManager.sendingHandler = sendHandler
                guild.audioManager.openAudioConnection(channel)
                this.channel = channel
                LOG.debug("Successfully connected to $guild :: $channel.")
                ConnectResult.SUCCESS
            }
        }
    }

    /**
     * Close the current audio connection to the [VoiceChannel].
     */
    fun close() {
        LOG.debug("Closed audio connection to $guild.")
        guild.let {
            it.audioManager.closeAudioConnection()
            it.audioManager.sendingHandler = null
            channel = null
        }
    }

    /**
     * Volume of the music player.
     */
    var volume: Int
        get() = player.volume
        set(value) { player.volume = value }

    /**
     * Repeat mode of the player. [RepeatMode]
     */
    var repeatMode: RepeatMode
        get() = scheduler.repeatMode
        set(value) { scheduler.repeatMode = value }

    val queue: Deque<AudioTrack>
        get() = scheduler.queue

    fun skip() {
        scheduler.nextTrack()
    }
}