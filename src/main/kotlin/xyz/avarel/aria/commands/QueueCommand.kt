package xyz.avarel.aria.commands

import xyz.avarel.aria.*
import xyz.avarel.aria.music.MusicController
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration
import java.util.regex.Pattern

class QueueCommand : Command<MessageContext> {
    override val aliases = arrayOf("queue", "q")

    override val info = CommandInfo(
            "Queue Command",
            Description(
                    "Show the music queue.",
                    "This command can also remove specific songs in the queue."
            ),
            Usage(Argument.Optional(Argument.Number)),
            Usage(Argument.Specific("remove"), Argument.Options(
                    Argument.Number,
                    Argument.Specific("start..end"),
                    Argument.Specific("first"),
                    Argument.Specific("last")
            ))
    )

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        if (context.args.isNotEmpty()) {
            when (context.args[0]) {
                "clear", "clr" -> return clear(context, controller)
                "remove", "rm" -> return remove(context, controller)
            }
        }

        context.channel.sendEmbed("Music Queue") {
            descBuilder {
                controller.scheduler.queue.forEachIndexed { index, audioTrack ->
                    append('`')
                    append(index + 1)
                    append("` `")
                    append(Duration.ofMillis(audioTrack.duration).formatDuration())
                    append("` **")
                    append(audioTrack.info.title)
                    append("**\n")
                }
            }

            field("Size", true) { controller.scheduler.queue.size.toString() }
            field("Duration", true) {
                val duration = (controller.player.playingTrack?.remainingDuration ?: 0) - controller.scheduler.duration
                Duration.ofMillis(duration).formatDuration()
            }
            field("Repeat Mode", true) { controller.scheduler.repeatMode.toString() }

            field("Now Playing") { controller.player.playingTrack?.info?.title ?: "Nothing" }
        }.queue()
    }

    private fun clear(context: MessageContext, controller: MusicController) {
        val size = controller.scheduler.queue.size
        if (size == 0) return errorMessage(context, "Queue is empty.")

        controller.scheduler.queue.clear()

        context.channel.sendEmbed("Music Queue") {
            desc { "Cleared all $size entries from the music queue." }
        }.queue()
    }

    private val pattern = Pattern.compile("(\\d+)?\\s*?(?:\\.\\.|-)\\s*(\\d+)?")

    private fun remove(context: MessageContext, controller: MusicController) {
        if (controller.scheduler.queue.isEmpty()) return errorMessage(context, "Queue is empty.")

        if (context.args.size < 2) {
            return insufficientArgumentsMessage(context, "`#`, `first`, `last` or `all`")
        }

        val queue = controller.scheduler.queue

        val track = when (context.args[1]) {
            "first" -> controller.scheduler.queue.removeFirst()
            "last" -> controller.scheduler.queue.removeLast()
            "all" -> return clear(context, controller)
            else -> {
                val arg = context.args.subList(1, context.args.size).joinToString(" ")

                val matcher = pattern.matcher(arg)
                if (matcher.find()) {
                    val start = matcher.group(1).let {
                        if (it == null) 1
                        else try {
                            it.toInt().coerceAtLeast(1)
                        } catch (e: NumberFormatException) {
                            return invalidArgumentsMessage(context, "start of range")
                        }
                    }

                    val end = matcher.group(2).let {
                        if (it == null) queue.size
                        else try {
                            it.toInt().coerceAtMost(queue.size)
                        } catch (e: NumberFormatException) {
                            return invalidArgumentsMessage(context, "end of range")
                        }
                    }

                    for (i in start..end) {
                        controller.scheduler.remove(i - 1)
                    }

                    context.channel.sendEmbed("Remove Tracks") {
                        desc { "Removed track number `$start..$end` from the queue." }
                    }.queue()
                    return
                }

                val index = arg.toIntOrNull()

                if (index == null || index !in 1..queue.size) {
                    return invalidArgumentsMessage(context, "track number `1..${queue.size}`")
                }

                controller.scheduler.remove(index - 1)
            }
        }

        context.channel.sendEmbed("Music Queue") {
            desc { "Removed **${track.info.title}** from the queue." }
        }.queue()
    }
}