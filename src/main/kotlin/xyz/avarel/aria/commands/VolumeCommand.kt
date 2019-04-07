package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.progressBarTo
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.*

class VolumeCommand : Command<MessageContext> {
    override val aliases = arrayOf("volume", "v", "vol")

    override val info = info("Volume Command") {
        desc { "Set the volume of the music player." }
        usage {
            optional {
                range(0, 150)
            }
        }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val original = controller.player.volume

        context.parse {
            if (hasNext()) {
                val number = expectInt("The music player's new volume.")
                val volume = number.coerceIn(0, 150)
                controller.player.volume = volume
                context.bot.store[context.guild.id, "music", "volume"].setInt(volume)
            }

            context.channel.sendEmbed("Volume") {
                descBuilder {
                    append("\uD83D\uDD0A ")

                    progressBarTo(this, 15, controller.player.volume.toDouble() / 150.0, prefix = "`", suffix = "`")

                    if (original != controller.volume) {
                        append(" `")
                        append(original)
                        append("%` →")
                    }

                    append(" `")
                    append(controller.volume)
                    append("%`")
                }
            }.queue()
        }
    }
}