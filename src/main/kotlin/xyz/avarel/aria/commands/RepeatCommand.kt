package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.RepeatMode
import xyz.avarel.aria.requireMusicControllerMessage
import xyz.avarel.aria.tryOrNull
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["repeat"],
        description = "Change the repeat mode."
)
class RepeatCommand : AnnotatedCommand<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val repeat = context.args.firstOrNull()?.let {
            tryOrNull { RepeatMode.valueOf(it.toUpperCase()) }
        }

        if (repeat == null) {
            context.channel.sendEmbed("Invalid Argument") {
                descBuilder {
                    append("Valid repeat options are `")
                    RepeatMode.values().joinTo(this)
                    append("`.")
                }
            }.queue()
            return
        }

        controller.scheduler.repeatMode = repeat

        context.channel.sendEmbed("Repeat") {
            desc {
                when (controller.scheduler.repeatMode) {
                    RepeatMode.NONE -> "The bot will not repeat songs."
                    RepeatMode.QUEUE -> "The bot will repeat the playlist."
                    RepeatMode.SONG -> "The bot will repeat the current song."
                }
            }
        }.queue()
    }
}