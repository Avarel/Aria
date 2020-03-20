package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.TrackContext
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration
import xyz.avarel.aria.music.MusicController.ConnectResult

@CommandInfo(
        aliases = ["play", "p"],
        description = "Play some music."
)
class PlayCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            string("(query | url)", "A search query or a music http URL.", true) { query ->
                val controller = join(context, false) ?: return

                val list = context.bot.musicManager.search(if ("https://" in query) query else "ytsearch:$query", 1)

                if (list.isEmpty()) {
                    context.channel.sendEmbed("No Results") {
                        desc { "YouTube returned no results for `$query`." }
                    }.queue()
                    return
                }

                val track = list[0]

                track.userData = TrackContext(context.member!!, context.textChannel)

                context.channel.sendEmbed(track.info.title, track.info.uri) {
                    setAuthor(track.info.author)

                    field("Duration", true) { Duration.ofMillis(track.duration).formatDuration() }
                    field("Time Until Play", true) {
                        val duration = (controller.player.playingTrack?.remainingDuration ?: 0) - controller.scheduler.duration
                        Duration.ofMillis(duration).formatDuration()
                    }

                    field("Requester", true) { track.trackContext.requester.asMention }
                    field("Requested Channel", true) { track.trackContext.requestChannel.asMention }

                    image { track.thumbnail }
                }.await()

                controller.scheduler.offer(list[0])
                controller.autoDestroy(false)
            }
        }
    }
}