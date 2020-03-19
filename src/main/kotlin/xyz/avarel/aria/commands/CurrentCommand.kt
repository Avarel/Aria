package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration

class CurrentCommand : Command<MessageContext> {
    override val aliases = arrayOf("current", "nowplaying", "np", "n")

    override val info = info("Now Playing Command") {
        desc { "Show currently playing music track." }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val track = controller.player.playingTrack ?: return requirePlayingTrackMessage(context)

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

                    progressBarTo(this, 20, track.position.toDouble() / track.duration.toDouble(), prefix = "`", suffix = "`")
                    append("\n`$position/$total`")
                }
                field("Time Remaining", true) {
                    Duration.ofMillis(track.duration - track.position).formatDuration()
                }
            }

            field("Volume", true) { "${controller.player.volume}%" }
            field("Repeat Mode", true) { controller.scheduler.repeatMode.toString() }

            field("Requester", true) { track.trackContext.requester.asMention }
            field("Requested Channel", true) { track.trackContext.requestChannel.asMention }

            image { track.thumbnail }
        }.queue()
    }
}