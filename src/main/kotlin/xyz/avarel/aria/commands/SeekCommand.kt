package xyz.avarel.aria.commands

import xyz.avarel.aria.*
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration

class SeekCommand : Command<MessageContext> {
    override val aliases = arrayOf("seek", "jump")

    override val info = info("Seek Command") {
        desc { "Seek to a specific time within the track." }
        usage {
            options {
                label("start")
                label("beginning")
            }
        }
        usage {
            options {
                label("plus")
                label("forward")
                label("+")
            }
            required { timestamp() }
        }
        usage {
            options {
                label("minus")
                label("backward")
                label("-")
            }
            required { timestamp() }
        }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)
        val track = controller.player.playingTrack ?: return requirePlayingTrackMessage(context)

        val current = Duration.ofMillis(track.position)

        val duration = when {
            context.args.nextIs("start", "beginning") -> Duration.ofSeconds(0)
            context.args.nextIs("+", "plus", "forward") -> Duration.ofMillis(track.position) + context.args.duration()
            context.args.nextIs("-", "minus", "backward") -> Duration.ofMillis(track.position) - context.args.duration()
            else -> context.args.duration()
        }

        track.position = duration.toMillis()

        context.channel.sendEmbed("Playback") {
            desc { "Changed track playback position from `${current.formatDuration()}` to `${duration.formatDuration()}`." }
        }.queue()
    }
}