package xyz.avarel.aria.commands

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.avarel.aria.music.MusicInstance
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration

@CommandInfo(
        aliases = ["queue", "q"],
        title = "Queue Command",
        description = "Show the music queue.",
        usage = "[#] | remove (#|start..end|first|last|all)"
)
class QueueCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        if (controller.queue.isEmpty()) {
            return context.errorMessage("The queue is empty.")
        }

        context.parse {
            when {
                nextMatch("Remove all tracks from the queue.", "clear", "clr") -> return clear(context, controller)
                nextMatch("Remove specific tracks from the queue.", "remove", "rm") -> return remove(context, controller)
            }

            val itemsPerPage = 10
            val pages = controller.queue.partition(itemsPerPage)

            val pg = if (hasNext()) {
                expectInt("Display the tracks queued on page `(number)`.")
            } else {
                optionalInt() ?: 1
            }.coerceIn(1, pages.size)

            val list = pages[pg - 1]

            context.channel.sendEmbed("Music Queue") {
                descBuilder {
                    list.forEachIndexed { i, track ->
                        append('`')
                        append((pg - 1) * itemsPerPage + i + 1)
                        append("` `")
                        append(Duration.ofMillis(track.duration).formatDuration())
                        append("` **")
                        append(track.info.title)
                        append("**")
                        appendln()
                    }
                }

                field("Size", true) { controller.queue.size.toString() }
                field("Duration", true) {
                    val duration = (controller.player.playingTrack?.remainingDuration ?: 0) - controller.scheduler.duration
                    Duration.ofMillis(duration).formatDuration()
                }
                field("Repeat Mode", true) { controller.scheduler.repeatMode.toString() }

                field("Now Playing") { controller.player.playingTrack?.info?.title ?: "Nothing" }

                footer { "Page $pg/${pages.size}" }
            }.queue()
        }
    }

    private fun clear(context: MessageContext, instance: MusicInstance) {
        val size = instance.queue.size
        if (size == 0) return context.errorMessage("Queue is empty.")

        instance.queue.clear()

        context.channel.sendEmbed("Music Queue") {
            desc { "Cleared all $size entries from the music queue." }
        }.queue()
    }

    private fun remove(context: MessageContext, instance: MusicInstance) {
        if (instance.queue.isEmpty()) return context.errorMessage("Queue is empty.")

        val queue = instance.queue

        context.parse {
            when {
                nextMatch("Remove the first track.", "first") -> notifyRemovedTrack(context, instance.queue.removeAt(0))
                nextMatch("Remove the last track.", "last") -> notifyRemovedTrack(context, instance.queue.removeAt(instance.queue.size - 1))
                nextMatch("Remove all tracks.", "all") -> return clear(context, instance)
                else -> {
                    matchInt("Index of the music track.", "(index)") { index ->
                        if (index !in 1..queue.size) {
                            return context.invalidArgumentsMessage("track number `1..${queue.size}`")
                        }
                        notifyRemovedTrack(context, instance.scheduler.remove(index - 1))
                    } || matchRange(description = "Remove all tracks from `low` to `high` positions.") { range ->
                        val low = range.start.coerceAtLeast(1)
                        val high = range.endInclusive.coerceAtMost(queue.size)

                        if (low > high) {
                            return context.errorMessage("The lower bound `$low` bound must not be greater than the upper bound `$high`.")
                        }

                        val list = (low..high).map { instance.scheduler.remove(low - 1) }

                        context.channel.sendEmbed("Remove ${list.size} Tracks") {
                            descBuilder {
                                list.subList(0, Math.min(10, list.size)).forEachIndexed { i, track ->
                                    append('`')
                                    append(low + i)
                                    append("` `")
                                    append(Duration.ofMillis(track.duration).formatDuration())
                                    append("` **")
                                    append(track.info.title)
                                    append("**")
                                    appendln()
                                }
                                if (list.size > 10) {
                                    append("... and ${list.size - 10} more addedSongs.")
                                }
                            }
                        }.queue()
                    } || matchError()
                }
            }
        }
    }

    private fun notifyRemovedTrack(context: MessageContext, track: AudioTrack) {
        context.channel.sendEmbed("Music Queue") {
            desc { "Removed **${track.info.title}** from the queue." }
        }.queue()
    }
}