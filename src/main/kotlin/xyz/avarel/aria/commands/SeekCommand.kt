package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.formatDuration
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.aria.utils.requirePlayingTrackMessage
import xyz.avarel.core.commands.AnnotatedCommand
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed
import java.time.Duration

@CommandInfo(
        aliases = ["seek", "jump"],
        title = "Seek Command",
        description = "Seek to a specific time within the track",
        usage = "[+|plus|forward|-|minus|backward] [[hh:]mm:]ss | start"
)
class SeekCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)
        val track = controller.player.playingTrack ?: return context.requirePlayingTrackMessage()

        val current = Duration.ofMillis(track.position)

        context.parse {
            val duration = when {
                nextMatch("Move to the start of the song.", "start", "beginning") -> {
                    Duration.ofSeconds(0)
                }
                nextMatch("Move forward by a certain amount of time.", "+", "plus", "forward") -> {
                    current + expectDuration("Amount of time the player should move forward.")
                }
                nextMatch("Move backward by a certain amount of time.", "-", "minus", "backward") -> {
                    current - expectDuration("Amount of time the player should move backwards.")
                }
                else -> {
                    expectDuration("The exact time that the player should move to.")
                }
            }

            track.position = duration.toMillis()

            context.channel.sendEmbed("Playback") {
                desc { "Changed track playback position from `${current.formatDuration()}` to `${duration.formatDuration()}`." }
            }.queue()
        }
    }
}