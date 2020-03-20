package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.aria.utils.musicController
import xyz.avarel.aria.utils.progressBarTo
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["volume", "v", "vol"],
        description = "Set the volume of the music player."
)
class VolumeCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        dsl(context) {
            musicController { controller ->
                val original = controller.player.volume

                intInRange(0, 150) { volume ->
                    controller.player.volume = volume
                    context.bot.store[context.guild.id, "music", "volume"].setInt(volume)
                }

                context.channel.sendEmbed("Volume") {
                    descBuilder {
                        append("\uD83D\uDD0A ")

                        progressBarTo(this, 15, controller.player.volume.toDouble() / 150.0, prefix = "`", suffix = "`")

                        if (original != controller.player.volume) {
                            append(" `")
                            append(original)
                            append("%` →")
                        }

                        append(" `")
                        append(controller.player.volume)
                        append("%`")
                    }
                }.queue()
            }
        }
    }
}