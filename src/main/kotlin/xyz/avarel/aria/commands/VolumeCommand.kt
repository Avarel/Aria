package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.progressBarTo
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.AnnotatedCommand
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.descBuilder
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
        aliases = ["volume", "v", "vol"],
        title = "Volume Command",
        description = "View or set the volume.",
        usage = "[1-150]"
)
class VolumeCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val original = controller.player.volume

        context.parse {
            if (hasNext()) {
                val number = expectInt("The music player's new volume.", "[1-150]")
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