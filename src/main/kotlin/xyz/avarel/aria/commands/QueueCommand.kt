package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.aria.utils.formatDuration
import xyz.avarel.aria.utils.remainingDuration
import xyz.avarel.aria.utils.requireMusic
import xyz.avarel.core.commands.*
import java.time.Duration

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
                            append(
                                Duration.ofMillis(audioTrack.duration)
                                    .formatDuration()
                            )
                            append("` **")
                            append(audioTrack.info.title)
                            append("**")
                            appendln()
                        }
                    }

                    field(
                        "Size",
                        true
                    ) { controller.scheduler.queue.size.toString() }
                    field("Duration", true) {
                        val duration =
                            controller.scheduler.duration + (controller.player.playingTrack?.remainingDuration
                                ?: 0)
                        Duration.ofMillis(duration).formatDuration()
                    }
                    field(
                        "Repeat Mode",
                        true
                    ) { controller.scheduler.repeatMode.toString() }

                    field("Now Playing") {
                        controller.player.playingTrack?.info?.title ?: "Nothing"
                    }
                }.queue()
            }
        }
    }
}