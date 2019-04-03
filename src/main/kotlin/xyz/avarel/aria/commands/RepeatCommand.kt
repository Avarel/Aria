package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.RepeatMode
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.*

class RepeatCommand : Command<MessageContext> {
    override val aliases = arrayOf("repeat")

    override val info = info("Repeat Command") {
        desc { "Change the repeat mode of the music player." }
        usage {
            required {
                options<RepeatMode>()
            }
        }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val repeat = context.args.enum<RepeatMode>()
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