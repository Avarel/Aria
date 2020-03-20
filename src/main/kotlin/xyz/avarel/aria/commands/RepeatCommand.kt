package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.RepeatMode
import xyz.avarel.aria.utils.dsl
import xyz.avarel.aria.utils.requireMusic
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["repeat", "r"],
        description = "Change the repeat mode of the music player."
)
class RepeatCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            requireMusic { controller ->
                enum<RepeatMode>("Repeat mode.") { mode ->
                    controller.scheduler.repeatMode = mode
                    context.channel.sendEmbed("Repeat") {
                        desc {
                            when (controller.scheduler.repeatMode) {
                                RepeatMode.NONE -> "The bot will not repeat songs."
                                RepeatMode.QUEUE -> "The bot will repeat the current queue."
                                RepeatMode.SONG -> "The bot will repeat the current song."
                            }
                        }
                    }.queue()
                }
            }
        }
    }
}