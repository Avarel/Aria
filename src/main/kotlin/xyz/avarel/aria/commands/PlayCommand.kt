package xyz.avarel.aria.commands

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.avarel.aria.music.TrackContext
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration

@CommandInfo(
        aliases = ["play", "p"],
        title = "Play Music Command",
        description = "Play some music.",
        usage = "(url|search)"
)
class PlayCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        context.parse {
            val query = expectString("(music name or URL)", "A music link or search query.", consumeRemaining = true)

            val isLink = "https://" in query
            val list = try {
                context.bot.musicManager.search(if (isLink) query else "ytsearch:$query", if (isLink) -1 else 1)
            } catch (e: FriendlyException) {
                return context.errorMessage("An exception occurred when searching: `${e.message}`")
            }

            if (list.isEmpty()) {
                context.channel.sendEmbed("No Results") {
                    desc { "YouTube returned no results for `$query`." }
                }.queue()
                return
            }

            list.forEach { it.userData = TrackContext(context.member, context.textChannel) }

            context.channel.sendEmbed {
                if (list.size == 1) {
                    val track = list[0]
                    setTitle(track.info.title, track.info.uri)
                    author { track.info.author }
                    image { track.thumbnail }
                } else {
                    title { "${list.size} Songs" }
                    descBuilder {
                        list.subList(0, Math.min(10, list.size)).forEachIndexed { i, track ->
                            append('`')
                            append(i + 1)
                            append("` • ")
                            append(track.info.title)
                            appendln()
                        }
                        if (list.size > 10) {
                            append("... and ${list.size - 10} more addedSongs.")
                        }
                    }
                }

                field("Duration", true) { Duration.ofMillis(list.sumByLong(AudioTrack::getDuration)).formatDuration() }
                field("Time Until Play", true) {
                    val duration = controller.scheduler.duration - (controller.player.playingTrack?.remainingDuration ?: 0)
                    Duration.ofMillis(duration).formatDuration()
                }

                field("Requester", true) { context.member.asMention }
                field("Requested Channel", true) { context.textChannel.asMention }

            }.await()

            list.forEach(controller.scheduler::offer)

            controller.setAutoDestroy(false)
        }
    }
}