package xyz.avarel.aria.commands.music

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.errorMessage
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.aria.utils.requirePlayingTrackMessage
import xyz.avarel.core.commands.AnnotatedCommand
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
        aliases = ["pause"],
        title = "Pause Command",
        description = "Pause the music player."
)
class PauseCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val instance = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        if (instance.player.playingTrack == null) return context.requirePlayingTrackMessage()

        if (instance.isAlone) {
            return context.errorMessage("No one is in the music channel right now.")
        }

        instance.player.isPaused = !instance.player.isPaused

        context.channel.sendEmbed("Playback") {
            desc {
                when (instance.player.isPaused) {
                    true -> "Music playback has paused."
                    else -> "Music playback has resumed."
                }
            }
        }.queue()
    }
}