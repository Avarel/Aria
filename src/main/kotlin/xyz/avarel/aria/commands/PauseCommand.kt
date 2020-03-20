package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.*
import xyz.avarel.core.commands.*

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