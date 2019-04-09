package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration

@CommandInfo(
        aliases = ["current", "nowplaying", "np"],
        title = "Now Playing Command",
        description = "Show currently playing music track."
)
class CurrentCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val instance = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val track = instance.player.playingTrack ?: return context.requirePlayingTrackMessage()

        context.channel.sendEmbed(track.info.title, track.info.uri) {
            author { track.info.author }

            if (track.info.isStream) {
                field("Stream") {
                    "\uD83D\uDD34 LIVE"
                }
            } else {
                fieldBuilder("Progress", true) {
                    val position = Duration.ofMillis(track.position).formatDuration()
                    val total = Duration.ofMillis(track.duration).formatDuration()

                    progressBarTo(this, 15, track.position.toDouble() / track.duration.toDouble(), prefix = "`", suffix = "`")
                    append("\n`$position/$total`")
                }
                field("Time Remaining", true) {
                    Duration.ofMillis(track.duration - track.position).formatDuration()
                }
            }

            field("Volume", true) { "${instance.player.volume}%" }
            field("Repeat Mode", true) { instance.scheduler.repeatMode.toString() }

            field("Requester", true) { track.trackContext.requester.asMention }
            field("Requested Channel", true) { track.trackContext.channel.asMention }

            image { track.thumbnail }
        }.queue()
    }
}