package xyz.avarel.aria.commands

import xyz.avarel.aria.*
import xyz.avarel.aria.music.TrackContext
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration

@CommandInfo(
        aliases = ["play", "p"],
        description = "Play some music.",
        usage = "(url|search)"
)
class PlayCommand : AnnotatedCommand<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        if (context.args.isEmpty()) {
            return insufficientArgumentsMessage(context, "music name or URL")
        }

        val query = context.args.joinToString(" ")

        val list = context.bot.musicManager.search(if ("https://" in query) query else "ytsearch:$query", 1)

        if (list.isEmpty()) {
            context.channel.sendEmbed("No Results") {
                desc { "YouTube returned no results for `$query`." }
            }.queue()
            return
        }

        val track = list[0]

        track.userData = TrackContext(context.member, context.textChannel)

        context.channel.sendEmbed(track.info.title, track.info.uri) {
            setAuthor(track.info.author)

            field("Duration", true) { Duration.ofMillis(track.duration).formatDuration() }
            field("Time Until Play", true) {
                val duration = (controller.player.playingTrack?.remainingDuration ?: 0) - controller.scheduler.duration
                Duration.ofMillis(duration).formatDuration()
            }

            field("Requester", true) { track.trackContext.requester.asMention }
            field("Requested Channel", true) { track.trackContext.channel.asMention }

            image { track.thumbnail }
        }.await()

        controller.scheduler.offer(list[0])
        controller.autoDestroy(false)
    }
}