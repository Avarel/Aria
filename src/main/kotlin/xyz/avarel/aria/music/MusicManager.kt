package xyz.avarel.aria.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import xyz.avarel.aria.Bot
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Manage creation and handling of [MusicController] instances.
 *
 * @author Avarel
 */
class MusicManager(private val bot: Bot) {
    private val registry: MutableMap<Long, MusicController> = ConcurrentHashMap()

    /**
     * A factory that creates new [AudioPlayer] instances.
     */
    private val playerFactory: AudioPlayerManager = DefaultAudioPlayerManager().also {
        it.registerSourceManager(YoutubeAudioSourceManager().apply {
            configureRequests {
                RequestConfig.copy(it).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build()
            }
        })
        it.registerSourceManager(SoundCloudAudioSourceManager())
        it.registerSourceManager(BandcampAudioSourceManager())
        it.registerSourceManager(VimeoAudioSourceManager())
        it.registerSourceManager(TwitchStreamAudioSourceManager())
        it.registerSourceManager(BeamAudioSourceManager())
        it.registerSourceManager(HttpAudioSourceManager())
    }

    /**
     * Get a [MusicController] for the guild or create a new one
     * if it doesn't currently exist.
     *
     * @return A [MusicController] instance.
     * @throws IllegalStateException
     *         If the guild id doesn't exist in JDA.
     */
    fun getOrCreate(guildID: Long): MusicController {
        val guild = bot.shardManager.getGuildById(guildID)
        if (guild == null) {
            destroy(guildID)
            throw IllegalStateException("Guild $guildID doesn't exist.")
        }

        return registry.getOrPut(guildID) {
            MusicController(bot, this, playerFactory.createPlayer(), guild)
        }
    }

    /**
     * Get a [MusicController] for the guild if it currently exist.
     *
     * @param  guildID
     *         Guild id.
     * @return A [MusicController] instance if the registry currently
     *         have one for the guild id `null` otherwise.
     */
    fun getExisting(guildID: Long): MusicController? {
        val guild = bot.shardManager.getGuildById(guildID)
        if (guild == null) {
            destroy(guildID)
            return null
        }

        return registry[guildID]
    }

    /**
     * Destroy and remove a [MusicController] associated with a guild
     * id if it exists.
     *
     * @param guildID
     *        Guild id.
     */
    fun destroy(guildID: Long) {
        registry[guildID]?.destroy()
        registry.remove(guildID)
    }

    /**
     * Search for [AudioTrack] instances using LavaPlayer functionality.
     */
    suspend fun search(query: String, maxResults: Int = -1): List<AudioTrack> {
        return suspendCoroutine { cont ->
            playerFactory.loadItem(query, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    cont.resume(listOf(track))
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    cont.resume(playlist.tracks.let {
                        if (maxResults != -1) {
                            it.subList(0, Math.min(maxResults, playlist.tracks.size))
                        } else it
                    })
                }

                override fun noMatches() {
                    cont.resume(emptyList())
                }

                override fun loadFailed(e: FriendlyException) {
                    cont.resumeWithException(e)
                }
            })
        }
    }
}