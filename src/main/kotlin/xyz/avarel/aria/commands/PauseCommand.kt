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
    aliases = ["pause"],
    description = "Pause the music player."
)
class PauseCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            requireMusic { controller ->
                requireTrack(controller) {
                    controller.player.isPaused = !controller.player.isPaused

                    context.channel.sendEmbed("Playback") {
                        desc {
                            when (controller.player.isPaused) {
                                true -> "Music playback has paused."
                                else -> "Music playback has resumed."
                            }
                        }
                    }.queue()
                }
            }
        }
    }
}