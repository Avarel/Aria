package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.aria.utils.requirePlayingTrackMessage
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["pause"],
        description = "Pause the music player."
)
class PauseCommand : AnnotatedCommand<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)
        controller.player.playingTrack ?: return requirePlayingTrackMessage(context)

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