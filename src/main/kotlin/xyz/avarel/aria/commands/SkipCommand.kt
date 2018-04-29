package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.RepeatMode
import xyz.avarel.aria.requireMusicControllerMessage
import xyz.avarel.aria.requirePlayingTrackMessage
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["skip"],
        description = "Skip the current track."
)
class SkipCommand : AnnotatedCommand<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)
        val track = controller.player.playingTrack ?: return requirePlayingTrackMessage(context)

        controller.scheduler.nextTrack()

        context.channel.sendEmbed("Skip") {
            desc { "Skipped **${track.info.title}**." }
        }.queue()
    }
}