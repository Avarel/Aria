package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.aria.utils.requireMusic
import xyz.avarel.aria.utils.progressBarTo
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["volume", "v", "vol"],
        description = "Set the volume of the music player."

)
class VolumeCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            requireMusic { controller ->
                context.channel.sendEmbed("Volume") {
                    descBuilder {
                        append("\uD83D\uDD0A ")

                        intInRange(0, 150, "New volume.") { volume ->
                            val original = controller.player.volume
                            controller.player.volume = volume
                            context.bot.store[context.guild.id, "music", "volume"].setInt(volume)

                            progressBarTo(this@descBuilder, 15, controller.player.volume.toDouble() / 150.0, "`", "`")

                            append(" `")
                            append(original)
                            append("%` →")
                        }

                        nothing("View the current volume.") {
                            progressBarTo(this@descBuilder, 15, controller.player.volume.toDouble() / 150.0, "`", "`")
                        }

                        more { return@requireMusic }

                        append(" `")
                        append(controller.player.volume)
                        append("%`")
                    }
                }.queue()
            }
        }
    }


}