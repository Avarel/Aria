package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.MusicController
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration
import java.util.regex.Pattern

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