package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.TrackContext
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*
import java.time.Duration
import xyz.avarel.aria.music.MusicController.ConnectResult

class PlayCommand : Command<MessageContext> {
    override val aliases = arrayOf("play", "p")

    override val info = info("Play Music Command") {
        desc { "Play some music." }
        usage {
            required {
                options {
                    url()
                    text()
                }
            }
        }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong) ?: kotlin.run {
            val vc = context.member?.voiceState?.channel
            if (vc == null) {
                context.channel.sendEmbed("No current voice channel.") {
                    desc { "This command requires you to be connected to a voice channel." }
                }.queue()
                return
            }

            val controller = context.bot.musicManager.createAndPut(context.guild.idLong)

            context.channel.sendEmbed {
                when (controller.connect(vc)) {
                    ConnectResult.SUCCESS -> {
                        title { "Joined Voice Channel" }
                        desc { "The bot has joined your voice channel `${vc.name}`." }
                    }
                    ConnectResult.USER_LIMIT -> {
                        title { "Voice channel is full." }
                        desc { "The voice channel `${vc.name}` is completely full." }
                    }
                    ConnectResult.NO_PERMISSION -> {
                        title { "No permission." }
                        desc { "The bot does not have permission to join `${vc.name}`." }
                    }
                }
            }.queue()

            controller
        }

        val query = context.args.string("music name or URL", consumeRemaining = true)

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