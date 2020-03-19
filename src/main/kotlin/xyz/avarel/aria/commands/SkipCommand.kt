package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.aria.utils.requirePlayingTrackMessage
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.info
import xyz.avarel.core.commands.sendEmbed

class SkipCommand : Command<MessageContext> {
    override val aliases = arrayOf("skip")

    override val info = info("Skip Command") {
        desc { "Skip the current track." }
    }

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