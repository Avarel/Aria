package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.RepeatMode
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.aria.utils.tryOrNull
import xyz.avarel.core.commands.*

class RepeatCommand : Command<MessageContext> {
    override val aliases = arrayOf("repeat")

    override val info = CommandInfo(
            "Queue Command",
            Description(
                    "Change the repeat mode."
            ),
            Usage(Argument.Specific("none")),
            Usage(Argument.Specific("song")),
            Usage(Argument.Specific("queue"))
    )

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