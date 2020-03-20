package xyz.avarel.aria.commands

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration

@CommandInfo(
        aliases = ["seek", "jump", "scrub"],
        description = "Seek to a specific time within the track."
)
class SeekCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        dsl(context) {
            musicController { controller ->
                playingTrack(controller) { track ->
                    val current = Duration.ofMillis(track.position)
                    match("start", "beginning", desc = "Seek to the the start of the track.") {
                        setDuration(context, track, current, Duration.ofSeconds(0))
                    }
                    match("+", "plus", "forward", desc = "Increase the playback position.") {
                        time(desc = "The time to add to the playback position.") {
                            setDuration(context, track, current, current + it)
                        }
                    }
                    match("-", "minus", "backward", desc = "Decrease the playback position.") {
                        time(desc = "The time to subtract from the playback position.") {
                            setDuration(context, track, current, current - it)
                        }
                    }
                    time(desc = "Set the playback position to an exact time.") {
                        setDuration(context, track, current, it)
                    }
                }
            }
        }
    }

    private fun setDuration(context: MessageContext, track: AudioTrack, current: Duration, duration: Duration) {
        track.position = duration.toMillis()

        context.channel.sendEmbed("Playback") {
            desc { "Changed track playback position from `${current.formatDuration()}` to `${duration.formatDuration()}`." }
        }.queue()
    }
}