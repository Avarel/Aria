package xyz.avarel.aria.commands

import xyz.avarel.aria.music.RepeatMode
import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.AnnotatedCommand
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
        aliases = ["repeat"],
        title = "Repeat Mode Command",
        description = "Change the repeat mode of the music player."
)
class RepeatCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        context.parse {
            val repeat = expectEnum<RepeatMode>()
            controller.repeatMode = repeat

            context.channel.sendEmbed("Repeat") {
                desc {
                    when (controller.repeatMode) {
                        RepeatMode.NONE -> "The bot will not repeat songs."
                        RepeatMode.QUEUE -> "The bot will repeat the playlist."
                        RepeatMode.SONG -> "The bot will repeat the current song."
                    }
                }
            }.queue()
        }
    }
}