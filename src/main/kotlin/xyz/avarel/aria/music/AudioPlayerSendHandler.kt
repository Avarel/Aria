package xyz.avarel.aria.music

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

/**
 * Interface used to send audio to Discord through JDA, specialized for LavaPlayer.
 * @param audioPlayer
 *        Audio player to wrap.
 */
class AudioPlayerSendHandler(private val audioPlayer: AudioPlayer) : AudioSendHandler {
    private var lastFrame = MutableAudioFrame().also {
        it.format = StandardAudioDataFormats.DISCORD_OPUS
        it.setBuffer(ByteBuffer.allocate(it.format.maximumChunkSize()))
    }

    override fun canProvide() = audioPlayer.provide(lastFrame)

    override fun provide20MsAudio() = ByteBuffer.wrap(lastFrame.data)!!

    override fun isOpus() = true
}