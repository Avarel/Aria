package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.aria.utils.requireMusic
import xyz.avarel.aria.utils.requireTrack
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
    aliases = ["skip"],
    description = "Skip the current track."
)
class SkipCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            requireMusic { controller ->
                requireTrack(controller) { track ->
                    controller.scheduler.nextTrack()

                    context.channel.sendEmbed("Skip") {
                        desc { "Skipped **${track.info.title}**." }
                    }.queue()
                }
            }
        }
    }
}