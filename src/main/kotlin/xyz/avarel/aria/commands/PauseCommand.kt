package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.aria.utils.requirePlayingTrackMessage
import xyz.avarel.core.commands.*

class PauseCommand : Command<MessageContext> {
    override val aliases = arrayOf("pause")

    override val info = info("Pause Command") {
        desc { "Pause the music player." }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)
        controller.player.playingTrack ?: return context.requirePlayingTrackMessage()

        controller.player.isPaused = !controller.player.isPaused

        context.channel.sendEmbed("Playback") {
            desc {
                when (controller.player.isPaused) {
                    true -> "Music playback has paused."
                    else -> "Music playback has resumed."
                }
            }
        }.queue()
    }
}