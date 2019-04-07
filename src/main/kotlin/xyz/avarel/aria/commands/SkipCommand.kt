package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.aria.utils.requirePlayingTrackMessage
import xyz.avarel.core.commands.*

class SkipCommand : Command<MessageContext> {
    override val aliases = arrayOf("skip")

    override val info = info("Skip Command") {
        desc { "Skip the current track." }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)
        val track = controller.player.playingTrack ?: return context.requirePlayingTrackMessage()

        controller.skip()

        context.channel.sendEmbed("Skip") {
            desc { "Skipped **${track.info.title}**." }
        }.queue()
    }
}