package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.MusicController
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration
import java.util.regex.Pattern

@CommandInfo(
        aliases = ["queue", "q"],
        description = "Show the music queue."
)
class QueueCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            requireMusic { controller ->
                context.channel.sendEmbed("Music Queue") {
                    descBuilder {
                        controller.scheduler.queue.forEachIndexed { index, audioTrack ->
                            append('`')
                            append(index + 1)
                            append("` `")
                            append(Duration.ofMillis(audioTrack.duration).formatDuration())
                            append("` **")
                            append(audioTrack.info.title)
                            append("**")
                            appendln()
                        }
                    }

                    field("Size", true) { controller.scheduler.queue.size.toString() }
                    field("Duration", true) {
                        val duration = controller.scheduler.duration
                        Duration.ofMillis(duration).formatDuration()
                    }
                    field("Repeat Mode", true) { controller.scheduler.repeatMode.toString() }

                    field("Now Playing") { controller.player.playingTrack?.info?.title ?: "Nothing" }
                }.queue()
            }
        }
    }
}