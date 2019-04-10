package xyz.avarel.aria.commands.music

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.aria.utils.requirePlayingTrackMessage
import xyz.avarel.core.commands.AnnotatedCommand
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
        aliases = ["skip"],
        title = "Skip Song Command",
        description = "Skip the current song."
)
class SkipCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val instance = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)
        val track = instance.player.playingTrack ?: return context.requirePlayingTrackMessage()

        instance.skip()

        context.channel.sendEmbed("Skip") {
            desc { "Skipped **${track.info.title}**." }
        }.queue()
    }
}