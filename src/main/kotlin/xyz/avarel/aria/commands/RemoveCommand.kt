package xyz.avarel.aria.commands

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.aria.utils.requireMusic
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
    aliases = ["remove", "rm"],
    description = "Remove tracks from the music queue."
)
class RemoveCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            requireMusic { controller ->
                val queue = controller.scheduler.queue
                if (queue.isEmpty()) {
                    context.channel.sendEmbed("Error") {
                        desc { "The queue is empty." }
                    }
                }
                match(arrayOf("first"), "Remove the first song of the queue.") {
                    remove(context, queue, listOf(queue.first()))
                }
                match(arrayOf("last"), "Remove last song of the queue.") {
                    remove(context, queue, listOf(queue.last()))
                }
                match(arrayOf("all"), "Clear the queue.") {
                    remove(context, queue, queue.map { it })
                }
                intInRange(1, queue.size, "Index of the track to remove.") { i ->
                    intInRange(i, queue.size, "Remove a range of tracks.") { j ->
                        remove(context, queue, queue.subList(i - 1, j).toList())
                    }
                    nothing("Remove one track.") {
                        remove(context, queue, listOf(queue[i - 1]))
                    }
                }
            }
        }
    }

    private fun remove(
        context: MessageContext,
        queue: MutableList<AudioTrack>,
        tracks: List<AudioTrack>
    ) {
        queue.removeAll(tracks)
        context.channel.sendEmbed("Music Queue") {
            desc {
                when (val size = tracks.size) {
                    0 -> "Removed nothing."
                    1 -> "Removed **${tracks[0].info.title}** from the queue."
                    else -> "Removed $size songs from the queue."
                }
            }
        }.queue()
    }
}