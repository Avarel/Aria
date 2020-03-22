package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.aria.utils.requireMusic
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
    aliases = ["clear", "clr"],
    description = "Clear the music queue."
)
class ClearCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            requireMusic { controller ->
                val size = controller.scheduler.queue.size
                controller.scheduler.queue.clear()

                context.channel.sendEmbed("Music Queue") {
                    desc {
                        when (size) {
                            0 -> "The queue was already empty."
                            1 -> "Cleared 1 song from the queue."
                            else -> "Cleared $size songs from the queue."
                        }
                    }
                }.queue()
            }
        }
    }
}