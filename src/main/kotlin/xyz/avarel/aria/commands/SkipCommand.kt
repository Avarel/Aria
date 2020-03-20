package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["skip"],
        description = "Skip the current track."
)
class SkipCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        dsl(context) {
            musicController { controller ->
                playingTrack(controller) { track ->
                    controller.scheduler.nextTrack()

                    context.channel.sendEmbed("Skip") {
                        desc { "Skipped **${track.info.title}**." }
                    }.queue()
                }
            }
        }
    }
}