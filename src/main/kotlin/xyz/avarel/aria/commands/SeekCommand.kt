package xyz.avarel.aria.commands

import xyz.avarel.aria.*
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration

class SeekCommand : Command<MessageContext> {
    override val aliases = arrayOf("seek", "jump")

    override val info = CommandInfo(
            "Seek Command",
            Description(
                    "Seek to a specific time within the track."
            ),
            Usage(Argument.Specific("start")),
            Usage(Argument.Specific("beginning")),
            Usage(
                    Argument.Options(
                            Argument.Specific("plus"),
                            Argument.Specific("forward"),
                            Argument.Specific("+")
                    ),
                    Argument.Timestamp
            ),
            Usage(
                    Argument.Options(
                            Argument.Specific("minus"),
                            Argument.Specific("backward"),
                            Argument.Specific("-")
                    ),
                    Argument.Timestamp
            )
    )

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)
        val track = controller.player.playingTrack ?: return requirePlayingTrackMessage(context)

        if (context.args.isEmpty()) {
            return insufficientArgumentsMessage(context, "timestamp `hh:mm:ss`")
        }

        val current = Duration.ofMillis(track.position)

        val duration = when (context.args[0]) {
            "start", "beginning" -> Duration.ofSeconds(0)
            "+", "plus", "forward" -> Duration.ofMillis(track.position) + (extractDuration(1, context) ?: return)
            "-", "minus", "backward" -> Duration.ofMillis(track.position) - (extractDuration(1, context) ?: return)
            else -> extractDuration(0, context) ?: return
        }

        track.position = duration.toMillis()

        context.channel.sendEmbed("Playback") {
            desc { "Changed track playback position from `${current.formatDuration()}` to `${duration.formatDuration()}`." }
        }.queue()
    }

    private fun extractDuration(index: Int, context: MessageContext): Duration? {
        if (index >= context.args.size) {
            insufficientArgumentsMessage(context, "timestamp `hh:mm:ss`")
            return null
        }
        val duration = context.args[index].toDurationOrNull()
        if (duration != null) return duration
        context.channel.sendEmbed("Invalid Argument") {
            desc { "${context.args[index]} is not a valid timestamp `hh:mm:ss`." }
        }.queue()
        return null
    }
}